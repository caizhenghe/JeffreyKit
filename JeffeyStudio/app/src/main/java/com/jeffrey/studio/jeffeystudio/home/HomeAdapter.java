package com.jeffrey.studio.jeffeystudio.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeffrey.studio.jeffeystudio.R;
import com.jeffrey.studio.jeffeystudio.common.MultipleRecyclerViewAdapter;

public class HomeAdapter extends MultipleRecyclerViewAdapter<HomeAdapter.HomeHolder> {
    private Context mContext;
    public HomeAdapter(Context context){
        mContext = context;
    }


    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public int getViewType(int position) {
        return 1;
    }

    @Override
    public HomeHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_home_article, parent, false);
        HomeHolder holder = new HomeHolder(v);
        return holder;
    }

    @Override
    public void onBindCustomViewHolder(HomeHolder holder, int position) {
        holder.articleIdTv.setText("" + position);

    }

    class HomeHolder extends RecyclerView.ViewHolder {
        TextView articleIdTv;

        public HomeHolder(View itemView) {
            super(itemView);
            articleIdTv = itemView.findViewById(R.id.item_article_id);
        }
    }
}
