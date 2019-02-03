package com.tplink.sdk.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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

public class AIDLService extends Service {
    private static final String TAG = AIDLService.class.getSimpleName();
    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList= new CopyOnWriteArrayList<>();
    private IBinder mBinder = new IBookManager.Stub() {

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (!mListenerList.contains(listener))
                mListenerList.add(listener);
            else
                Log.d(TAG, "Listener already exist!");
            Log.d(TAG, "registerListener: size = " + mListenerList.size());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (!mListenerList.contains(listener))
                mListenerList.remove(listener);
            else
                Log.d(TAG, "Listener not found, unregister fail!");
            Log.d(TAG, "unregisterListener: size = " + mListenerList.size());
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "server service onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "Java"));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceDestroyed.set(true);
    }

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            // do background processing
            while (!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "NewBook#" + bookId);
                mBookList.add(newBook);
                try {
                    for (IOnNewBookArrivedListener listener : mListenerList) {
                        listener.onBookArrived(newBook);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
