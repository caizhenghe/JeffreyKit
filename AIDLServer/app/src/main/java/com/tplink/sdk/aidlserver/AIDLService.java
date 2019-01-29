package com.tplink.sdk.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tplink.sdk.aidlclient.aidl.Book;
import com.tplink.sdk.aidlclient.aidl.IBookManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * @author caizhenghe
 * @ClassName: AIDLService
 * @Description: Version 1.0.0, 2019-01-29, caizhenghe create file.
 */

public class AIDLService extends Service {
    private static final String TAG = AIDLService.class.getSimpleName();
    private List<Book> mBookList;
    private IBinder mBinder = new IBookManager.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "server service onBind");
        mBookList = new ArrayList<>();
        return mBinder;
    }
}
