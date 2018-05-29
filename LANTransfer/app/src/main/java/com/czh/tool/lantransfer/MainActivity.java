package com.czh.tool.lantransfer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements FileTransferContract.View{

    private FileTransferPresenter mFileTransferPresenter;
    private boolean mIsActive = false, mAttachToWindow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFileTransferPresenter = new FileTransferPresenter(this);
        mFileTransferPresenter.startServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActive = true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachToWindow = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachToWindow = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFileTransferPresenter.stopServer();
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.lan_get_ip_address_bt:
                mFileTransferPresenter.getIpAddress();
                break;
        }
    }


    @Override
    public void showIpAddress(String ip) {
        TextView ipTv = findViewById(R.id.lan_ip_address_tv);
        if (!TextUtils.isEmpty(ip)) {
            ipTv.setText(ip);

        }
    }

    @Override
    public void updateHelp(String ip) {
        TextView noticeTv = findViewById(R.id.lan_notice);
        if (!TextUtils.isEmpty(ip)) {
            String notice = noticeTv.getText().toString();
            String newNotice = notice.replace("手机IP:", ip + ":");
            noticeTv.setText(newNotice);
        }
    }

    @Override
    public boolean isActive() {
        return mIsActive && mAttachToWindow;
    }
}
