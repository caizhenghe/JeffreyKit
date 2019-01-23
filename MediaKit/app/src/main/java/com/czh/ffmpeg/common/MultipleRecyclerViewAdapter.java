package com.czh.ffmpeg.common;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;


import java.util.List;

/**
 * Copyright (C), 2017, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * @author chenhao_y0153
 * @ClassName: MultipleRecyclerViewAdapter
 * @Description: 支持EmptyView和HeaderView的RecyclerViewAdapter
 * Version 1.0.0, 2017-09-14, chenhao_y0153 create file.
 */
public abstract class MultipleRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MultipleRecyclerViewAdapter.class.getSimpleName();

    private static final int TYPE_EMPTY = ViewProducer.VIEW_TYPE_EMPTY;
    private static final int TYPE_HEADER = ViewProducer.VIEW_TYPE_HEADER;
    private static final int TYPE_FOOTER = ViewProducer.VIEW_TYPE_FOOTER;

    protected ViewProducer mEmptyViewProducer;
    protected ViewProducer mHeaderViewProducer;
    protected ViewProducer mFooterViewProducer;
    protected boolean mIsEmpty;

    /******************
     * abstract methods
     ******************/

    public abstract int getCount();

    public abstract int getViewType(int positoin);

    public abstract VH onCreateCustomizeViewHolder(final ViewGroup parent, int viewType);

    public abstract void onBindCustomizeViewHolder(final VH holder, int position);

    protected void onBindCustomizeViewHolder(final VH holder, int position, List<Object> payloads) {
        onBindCustomizeViewHolder(holder, position);
    }

    /******************
     * external interface
     ******************/

    public boolean isEmpty() {
        return mIsEmpty;
    }

    public int getAdapterPosition(int listPosition) {
        if (mIsEmpty) {
            Log.e(TAG, "getAdapterPosition fail , list is empty");
        }
        if (mHeaderViewProducer != null) {
            listPosition++;
        }
        return listPosition;
    }

    public void setEmptyViewProducer(ViewProducer emptyViewProducer) {
        if (mEmptyViewProducer != emptyViewProducer) {
            mEmptyViewProducer = emptyViewProducer;
            if (mIsEmpty) {
                notifyDataSetChanged();
            }
        }
    }

    public boolean setHeaderViewProducer(ViewProducer emptyViewProducer) {
        if (mHeaderViewProducer != emptyViewProducer) {
            mHeaderViewProducer = emptyViewProducer;
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public boolean setFooterViewProducer(ViewProducer emptyViewProducer) {
        if (mFooterViewProducer != emptyViewProducer) {
            mFooterViewProducer = emptyViewProducer;
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    // 更新footer，避免整个页面刷新
    public boolean updateFooterViewProducer(ViewProducer emptyViewProducer) {
        if (mFooterViewProducer != emptyViewProducer) {
            mFooterViewProducer = emptyViewProducer;
            return true;
        }
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        int headerCarry = (mHeaderViewProducer == null) ? 0 : 1;
        if (headerCarry != 0 && position == 0) {
            return TYPE_HEADER;
        }
        int emptyViewCarry = (mIsEmpty && mEmptyViewProducer != null) ? 1 : 0;
        if (mFooterViewProducer != null && position == getCount() + headerCarry + emptyViewCarry) {
            return TYPE_FOOTER;
        }
        if (mIsEmpty) {
            return TYPE_EMPTY;
        }
        final int viewType = getViewType(position);
        if (viewType != TYPE_EMPTY && viewType != TYPE_FOOTER && viewType != TYPE_HEADER) {
            return viewType;
        } else {
            throw new IllegalStateException("getViewType conflicts with original TYPE_EMPTY : " + viewType);
        }
    }

    @Override
    public final int getItemCount() {
        int result = getCount();
        if (result == 0 && mEmptyViewProducer != null) {
            mIsEmpty = true;
            result++;
        } else {
            mIsEmpty = false;
        }
        if (mHeaderViewProducer != null) {
            result++;
        }
        if (mFooterViewProducer != null) {
            result++;
        }
        return result;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return mEmptyViewProducer.onCreateMyViewHolder(parent);
        } else if (viewType == TYPE_HEADER) {
            return mHeaderViewProducer.onCreateMyViewHolder(parent);
        } else if (viewType == TYPE_FOOTER) {
            return mFooterViewProducer.onCreateMyViewHolder(parent);
        } else {
            return onCreateCustomizeViewHolder(parent, viewType);
        }
    }

    @Override
    public final void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_EMPTY) {
            mEmptyViewProducer.onBindMyViewHolder(holder);
        } else if (holder.getItemViewType() == TYPE_HEADER) {
            mHeaderViewProducer.onBindMyViewHolder(holder);
        } else if (holder.getItemViewType() == TYPE_FOOTER) {
            mFooterViewProducer.onBindMyViewHolder(holder);
        } else {
            if (mHeaderViewProducer != null) {
                position--;
            }
            onBindCustomizeViewHolder((VH) holder, position);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (holder.getItemViewType() == TYPE_EMPTY) {
            mEmptyViewProducer.onBindMyViewHolder(holder);
        } else if (holder.getItemViewType() == TYPE_HEADER) {
            mHeaderViewProducer.onBindMyViewHolder(holder);
        } else if (holder.getItemViewType() == TYPE_FOOTER) {
            mFooterViewProducer.onBindMyViewHolder(holder);
        } else {
            if (mHeaderViewProducer != null) {
                position--;
            }
            onBindCustomizeViewHolder((VH) holder, position, payloads);
        }
    }
}
