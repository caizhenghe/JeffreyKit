package com.czh.ffmpeg.jni;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.czh.ffmpeg.common.ByteArray;
import com.czh.ffmpeg.render.GLProgram;

public class JNIGLProgram implements GLProgram {
    public final static int GLPROGRAM_TYPE_I420 = 0;
    public final static int GLPROGRAM_TYPE_NV12 = 1;

    long mNativePointer;

    public JNIGLProgram(int type) {
        mNativePointer = nativeConstruct(type);
    }

    @Override
    protected void finalize() throws Throwable {
        nativeFinalize(mNativePointer);
        super.finalize();
    }

    @Override
    public void buildProgram() {
        nativeBuildProgram(0, 0, 0, mNativePointer);
    }

    @Override
    public void buildProgram(@NonNull JNIShaderBuildOption option) {
        nativeBuildProgram(option.getNativePointer(), 0, 0, mNativePointer);
    }

    @Override
    public void buildProgram(@NonNull JNIShaderBuildOption option, @NonNull ByteArray byteArray4DisplayParams) {
        nativeBuildProgram(option.getNativePointer(), ((JNIByteArray)byteArray4DisplayParams).getBufferPointer(),
               byteArray4DisplayParams.size(), mNativePointer);
    }

    @Override
    public void buildTextures(AVFrame frame) {
        nativeBuildTextures(frame.getNativeAVFramePointer(), mNativePointer);
    }

    @Override
    public int drawFrame() {
        return nativeDrawFrame(mNativePointer);
    }

    @Override
    public void setScaleMode(int mode) {
        nativeSetScaleMode(mode, mNativePointer);
    }

    @Override
    public void setScaleMode(int mode, float displayRatio, float verticalOffsetRatio) {
        nativeSetScaleMode(mode, displayRatio, verticalOffsetRatio, mNativePointer);
    }

    @Override
    public void setScreenRatio(float ratio) {
        nativeSetScreenRatio(ratio, mNativePointer);
    }

    @Override
    public void setDisplayInfo(@NonNull ByteArray byteArray4DisplayInfo) {
        nativeSetDisplayInfo(((JNIByteArray)byteArray4DisplayInfo).getBufferPointer(), mNativePointer);
    }

    @Override
    public void setBackgroundColor(int color) {
        nativeSetBackgroundColor((float)(Color.red(color) / 255.0), (float)(Color.green(color) / 255.0),
                (float)(Color.blue(color) / 255.0), (float)(Color.alpha(color) / 255.0), mNativePointer);
    }

    @Override
    public int singleTouch(int touchAction, float x, float y) {
        return nativeSingleTouch(touchAction, x, y, -1, mNativePointer);
    }

    @Override
    public int singleTouch(int touchAction, float x, float y, long touchTime) {
        return nativeSingleTouch(touchAction, x, y, touchTime, mNativePointer);
    }

    @Override
    public int doubleTouch(int touchAction, float x1, float y1, float x2, float y2) {
        return nativeDoubleTouch(touchAction, x1, y1, x2, y2, mNativePointer);
    }

    @Override
    public int doubleClick(float x, float y) {
        return nativeDoubleClick(x, y, mNativePointer);
    }

    @Override
    public int cancelZoom() {
        return nativeCancelZoom(mNativePointer);
    }

    @Override
    public void setCruise(int enable) {
        nativeSetCruise(enable, mNativePointer);
    }

    @Override
    public boolean hasTextureBuilt() {
        return nativeHasTextureBuilt(mNativePointer);
    }

    @Override
    public int getDisplayParamsLength() {
        return nativeGetDisplayParamsLength(mNativePointer);
    }

    @Override
    public int getDisplayParams(ByteArray byteArray) {
        return nativeGetDisplayParams(((JNIByteArray)byteArray).getBufferPointer(), mNativePointer);
    }

    @Override
    public boolean isInTransition() {
        return nativeIsInTransition(mNativePointer);
    }

    @Override
    public void cancelTransition() {
        nativeCancelTransition(mNativePointer);
    }

    public static void setViewPort(int x, int y, int width, int height) {
        nativeSetViewPort(x, y, width, height);
    }

    public static void clearColor(int color) {
        nativeClearColor((float)(Color.red(color) / 255.0), (float)(Color.green(color) / 255.0),
                (float)(Color.blue(color) / 255.0), (float)(Color.alpha(color) / 255.0));
    }

    @Override
    public boolean readPixelsRGBA8888(int x, int y, int width, int height, Bitmap bitmap) {
        return nativeReadPixelsRGBA8888(x, y, width, height, bitmap, mNativePointer);
    }

    @Override
    public boolean readPixels2JpgFile(int x, int y, int width, int height, String jpgUri, boolean offsetScreenRender) {
        return nativeReadPixels2JpgFile(x, y, width, height, jpgUri, offsetScreenRender, mNativePointer);
    }

    private native long nativeConstruct(int type);
    private native void nativeFinalize(long nativePointer);
    private native int nativeBuildProgram(long optionPointer, long displayParamsPointer, int displayParamsLength, long nativePointer);
    private native int nativeBuildTextures(long avFramePointer, long nativePointer);
    private native int nativeDrawFrame(long nativePointer);
    private native void nativeSetScaleMode(int mode, long nativePointer);
    private native void nativeSetScaleMode(int mode, float displayRatio, float verticalOffsetRatio, long nativePointer);
    private native int nativeSetScreenRatio(float ratio, long nativePointer);
    private native int nativeSetDisplayInfo(long displayInfoPointer, long nativePointer);
    private native void nativeSetBackgroundColor(float red, float green, float blue, float alpha, long nativePointer);
    private native int nativeSingleTouch(int touchAction, float x, float y, long time, long nativePointer);
    private native int nativeDoubleTouch(int touchAction, float x1, float y1, float x2, float y2, long nativePointer);
    private native int nativeDoubleClick(float x, float y, long nativePointer);
    private native int nativeCancelZoom(long nativePointer);
    private native int nativeSetCruise(int enable, long nativePointer);
    private native boolean nativeHasTextureBuilt(long nativePointer);
    private native int nativeGetDisplayParamsLength(long nativePointer);
    private native int nativeGetDisplayParams(long paramsBufferPointer, long nativePointer);
    private native boolean nativeIsInTransition(long nativePointer);
    private native int nativeCancelTransition(long nativePointer);
    private native static void nativeSetViewPort(int x, int y, int width, int height);
    private native static void nativeClearColor(float red, float green, float blue, float alpha);
    private native boolean nativeReadPixelsRGBA8888(int x, int y, int width, int height, Object bitmap, long nativePointer);
    private native boolean nativeReadPixels2JpgFile(int x, int y, int width, int height, String jpgUri, boolean offsetScreenRender, long nativePointer);
    private native static boolean nativeReadPixels2JpgFile(int x, int y, int width, int height, String jpgUri);
}
