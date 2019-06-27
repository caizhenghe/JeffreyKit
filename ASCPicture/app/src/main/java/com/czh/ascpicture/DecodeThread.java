package com.czh.ascpicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author caizhenghe
 */
public class DecodeThread extends Thread {

    private OnDecoderListener mOnDecoderListener;
    private String mVideoPath, mPicListDir;
    private int mFps;
    Handler mHandler = new Handler(Looper.getMainLooper());

    int mDecodeTotalCount;
    MediaDecoder mMediaDecoder;
    private Bitmap mBitmapTemp;
    //    WeakReference<Context> mWeakReference;
    Context mContext;

    public DecodeThread(String picListDir, String videoPath, int fps, OnDecoderListener onEncoderListener, MainActivity mainActivity) {
        this.mFps = fps;
        this.mPicListDir = picListDir;
        this.mVideoPath = videoPath;
        this.mOnDecoderListener = onEncoderListener;
        //mWeakReference = new WeakReference(mainActivity);
        mContext = mainActivity;
    }

    @Override
    public void run() {
        mMediaDecoder = new MediaDecoder(mVideoPath);
        String videoDuration = mMediaDecoder.getVideoDuration();
        if (videoDuration != null) {
            try {
                int duration = Integer.parseInt(videoDuration);
                mDecodeTotalCount = duration / (1000 / mFps);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < mDecodeTotalCount; i++) {
            Log.i("icv", "第" + i + "张解码开始: time = " + i * (1000 / mFps) + " ----------------\n");
            Bitmap bitmap = mMediaDecoder.decodeFrame(i * (1000 / mFps));
            if (bitmap == null) {
                Log.i("icv", "第" + i + "张解码结束异常\n");
                continue;
            }
//            Log.i("icv", "第" + i + "张转换开始\n");
//            if (mWeakReference == null || mWeakReference.get() == null) return;
            mBitmapTemp = BitmapUtils.createAsciiPic(mContext, BitmapUtils.scale(mContext, bitmap));
//            mBitmapTemp = bitmap;
//            Log.i("icv", "第" + i + "张转换结束\n");
            Log.i("icv", "第" + i + "张编码结束----------------\n\n");
            FileOutputStream fos;
            try {
                String index = String.format("%05d", i);
                fos = new FileOutputStream(mPicListDir + File.separator + index + ".jpeg");
                if(mBitmapTemp.compress(Bitmap.CompressFormat.JPEG, 100, fos)){
                    fos.flush();
                    fos.close();
                }

                if (mOnDecoderListener != null) {
                    mOnDecoderListener.onProgress(((int) (100 * ((float) i / mDecodeTotalCount))));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnDecoderListener != null) {
                        mOnDecoderListener.showImg(mBitmapTemp);
                    }
                }
            });
        }
        Log.i("icv", "处理完成");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mOnDecoderListener != null) {
                    mOnDecoderListener.onComplete();
                }
            }
        });
    }

    public interface OnDecoderListener {
        void onProgress(int progress);

        void onComplete();

        void showImg(Bitmap bitmapTemp);
    }
}
