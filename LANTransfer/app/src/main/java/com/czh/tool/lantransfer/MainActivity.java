package com.czh.tool.lantransfer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

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
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {

    private AsyncHttpServer mAsyncHttpServer = new AsyncHttpServer();
    private AsyncServer mAsyncServer = new AsyncServer();
    private final FileUploadHolder mHolder = new FileUploadHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startServer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAsyncHttpServer != null) {
            mAsyncHttpServer.stop();
        }
        if (mAsyncServer != null) {
            mAsyncServer.stop();
        }
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.lan_get_ip_address_bt:
                String ip = NetworkUtils.getIPAddress(this);
                TextView ipTv = findViewById(R.id.lan_ip_address_tv);
                TextView noticeTv = findViewById(R.id.lan_notice);
                if (!TextUtils.isEmpty(ip)) {
                    ipTv.setText(ip);
                    String notice = noticeTv.getText().toString();
                    String newNotice = notice.replace("手机IP:54321", ip + ":54321");
                    noticeTv.setText(newNotice);
                }
                break;
        }
    }

    private void startServer() {
        mAsyncHttpServer.get(Constants.Url.RESOURCE_IMAGE,
                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
                    sendResources(request, response);
                });
        mAsyncHttpServer.get(Constants.Url.RESOURCE_SCRIPT,
                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
                    sendResources(request, response);
                });
        mAsyncHttpServer.get(Constants.Url.RESOURCE_CSS,
                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
                    sendResources(request, response);

                });
//        mAsyncHttpServer.get(Constants.Url.INDEX,
//                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
//                    try {
//                        response.send(getIndexContent());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        response.code(Constants.ErrorCode.SERVER_DENY).end();
//                    }
//                });

        mAsyncHttpServer.get(Constants.Url.INDEX,
                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
                    try {
                        response.send(getIndexContent());
                    } catch (IOException e) {
                        e.printStackTrace();
                        response.code(500).end();
                    }
                });

//        mAsyncHttpServer.get("/download/files",
//                (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
//                    JSONArray jsonArray = new JSONArray();
//                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Download");
//                    if (dir == null)
//                        return;
//                    String[] fileNames = dir.list();
//                    if (fileNames != null) {
//                        for (String fileName : fileNames) {
//                            File file = new File(dir, fileName);
//                            if (file.isFile() && file.exists()) {
////                          && fileName.endsWith("mp4")
//                                try {
//                                    JSONObject jsonObject = new JSONObject();
//                                    jsonObject.put("name", fileName);
//                                    jsonObject.put("path", file.getAbsolutePath());
//                                    jsonArray.put(jsonObject);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//                    response.send(jsonArray.toString());
//                });

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
                                jsonObject.put("name", fileName);
                                long fileLen = file.length();
                                jsonObject.put("size", NetworkUtils.getFileSize(fileLen));
                                array.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    response.send(array.toString());
                });

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
                    response.code(Constants.ErrorCode.NOT_FOUND).send("Not found!");

                }
        );


        //upload
        mAsyncHttpServer.post(Constants.Url.UPLOAD_FILE_CONTENT, (AsyncHttpServerRequest request, AsyncHttpServerResponse response) -> {
                    final MultipartFormDataBody body = (MultipartFormDataBody) request.getBody();
                    body.setMultipartCallback((Part part) -> {
                        if (part.isFile()) {
                            body.setDataCallback((DataEmitter emitter, ByteBufferList bb) ->{
                                    mHolder.write(bb.getAllByteArray());
                                    bb.recycle();
                            });
                        } else {
                            if (body.getDataCallback() == null) {
                                body.setDataCallback((DataEmitter emitter, ByteBufferList bb)-> {
                                        try {
                                            String fileName = URLDecoder.decode(new String(bb.getAllByteArray()), "UTF-8");
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


        mAsyncHttpServer.listen(mAsyncServer, 54321);
    }

    private void sendResources(final AsyncHttpServerRequest request,
                               final AsyncHttpServerResponse response) {
        try {
            String fullPath = request.getPath();
            fullPath = fullPath.replace("%20", " ");
            String resourceName = fullPath;
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1);
            }
            if (resourceName.indexOf("?") > 0) {
                resourceName = resourceName.substring(0, resourceName.indexOf("?"));
            }
            if (!TextUtils.isEmpty(NetworkUtils.getContentTypeByResourceName(resourceName))) {
                response.setContentType(NetworkUtils.getContentTypeByResourceName(resourceName));
            }
            BufferedInputStream bInputStream = new BufferedInputStream(getAssets().open(resourceName));
            response.sendStream(bInputStream, bInputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
            response.code(Constants.ErrorCode.NOT_FOUND).end();
            return;
        }
    }

//    private String getDownloadPageContent() throws IOException {
//        BufferedInputStream bInputStream = null;
//        try {
//            bInputStream = new BufferedInputStream(getAssets().open("download.html"));
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            int len = 0;
//            byte[] tmp = new byte[10240];
//            while ((len = bInputStream.read(tmp)) > 0) {
//                baos.write(tmp, 0, len);
//            }
//            return new String(baos.toByteArray(), "utf-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw e;
//        } finally {
//            if (bInputStream != null) {
//                try {
//                    bInputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private String getIndexContent() throws IOException {
        BufferedInputStream bInputStream = null;
        try {
            bInputStream = new BufferedInputStream(getAssets().open(Constants.INDEX_FILE_NAME));
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

    public class FileUploadHolder {
        private String fileName;
        private File receivedFile;
        private BufferedOutputStream fileOutPutStream;
        private long totalSize;


        public BufferedOutputStream getFileOutPutStream() {
            return fileOutPutStream;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
            totalSize = 0;
            if (!Constants.DIR.exists()) {
                Constants.DIR.mkdirs();
            }
            this.receivedFile = new File(Constants.DIR, this.fileName);
//            Timber.d(receivedFile.getAbsolutePath());
            try {
                fileOutPutStream = new BufferedOutputStream(new FileOutputStream(receivedFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void reset() {
            if (fileOutPutStream != null) {
                try {
                    fileOutPutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileOutPutStream = null;
        }

        public void write(byte[] data) {
            if (fileOutPutStream != null) {
                try {
                    fileOutPutStream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            totalSize += data.length;
        }
    }
}
