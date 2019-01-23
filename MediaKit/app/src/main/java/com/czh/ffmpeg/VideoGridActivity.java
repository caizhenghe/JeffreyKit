package com.czh.ffmpeg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Copyright (C), 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * @author caizhenghe
 * @ClassName: VideoGridActivity
 * @Description: Version 1.0.0, 2019-01-23, caizhenghe create file.
 */

public class VideoGridActivity extends AppCompatActivity {
    private RecyclerView mRv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_grid);
        VideoAdapter adapter = new VideoAdapter();
        mRv = findViewById(R.id.video_grid_rv);
        mRv.setLayoutManager(new GridLayoutManager(this, 3));
        mRv.setAdapter(adapter);
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, VideoGridActivity.class);
        context.startActivity(intent);
    }
}
