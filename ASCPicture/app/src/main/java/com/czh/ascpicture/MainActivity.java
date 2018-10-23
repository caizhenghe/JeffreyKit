package com.czh.ascpicture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final int ALBUM_REQ_ID = 100;

    private boolean mIsAdd;
    private Bitmap mOutBitmap;

    private ImageView mImageView, mAddIv;

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
                break;
            case R.id.marie_iv:
                mIsAdd = false;
                updateImageView(null);
                mOutBitmap = null;
                Toast.makeText(this, "图片已删除", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, ALBUM_REQ_ID);
    }

    private void showBitmap(final Bitmap originBitmap) {
        String testPath = ROOT_PATH + "/DCIM/m8.jpg";
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


}
