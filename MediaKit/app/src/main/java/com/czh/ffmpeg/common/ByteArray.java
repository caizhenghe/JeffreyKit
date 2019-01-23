package com.czh.ffmpeg.common;

import java.nio.ByteBuffer;

/**
 * byte array wraps an array, and provides convenient methods to put/get various types of variables.
 * byte array can be used to read or write, but we can't read and write a byte array in the same time.
 * byte array structure: <br>
 * 00 00 00 01 02 03 04 05 00 00 00
 *          |           |        |
 *        curPos       end      size  <br>
 * curPos is the start of valid data.
 * end is the end of valid data.
 * size is the array's allcated length.
 * the data between curPos and end is valid data, we can safely read them or write there.
 * when we call get/put method, curPos will move forward a length of the get/put variable.
 */
public interface ByteArray {
    int SIZE_OF_BYTE = 1;
    int SIZE_OF_INT = 4;
    int SIZE_OF_LONG = 8;
    int SIZE_OF_DOUBLE = 8;

    /**
     * @return size
     */
    int size();

    /**
     * @return size - curPos
     */
    int spaceAvailable();

    /**
     * @return end - curPos
     */
    int length();

    /**
     * @return curPos, starting from 0
     */
    int curPos();

    /**
     * @return curBufferPos, the pointer of curPos,
     * starting from the pointer of the ByteArray Buffer
     */
    long curBufferPos();

    /**
     * curPos move forward length
     */
    void advance(int length);

    /**
     * move curPos to offset value
     */
    void seek(int offset);

    /**
     * move curPos to the start position
     */
    void flush();

    /**
     * call this method after finish writing, then we can safely read
     */
    void convertToReadBuffer();

    /**
     * this set of methods will put a variable into the buffer, and return the put length
     */
    int putInt(int i);
    int putLong(long l);
    int putByte(byte b);
    int putBytes(ByteBuffer src);
    int putString(String src);
    int putDouble(double d);

    int getInt();
    long getLong();
    byte getByte();
    ByteBuffer getBytes(ByteBuffer dst, int length);
    String getString();
    double getDouble();
}
