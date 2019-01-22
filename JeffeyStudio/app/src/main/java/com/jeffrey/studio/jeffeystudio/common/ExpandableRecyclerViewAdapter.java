package com.jeffrey.studio.jeffeystudio.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Copyright (C), 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * 支持二级列表的RecyclerView
 *
 * 不继承{@link MultipleRecyclerViewAdapter}, 避免过度封装
 * @author caizhenghe
 * @ClassName: ExpandableRecyclerViewAdapter
 * @Description: Version 1.0.0, 2019-01-22, caizhenghe create file.
 */

public abstract class ExpandableRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_EMPTY = ViewHolderProducer.VIEW_TYPE_EMPTY;
    private static final int TYPE_HEADER = ViewHolderProducer.VIEW_TYPE_HEADER;
    private static final int TYPE_FOOTER = ViewHolderProducer.VIEW_TYPE_FOOTER;
    private static final int TYPE_GROUP = 1;
    private static final int TYPE_CHILD = 2;


    private boolean mIsEmpty;
    private ViewHolderProducer mEmptyViewProducer;
    private ViewHolderProducer mHeaderViewProducer;
    private ViewHolderProducer mFooterViewProducer;

    /******************
     * Abstract methods
     ******************/

    protected abstract int getCount();

    protected abstract int getViewType(int position);

    protected abstract VH onCreateCustomViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindCustomViewHolder(VH holder, int position);

    /******************
     * External methods
     ******************/
    public void setEmptyViewProducer(ViewHolderProducer emptyView) {
        if (mEmptyViewProducer != emptyView) {
            mEmptyViewProducer = emptyView;
            notifyDataSetChanged();
        }
    }

    public void setHeaderViewProducer(ViewHolderProducer header) {
        if (mHeaderViewProducer != header) {
            mHeaderViewProducer = header;
            notifyDataSetChanged();
        }
    }

    public void setFooterViewProducer(ViewHolderProducer footer) {
        if (mFooterViewProducer != footer) {
            mFooterViewProducer = footer;
        }
    }


    @Override
    public int getItemViewType(int position) {
        int emptyViewCarry = (mIsEmpty && mEmptyViewProducer != null) ? 1 : 0;
        if (emptyViewCarry != 0) {
            return TYPE_EMPTY;
        }
        int headerViewCarry = mHeaderViewProducer == null ? 0 : 1;
        if (headerViewCarry != 0 && position == 0) {
            return TYPE_HEADER;
        }
        if (mFooterViewProducer != null && position == getCount() + emptyViewCarry + headerViewCarry) {
            return TYPE_FOOTER;
        }

        final int viewType = getViewType(position);
        if (viewType != TYPE_EMPTY && viewType != TYPE_HEADER && viewType != TYPE_FOOTER) {
            return viewType;
        } else {
            throw new IllegalStateException("getViewType conflicts with base type: " + viewType);
        }
    }

    @Override
    public int getItemCount() {
        int result = getCount();
        if (result == 0 && mEmptyViewProducer != null) {
            result++;
            mIsEmpty = true;
        } else {
            mIsEmpty = false;
        }
        if (mHeaderViewProducer != null) result++;
        if (mFooterViewProducer != null) result++;

        return result;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_EMPTY) {
            holder = mEmptyViewProducer.onCreateViewHolder(parent, viewType);
        } else if (viewType == TYPE_HEADER) {
            holder = mHeaderViewProducer.onCreateViewHolder(parent, viewType);
        } else if (viewType == TYPE_FOOTER) {
            holder = mFooterViewProducer.onCreateViewHolder(parent, viewType);
        } else {
            holder = onCreateCustomViewHolder(parent, viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_EMPTY) {
            mEmptyViewProducer.onBindViewHolder(holder, position);
        } else if (viewType == TYPE_HEADER) {
            mHeaderViewProducer.onBindViewHolder(holder, position);
        } else if (viewType == TYPE_FOOTER) {
            mFooterViewProducer.onBindViewHolder(holder, position);
        } else {
            if(mHeaderViewProducer != null)
                position--;
            onBindCustomViewHolder((VH)holder, position);
        }
    }

}
