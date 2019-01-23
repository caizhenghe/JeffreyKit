package com.czh.ffmpeg;

import android.opengl.GLSurfaceView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.czh.ffmpeg.common.MultipleRecyclerViewAdapter;

/**
 * Copyright (C), 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * @author caizhenghe
 * @ClassName: VideoAdapter
 * @Description: Version 1.0.0, 2019-01-23, caizhenghe create file.
 */

public class VideoAdapter extends MultipleRecyclerViewAdapter<VideoAdapter.VideoHolder> {
    @Override
    public int getCount() {
        return 200;
    }

    @Override
    public int getViewType(int positoin) {
        return 0;
    }

    @Override
    public VideoHolder onCreateCustomizeViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, parent, false);
        VideoHolder vh = new VideoHolder(item);
        vh.surface = item.findViewById(R.id.video_item_surface_view);
        return vh;
    }

    @Override
    public void onBindCustomizeViewHolder(VideoHolder holder, int position) {

    }

    class VideoHolder extends RecyclerView.ViewHolder {
        GLSurfaceView surface;
        public VideoHolder(View itemView) {
            super(itemView);
        }
    }
}
