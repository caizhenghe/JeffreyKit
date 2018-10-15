package com.czh.tax;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by Administrator on 2018/10/15.
 */

public class MedicalRuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_rule);

        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl("http://www.szsi.gov.cn/sbjxxgk/tzgg/simtgg/201802/t20180205_10767179.htm");

        // 设置WebView属性，能够执行JavaScript脚本
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        // 为图片添加放大缩小功能
        webView.getSettings().setUseWideViewPort(true);

        webView.setInitialScale(50);   //100代表不缩放
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MedicalRuleActivity.class);
        context.startActivity(intent);
    }
}
