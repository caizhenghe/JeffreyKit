package com.czh.tool.lantransfer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

public class FileTransferPresenter implements FileTransferContract.Presenter {

    private Context mContext;
    private FileTransferContract.View mTransferFileView;
    private AsyncHttpServer mAsyncHttpServer = new AsyncHttpServer();
    private AsyncServer mAsyncServer = new AsyncServer();
    private final FileTransferHolder mHolder = new FileTransferHolder();

    public FileTransferPresenter(@NonNull FileTransferContract.View view) {
        mTransferFileView = Objects.requireNonNull(view);
        mContext = (Context) view;
    }

    @Override
    public void getIpAddress() {
        String ip = NetworkUtils.getIPAddress(mContext);
        if (mTransferFileView.isActive()) {
            mTransferFileView.showIpAddress(ip);
            mTransferFileView.updateHelp(ip);
        }
    }

    @Override
    public void startServer() {
        // set resources
        mAsyncHttpServer.get(Constants.Url.RESOURCE_IMAGE,
                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> sendResources(request, response));
        mAsyncHttpServer.get(Constants.Url.RESOURCE_SCRIPT,
                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> sendResources(request, response));
        mAsyncHttpServer.get(Constants.Url.RESOURCE_CSS,
                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> sendResources(request, response));
        // index
        mAsyncHttpServer.get(Constants.Url.INDEX,
                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
                    try {
                        response.send(getIndexContent());
                    } catch (IOException e) {
                        e.printStackTrace();
                        response.code(Constants.ErrorCode.SERVER_ERROR).end();
                    }
                });

        //file list
        mAsyncHttpServer.get(Constants.Url.FILE_LIST,
                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
                    JSONArray array = new JSONArray();
                    File dir = Constants.DIR;
                    if (!dir.exists() || !dir.isDirectory())
                        return;
                    String[] fileNames = dir.list();
                    if (fileNames == null)
                        return;
                    for (String fileName : fileNames) {
                        File file = new File(dir, fileName);
                        if (file.exists() && file.isFile()) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(Constants.JsonKey.FILE_NAME, fileName);
                                long fileLen = file.length();
                                jsonObject.put(Constants.JsonKey.FILE_SIZE, NetworkUtils.getFileSize(fileLen));
                                array.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    response.send(array.toString());
                });
        // download
        mAsyncHttpServer.get(Constants.Url.DOWNLOAD_FILE_CONTENT,
                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
                    FileInputStream is = null;
                    String filePath = request.getPath().replace(Constants.Url.FILE_LIST + File.separator, "");
                    try {
                        filePath = URLDecoder.decode(filePath, Constants.ENCODE_UTF8);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    File file = new File(filePath);
                    if (file.isFile() && file.exists()) {
                        try {
                            is = new FileInputStream(filePath);
                            response.sendStream(is, is.available());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    response.code(Constants.ErrorCode.NOT_FOUND).send(mContext.getResources().getString(R.string.error_not_found_content));

                }
        );

        //upload
        mAsyncHttpServer.post(Constants.Url.UPLOAD_FILE_CONTENT, (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
                    final MultipartFormDataBody body = (MultipartFormDataBody) request.getBody();
                    body.setMultipartCallback((Part part) -> {
                        if (part.isFile()) {
                            body.setDataCallback((DataEmitter emitter, ByteBufferList bb) -> {
                                mHolder.write(bb.getAllByteArray());
                                bb.recycle();
                            });
                        } else {
                            if (body.getDataCallback() == null) {
                                body.setDataCallback((DataEmitter emitter, ByteBufferList bb) -> {
                                    try {
                                        String fileName = URLDecoder.decode(new String(bb.getAllByteArray()), Constants.ENCODE_UTF8);
                                        mHolder.setFileName(fileName);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    bb.recycle();
                                });
                            }
                        }
                    });
                    request.setEndCallback((Exception e) -> {
                        mHolder.reset();
                    });
                }
        );

        mAsyncHttpServer.listen(mAsyncServer, Constants.HTTP_PORT);
    }

    @Override
    public void stopServer() {
        if (mAsyncHttpServer != null) {
            mAsyncHttpServer.stop();
        }
        if (mAsyncServer != null) {
            mAsyncServer.stop();
        }
    }

    private void sendResources(final AsyncHttpServerRequest request,
                               final AsyncHttpServerResponse response) {
        try {
            String fullPath = request.getPath();
            fullPath = fullPath.replace("%20", " ");
            String resourceName = fullPath;
            if (resourceName.startsWith(File.separator)) {
                resourceName = resourceName.substring(1);
            }
            if (resourceName.indexOf("?") > 0) {
                resourceName = resourceName.substring(0, resourceName.indexOf("?"));
            }
            if (!TextUtils.isEmpty(NetworkUtils.getContentTypeByResourceName(resourceName))) {
                response.setContentType(NetworkUtils.getContentTypeByResourceName(resourceName));
            }
            BufferedInputStream bInputStream = new BufferedInputStream(mContext.getAssets().open(resourceName));
            response.sendStream(bInputStream, bInputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
            response.code(Constants.ErrorCode.NOT_FOUND).end();
            return;
        }
    }


    private String getIndexContent() throws IOException {
        BufferedInputStream bInputStream = null;
        try {
            bInputStream = new BufferedInputStream(mContext.getAssets().open(Constants.INDEX_FILE_NAME));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] tmp = new byte[10240];
            while ((len = bInputStream.read(tmp)) > 0) {
                baos.write(tmp, 0, len);
            }
            return new String(baos.toByteArray(), Constants.ENCODE_UTF8);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (bInputStream != null) {
                try {
                    bInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
