// IOnNewBookArrivedListener.aidl
package com.tplink.sdk.aidlclient.aidl;
import com.tplink.sdk.aidlclient.aidl.Book;


interface IOnNewBookArrivedListener {
    void onBookArrived(in Book newBook);
}
