package com.czh.gradle.rvanalysis;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {
    List<People> mDataList = new ArrayList<>();
    private Context mContext;

    BaseAdapter(Context context) {
        mContext = context;
        for (int i = 0; i < 20; i++) {
            People person = new People().randomAppearance().randomBody().randomName();
            mDataList.add(person);
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_people, parent, false);
        BaseViewHolder holder = new BaseViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
//        holder.nameTv.setText(mDataList.get(position).getName());
//        holder.bodyTv.setText(mDataList.get(position).getBodyString());
//        holder.faceTv.setText(mDataList.get(position).getAppearanceString());
        int asc = (char) ((int)'A' + (int)(Math.random() * 26));
        holder.mTv.setText((char)asc + "");
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
//        TextView faceTv;
//        TextView nameTv;
//        TextView bodyTv;
        TextView mTv;

        public BaseViewHolder(View itemView) {
            super(itemView);
//            faceTv = itemView.findViewById(R.id.item_face_tv);
////            nameTv = itemView.findViewById(R.id.item_name_tv);
////            bodyTv = itemView.findViewById(R.id.item_body_tv);
            mTv = itemView.findViewById(R.id.name);
        }
    }
}
