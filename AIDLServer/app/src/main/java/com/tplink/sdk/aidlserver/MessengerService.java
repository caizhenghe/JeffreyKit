package com.tplink.sdk.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tplink.sdk.aidlclient.aidl.Book;
import com.tplink.sdk.aidlclient.aidl.IBookManager;
import com.tplink.sdk.aidlclient.aidl.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (C), 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * @author caizhenghe
 * @ClassName: AIDLService
 * @Description: Version 1.0.0, 2019-01-29, caizhenghe create file.
 */

public class MessengerService extends Service {
    private static final String TAG = MessengerService.class.getSimpleName();

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    Log.d(TAG, "receive msg from client: " + msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;
                    Message reply = Message.obtain(null, 200);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "嗯，你的消息我已经收到了，稍后回复你。");
                    reply.setData(bundle);
                    try {
                        client.send(reply);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "server service onBind");
        return mMessenger.getBinder();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
