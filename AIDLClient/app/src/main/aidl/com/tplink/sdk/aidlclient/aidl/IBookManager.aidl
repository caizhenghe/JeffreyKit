// IBookManager.aidl
package com.tplink.sdk.aidlclient.aidl;
import com.tplink.sdk.aidlclient.aidl.Book;
import com.tplink.sdk.aidlclient.aidl.IOnNewBookArrivedListener;


interface IBookManager {

    void addBook(in Book book);

    List<Book> getBookList();

    void registerListener(IOnNewBookArrivedListener listener);

    void unregisterListener(IOnNewBookArrivedListener listener);
}
