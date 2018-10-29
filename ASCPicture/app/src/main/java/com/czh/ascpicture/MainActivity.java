package com.czh.ascpicture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {
    private static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String PIC_LIST_DIR = ROOT_DIR + "/DCIM/video/tmp/";
    private static final String VIDEO_DIR = ROOT_DIR + "/DCIM/video/";
    private static final String VIDEO_TEST_PATH = VIDEO_DIR + "test.mp4";
    private static final String VIDEO_OUT_DIR = ROOT_DIR + "/DCIM/video_out/";
    private static final int ALBUM_REQ_ID = 100;

    private boolean mIsAdd;
    private Bitmap mOutBitmap;
    private FILE_TYPE mFileType = FILE_TYPE.none;
    private MediaDecoder mMediaDecoder;
    private MyOnDecoderListener mDecoderListener;
    private Handler mHandler = new Handler();

    private ImageView mImageView, mAddIv;


    enum FILE_TYPE {
        none, pic, video
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.marie_iv);
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BitmapUtils.saveBitmapToSysAlbum(MainActivity.this, mOutBitmap);
                Toast.makeText(MainActivity.this, "图片已保存", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mAddIv = (ImageView) findViewById(R.id.add_iv);
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.add_iv:
                openSystemAlbum();

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < 100; i++) {
//                            Bitmap originBitmap = BitmapUtils.decodeBitmapFromPath(MainActivity.this, PIC_LIST_DIR + "0000" + i + ".jpeg");
//                            if (originBitmap != null) {
//                                Bitmap outBitmap = BitmapUtils.createAsciiPic(MainActivity.this, originBitmap);
//                                BitmapUtils.saveBitmapToSysAlbum(MainActivity.this, outBitmap);
//                            }
//                        }
//                    }
//                }).start();
                break;
            case R.id.marie_iv:
                mIsAdd = false;
                updateImageView(null);
                mOutBitmap = null;
                Toast.makeText(this, "图片已删除", Toast.LENGTH_SHORT).show();
                break;
            case R.id.decode_btn:
                mediaDecode(5);
                break;
            case R.id.encode_btn:
                mediaEncode("mp4", 25);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ALBUM_REQ_ID) {
            if (data != null) {
                try {
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    Bitmap originBitmap = BitmapUtils.decodeBitmapFromStream(this, is);
                    showBitmap(originBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void openSystemAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, ALBUM_REQ_ID);
    }

    private void showBitmap(final Bitmap originBitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mOutBitmap = BitmapUtils.createAsciiPic(MainActivity.this, originBitmap);
                mImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        mIsAdd = true;
                        updateImageView(mOutBitmap);

                    }
                });
            }
        }).start();
    }

    private void updateImageView(Bitmap bitmap) {
        if (mIsAdd) {
            mAddIv.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(bitmap);
        } else {
            mAddIv.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(null);
            mImageView.setVisibility(View.GONE);
        }
    }

    private void mediaDecode(int fps) {
        mMediaDecoder = new MediaDecoder(VIDEO_TEST_PATH);
        String videoFileLength = mMediaDecoder.getVideoDuration();
        int encodeTotalCount = 0;
        try {
            encodeTotalCount = Integer.valueOf(videoFileLength) / (1000 / fps);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        if (encodeTotalCount == 0) {
            return;
        }
        new DecodeThread(PIC_LIST_DIR, VIDEO_TEST_PATH, fps, mDecoderListener, MainActivity.this).start();
    }

    private class MyOnDecoderListener implements DecodeThread.OnDecoderListener {

        @Override
        public void onProgress(int progress) {
        }

        @Override
        public void onComplete() {
            Toast.makeText(MainActivity.this, "视频转换完成，请接下来进行视频拼接", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void showImg(Bitmap bitmapTemp) {
        }
    }

    private void mediaEncode(String format, int fps) {
        File file = new File(VIDEO_TEST_PATH);
        if (!file.exists()) return;
        String fileName = file.getName();
        int i = fileName.lastIndexOf(".");
        if (i == -1 || i == 0) {
            Toast.makeText(this, "媒体格式不对，无法进行拼接", Toast.LENGTH_SHORT).show();
            return;
        }
        final String videoName = fileName.substring(0, i) + "." + format;
        String[] commands = FFmpegCommandCenter.concatVideo(PIC_LIST_DIR, VIDEO_OUT_DIR + "marie.mp4", fps + "");
        final String[] _commands = commands;
        new Thread(new Runnable() {
            @Override
            public void run() {
                FFmpegKit.execute(_commands);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        mDecoderListener = null;
        super.onDestroy();
    }


}
