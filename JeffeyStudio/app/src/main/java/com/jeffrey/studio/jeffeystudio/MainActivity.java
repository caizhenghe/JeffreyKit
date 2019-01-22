package com.jeffrey.studio.jeffeystudio;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jeffrey.studio.jeffeystudio.bean.HomeArticleListWrapper;
import com.jeffrey.studio.jeffeystudio.common.Constant;
import com.jeffrey.studio.jeffeystudio.common.JsonUtils;
import com.jeffrey.studio.jeffeystudio.common.OkHttpUtils;
import com.jeffrey.studio.jeffeystudio.common.ViewHolderProducer;
import com.jeffrey.studio.jeffeystudio.home.HomeAdapter;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView mJsonTv;
    RecyclerView mArticleRv;
    HomeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mJsonTv = findViewById(R.id.home_json_tv);

        mArticleRv = findViewById(R.id.home_article_rv);
        mAdapter = new HomeAdapter(this);
        mAdapter.setEmptyViewProducer(new ViewHolderProducer() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_home_article_empty, parent, false);
                return new DefaultEmptyViewHolder(v);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }
        });
        mAdapter.setHeaderViewProducer(new ViewHolderProducer() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View header = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_home_article_header, parent, false);
                return new DefaultHeaderViewHolder(header);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }
        });

        mAdapter.setFooterViewProducer(new ViewHolderProducer() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View header = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_home_article_footer, parent, false);
                return new DefaultFooterViewHolder(header);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }
        });
        mArticleRv.setLayoutManager(new LinearLayoutManager(this));
        mArticleRv.setAdapter(mAdapter);

        //reqGetHomeArticleList();
        //reqPostLogin();

    }

    private void reqGetHomeArticleList() {
        OkHttpClient okHttpClient = OkHttpUtils.getClient();
        final Request request = new Request.Builder()
                .url(Constant.HOME_ARTICLE_LIST_URL + "/1/json")
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String body = response.body().string();
                HomeArticleListWrapper wrapper = JsonUtils.getObject(body, HomeArticleListWrapper.class);

                Log.d(TAG, "onResponse: " + wrapper.data);
                mJsonTv.post(new Runnable() {
                    @Override
                    public void run() {
                        mJsonTv.setText(body);
                    }
                });
            }
        });
    }

    private void reqPostLogin(){
        OkHttpClient okHttpClient = OkHttpUtils.getClient();
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        String content = "13006662432, 123456";
        RequestBody reqBody = RequestBody.create(mediaType, content);
        final Request request = new Request.Builder()
                .url(Constant.LOGIN_URL)
                .post(reqBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String body = response.body().string();
                HomeArticleListWrapper wrapper = JsonUtils.getObject(body, HomeArticleListWrapper.class);

                Log.d(TAG, "onResponse: " + wrapper.data);
                mJsonTv.post(new Runnable() {
                    @Override
                    public void run() {
                        mJsonTv.setText(body);
                    }
                });
            }
        });
    }


}
