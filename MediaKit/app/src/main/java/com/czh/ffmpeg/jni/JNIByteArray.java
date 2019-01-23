package com.czh.ffmpeg.jni;

import com.czh.ffmpeg.common.ByteArray;

import java.nio.ByteBuffer;

public class JNIByteArray implements ByteArray {
    private final long mJniPointer;
    /**whether the ByteArray in C is attached from external*/
    private final boolean mIsAttached;
    /**whether the buffer in ByteArray is attached from external*/
    private final boolean mIsBufferAttached;

    public JNIByteArray(long jniPointer) {
        mJniPointer = jniPointer;
        mIsAttached = true;
        mIsBufferAttached = true;
    }

    public JNIByteArray(int bufferSize) {
        this(0, bufferSize);
    }

    public JNIByteArray(long jniBufferPointer, int bufferSize) {
        mJniPointer = nativeConstruct(jniBufferPointer, bufferSize);
        mIsAttached = false;
        mIsBufferAttached = (jniBufferPointer != 0);
    }

    public long getJniPointer() {
        return mJniPointer;
    }

    public native long getBufferPointer();

    @Override
    public native int size();

    @Override
    public native int spaceAvailable();

    @Override
    public native int length();

    @Override
    public native int curPos();

    @Override
    public native long curBufferPos();

    @Override
    public native void advance(int length);

    @Override
    public native void seek(int offset);

    @Override
    public native void flush();

    @Override
    public native void convertToReadBuffer();

    @Override
    public native int putInt(int i);

    @Override
    public native int putLong(long l);

    @Override
    public native int putByte(byte b);

    @Override
    public int putBytes(ByteBuffer src) {
        int putLength = src.remaining();
        nativePutBytes(src, src.position(), putLength);
        src.position(src.limit());
        return putLength;
    }

    @Override
    public native int putString(String src);

    @Override
    public native int putDouble(double d);

    @Override
    public native int getInt();

    @Override
    public native long getLong();

    @Override
    public native byte getByte();

    @Override
    public ByteBuffer getBytes(ByteBuffer dst, int length) {
        nativeGetBytes(dst, dst.position(), length);
        dst.position(dst.position() + length);
        return dst;
    }

    @Override
    public native String getString();

    @Override
    public native double getDouble();

    public native int getObject(long objectPointer, int objectSize);

    public native int putObject(long objectPointer, int objectSize);

    private native static void nativeSetup();

    private native long nativeConstruct(long jniBufferPointer, int bufferSize);

    private native void nativeFinalize(boolean bufferAttached);

    private native void nativePutBytes(ByteBuffer buffer, int position, int remaining);

    private native void nativeGetBytes(ByteBuffer buffer, int position, int length);

    @Override
    protected void finalize() throws Throwable {
        try {
            if (!mIsAttached)
                nativeFinalize(mIsBufferAttached);
        } finally {
            super.finalize();
        }
    }

    static {
        System.loadLibrary("mediakit");
        nativeSetup();
    }
}
