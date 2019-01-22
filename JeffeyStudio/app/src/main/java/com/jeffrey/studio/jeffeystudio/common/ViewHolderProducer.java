package com.jeffrey.studio.jeffeystudio.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public interface ViewHolderProducer {
    int VIEW_TYPE_EMPTY = Integer.MAX_VALUE;
    int VIEW_TYPE_HEADER = Integer.MAX_VALUE - 1;
    int VIEW_TYPE_FOOTER = Integer.MAX_VALUE - 2;

    RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

    class DefaultEmptyViewHolder extends RecyclerView.ViewHolder{

        public DefaultEmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    class DefaultHeaderViewHolder extends RecyclerView.ViewHolder{

        public DefaultHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class DefaultFooterViewHolder extends RecyclerView.ViewHolder{

        public DefaultFooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
