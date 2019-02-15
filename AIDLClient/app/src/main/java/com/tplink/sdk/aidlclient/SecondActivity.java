package com.tplink.sdk.aidlclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tplink.sdk.aidlclient.aidl.IBookManager;

/**
 * Copyright (C), 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * @author caizhenghe
 * @ClassName: SecondActivity
 * @Description: Version 1.0.0, 2019-02-15, caizhenghe create file.
 */

public class SecondActivity extends AppCompatActivity {
    static class ReplyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    Log.d("tag", "receive msg from Service: " + msg.getData().getString("reply"));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    }

    Messenger mMessenger, mReplyMessenger = new Messenger(new ReplyHandler());

    ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            Message msg = Message.obtain(null, 100);
            Bundle bundle = new Bundle();
            bundle.putString("msg", "hello, this is client.");
            msg.setData(bundle);
            msg.replyTo = mReplyMessenger;
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        ComponentName name = new ComponentName("com.tplink.sdk.aidlserver", "com.tplink.sdk.aidlserver.MessengerService");
        intent.setComponent(name);
        bindService(intent, mConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConn);
        super.onDestroy();
    }
}
