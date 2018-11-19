package com.czh.gradle.rvanalysis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRv = findViewById(R.id.rv);
        BaseAdapter adapter = new BaseAdapter(this);
        mRv.setAdapter(adapter);
//        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 3);
//        GridLayoutManager manager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false){
//            @Override
//            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(200, 300);
//                return params;
//            }
//        };
//        manager.setAutoMeasureEnabled(false);
//        mRv.setHasFixedSize(false);
        mRv.setLayoutManager(manager);
        // 添加Android默认分割线
        //mRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        // 自定义分割线
//        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        decoration.setDrawable(getResources().getDrawable(R.drawable.item_divider));
        // 万能分割线
        DividerGridItemDecoration decoration = new DividerGridItemDecoration(getResources().getDrawable(R.drawable.item_divider));
        mRv.addItemDecoration(decoration);
    }
}
