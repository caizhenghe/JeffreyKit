package com.czh.ffmpeg.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Copyright (C), 2017, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * @author chenhao_y0153
 * @ClassName: ViewProducer
 * @Description: <p>
 * Version 1.0.0, 2017-09-15, chenhao_y0153 create file.
 */
public interface ViewProducer {
    public static final int VIEW_TYPE_EMPTY = Integer.MAX_VALUE;
    public static final int VIEW_TYPE_HEADER = Integer.MAX_VALUE - 1;
    public static final int VIEW_TYPE_FOOTER = Integer.MAX_VALUE - 2;

    RecyclerView.ViewHolder onCreateMyViewHolder(ViewGroup parent);

    void onBindMyViewHolder(RecyclerView.ViewHolder holder);

    class DefaultEmptyViewHolder extends RecyclerView.ViewHolder {
        public DefaultEmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    class DefaultFootViewHolder extends RecyclerView.ViewHolder {
        public DefaultFootViewHolder(View itemView) {
            super(itemView);
        }
    }

    class DefaultHeaderViewHolder extends RecyclerView.ViewHolder {
        public DefaultHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
