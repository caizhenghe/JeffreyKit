package com.czh.ascpicture;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.text.style.MetricAffectingSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    Bitmap mBitmap;
    Bitmap mAscBitmap;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.marie_iv);
        new Thread(new Runnable() {
            @Override
            public void run() {
//                DisplayMetrics metrics = BitmapUtils.getSreenMethics(MainActivity.this);
//                mBitmap = BitmapUtils.decodeBitmapFromRes(getResources(), R.drawable.icon, metrics.widthPixels, metrics.heightPixels);
                mAscBitmap = BitmapUtils.createAsciiPic(getResources(), R.drawable.m9, MainActivity.this);
                mImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImageBitmap(mAscBitmap);
                    }
                });
            }
        }).start();
    }

    public void doClick(View v) {
        mImageView.setImageBitmap(mBitmap);
    }
}
