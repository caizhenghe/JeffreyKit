// IBookManager.aidl
package com.tplink.sdk.aidlclient.aidl;
import com.tplink.sdk.aidlclient.aidl.Book;


// Declare any non-default types here with import statements

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void addBook(in Book book);

    List<Book> getBookList();
}
