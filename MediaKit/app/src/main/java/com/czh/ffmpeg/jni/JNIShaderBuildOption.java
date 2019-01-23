package com.czh.ffmpeg.jni;

/**
 * @author zhoucongcong
 * @ClassName: JNIShaderBuildOption
 * @Description: jni wrapper of ShaderBuildOption
 * @date 2017-03-16
 */
public class JNIShaderBuildOption {


    private final long mNativePointer;

    public JNIShaderBuildOption() {
        mNativePointer = nativeConstruct();
    }

    public JNIShaderBuildOption(boolean verticalMirror, boolean horizontalMirror,
                                boolean hazerm, boolean dewarp, long dewarpParameterPointer, int displayMode) {
        mNativePointer = nativeConstruct();
        nativeSetOption(mNativePointer, verticalMirror, horizontalMirror, hazerm, dewarp , dewarpParameterPointer, displayMode);
    }

    public long getNativePointer() {
        return mNativePointer;
    }

    @Override
    protected void finalize() throws Throwable {
        nativeFinalize(mNativePointer);
        super.finalize();
    }

    /** native method */

    private native long nativeConstruct();
    private native void nativeFinalize(long pointer);
    private native void nativeSetOption(long pointer, boolean verticalMirror, boolean horizontalMirror,
                                        boolean hazerm, boolean dewarp, long dewarpParameterPointer, int displayMode);
}
