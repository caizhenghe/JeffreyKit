package com.czh.ffmpeg.jni;

import android.util.Log;

import java.nio.ByteBuffer;

public class AVFrame {
    public final static int TPAVFRAME_FORMAT_I420 = 10;
    public final static int TPAVFRAME_FORMAT_NV12 = 11;
    public final static int TPAVFRAME_FORMAT_ARGB8888 = 12;
    public final static int TPAVFRAME_FORMAT_RGB888 = 13;
    public final static int TPAVFRAME_FORMAT_RGB565LE = 14;
    public final static int TPAVFRAME_FORMAT_S16 = 20;
    public final static int TPAVFRAME_FORMAT_FLTP = 21;

    private long mNativeAVFramePointer;
    /**
     * 表示C层TPAVFrame是否由外部绑定，当由外部绑定时，只是C层的一个镜像，不能对其frame(ffmpeg)进行ref、unref等操作
     */
    private boolean mIsAttached;

    public int type;
    public int format;
    public long pts;
    public long utc;
    public int ptsTimeScale;
    public int ready;
    public long time;
    public int timeScale;
    /**
     * for video
     */
    public int width;
    public int height;
    /**
     * for audio
     */
    public int samplingFrequency;
    public int numberOfBits;
    public int numberOfChannels;
    public int numberSamples;
    public int linesize;

    //this is filled in JNI Level by reflection
    public ByteBuffer[] yuvData;
    //this is filled in JNI Level by reflection
    public ByteBuffer audioStream;

    public AVFrame() {
        mNativeAVFramePointer = nativeConstruct();
        mIsAttached = false;
    }

    // constuct by jni
    public AVFrame(long nativeAVFramePointer, int type, int format, long pts, long utc, int ptsTimeScale,
                   long time, int timeScale, int ready, int width, int height,
                   int samplingFrequency, int numberOfBits, int numberOfChannels, int numberSamples) {
        this.mNativeAVFramePointer = nativeAVFramePointer;
        this.type = type;
        this.format = format;
        this.pts = pts;
        this.utc = utc;
        this.ptsTimeScale = ptsTimeScale;
        this.time = time;
        this.timeScale = timeScale;
        this.ready = ready;
        this.width = width;
        this.height = height;
        this.samplingFrequency = samplingFrequency;
        this.numberOfBits = numberOfBits;
        this.numberOfChannels = numberOfChannels;
        this.numberSamples = numberSamples;
        this.mIsAttached = true;
    }

    public boolean writeAudioStream(ByteBuffer streamBuffer) {
        if (audioStream == null) return false;
        audioStream.clear();
        audioStream.put(streamBuffer);
        return true;
    }

    public void syncToNative() {
        nativeSyncToNative(mNativeAVFramePointer);
    }

    public void syncFromNative() {
        nativeSyncFromNative(mNativeAVFramePointer);
    }

    public long getNativeAVFramePointer() {
        return mNativeAVFramePointer;
    }

    public boolean RefFrom(AVFrame srcFrame) {
        if (!mIsAttached) {
            nativeUnref(mNativeAVFramePointer);
            nativeRefFrom(srcFrame.getNativeAVFramePointer(), mNativeAVFramePointer);
            nativeSyncFromNative(mNativeAVFramePointer);
            return true;
        } else {
            return false;
        }
    }

    public void UnrefFrame() {
        if (!mIsAttached) {
            nativeUnref(mNativeAVFramePointer);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (!mIsAttached) {
                nativeFinalize(mNativeAVFramePointer);
                Log.d("AVFrame", "^^^finalize" + this);
            }
        } finally {
            super.finalize();
        }
    }

    private native long nativeConstruct();
    private native void nativeFinalize(long nativePointer);
    private native void nativeSyncToNative(long nativeAVFramePointer);
    private native void nativeSyncFromNative(long nativeAVFramePointer);
    private native void nativeRefFrom(long srcFramePointer, long nativePointer);
    private native void nativeUnref(long nativePointer);
}
