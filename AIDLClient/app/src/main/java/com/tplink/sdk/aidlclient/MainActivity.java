package com.tplink.sdk.aidlclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tplink.sdk.aidlclient.aidl.Book;
import com.tplink.sdk.aidlclient.aidl.IBookManager;
import com.tplink.sdk.aidlclient.aidl.IOnNewBookArrivedListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private IBookManager mIBookManager;
    private int mBookId = 0;
    private Handler mHandler = new Handler();

    IBinder.DeathRecipient Recipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mIBookManager == null)
                return;
            mIBookManager.asBinder().unlinkToDeath(Recipient, 0);
            mIBookManager = null;
            // 重新绑定远程service
            Intent intent = new Intent();
            ComponentName name = new ComponentName("com.tplink.sdk.aidlserver", "com.tplink.sdk.aidlserver.AIDLService");
            intent.setComponent(name);
            bindService(intent, mConn, BIND_AUTO_CREATE);
        }
    };
    ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBookManager = IBookManager.Stub.asInterface(service);
            try {
                mIBookManager.registerListener(mArriveListener);
                service.linkToDeath(Recipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                mIBookManager.unregisterListener(mArriveListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mIBookManager = null;

        }
    };

    IOnNewBookArrivedListener mArriveListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onBookArrived(Book newBook) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mIBookManager == null) return;
                    try {
                        List<Book> bookList = mIBookManager.getBookList();
                        TextView tv = findViewById(R.id.tv_book_list);
                        tv.setText(bookList.toString());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent();
        ComponentName name = new ComponentName("com.tplink.sdk.aidlserver", "com.tplink.sdk.aidlserver.AIDLService");
        intent.setComponent(name);
        bindService(intent, mConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_book:
                if (mIBookManager != null) {
                    mBookId++;
                    Book book = new Book();
                    book.bookId = mBookId;
                    book.bookName = "Book" + mBookId;
                    try {
                        mIBookManager.addBook(book);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_get_book_list:
                if (mIBookManager != null) {
                    try {
                        List<Book> bookList = mIBookManager.getBookList();
                        TextView tv = findViewById(R.id.tv_book_list);
                        tv.setText(bookList.toString());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
