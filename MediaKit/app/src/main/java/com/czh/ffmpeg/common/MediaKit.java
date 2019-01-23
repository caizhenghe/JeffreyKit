package com.czh.ffmpeg.common;

public class MediaKit {
    static {
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avdevice-57");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avutil-55");
        System.loadLibrary("postproc-54");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
        System.loadLibrary("mediakit");
    }

    public String getAvcodecConfiguration() {
        return getAvcodecConfigurationNative();
    }

    public int decode(String inputFilePath, String outputFilePath) {
        return decodeNative(inputFilePath, outputFilePath);
    }

    private native String getAvcodecConfigurationNative();

    private native int decodeNative(String input, String output);
}
