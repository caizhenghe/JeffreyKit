package com.czh.ascpicture;


import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class MediaDecoder {
    private static final String TAG = "MediaDecoder";
    private FFmpegMediaMetadataRetriever mRetriever = null;
    private String mDuration;

    public MediaDecoder(String file) {
        if (VideoUtils.checkFile(file)) {
            mRetriever = new FFmpegMediaMetadataRetriever();
            mRetriever.setDataSource(file);
            mDuration = mRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
            Log.i(TAG, "mDuration : " + mDuration);
        } else {
            Log.e(TAG, "Error! Video not exists!");
        }
    }

    /**
     * 获取视频某一帧
     *
     * @param timeMs 毫秒
     */
    public Bitmap decodeFrame(long timeMs) {
        if (mRetriever == null) return null;
        Bitmap bitmap = mRetriever.getFrameAtTime(timeMs * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
        return bitmap;
    }

    /**
     * 取得视频文件播放长度
     *
     * @return
     */
    public String getVideoDuration() {
        return mDuration;
    }

}
