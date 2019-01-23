package com.czh.ffmpeg.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * 继承GLSurfaceView，
 * 重写对GLSurfaceView中对SurfaceHolder的三个Callback的实现：surfaceCreated、surfaceChanged、surfaceDestroyed
 */
public class GLVideoView extends GLSurfaceView {
    public final static int TPPLAYER_DISPLAY_SCALE_MODE_FILL = 0;
    public final static int TPPLAYER_DISPLAY_SCALE_MODE_ASPECT_FIT = 1;

    protected int mBackgroundColor;
    private SurfaceHolderCallback mSurfaceHolderCallback;
    private GetSnapshotListener mGetSnapshotListener;
    private SnapshotFinishListener mSnapshotFinishListener;
    private View mBackgroundView;
    protected int mScaleMode;
    protected float mDisplayRatio;
    protected int mVerticalOffset;

    public GLVideoView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        getHolder().removeCallback(this);
        mSurfaceHolderCallback = null;
        mBackgroundColor = Color.TRANSPARENT;
        mBackgroundView = null;
        mScaleMode = TPPLAYER_DISPLAY_SCALE_MODE_FILL;
        mDisplayRatio = 0.0f;
        mVerticalOffset = 0;
    }

    public GLVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSurfaceHolderCallback(SurfaceHolderCallback surfaceHolderCallback) {
        mSurfaceHolderCallback = surfaceHolderCallback;
    }

    public void setGetSnapshotListener(GetSnapshotListener listener) {
        mGetSnapshotListener = listener;
    }

    public void setSnapshotFinishListener(SnapshotFinishListener listener) {
        mSnapshotFinishListener = listener;
    }

    public void setScaleMode(int mode) {
        mScaleMode = mode;
    }

    /** 设置scale mode，如果是TPPLAYER_DISPLAY_SCALE_MODE_ASPECT_FIT，还可以指定显示的比例以及竖直方向偏移像素 */
    public void setScaleMode(int mode, float displayRatio, int verticalOffset) {
        mScaleMode = mode;
        mDisplayRatio = displayRatio;
        mVerticalOffset = verticalOffset;
    }

    public int getScaleMode() {
        return mScaleMode;
    }

    public float getDisplayRatio() {
        return mDisplayRatio;
    }

    public int getVerticalOffset() {
        return mVerticalOffset;
    }

    public void setVideoBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public int getVideoBackgroundColor() {
        return mBackgroundColor;
    }

    /**call from UI thread*/
    public void start() {
        //如果addCallback前Surface已经创建了，需要手动通知GLSurfaceView，以建立GLContext。
        //holder.setFormat()可以引起onSurfaceCreated()、onSurfaceChanged()回调。
        SurfaceHolder holder = this.getHolder();
        holder.addCallback(this);
        //this must be called from the same thread running the SurfaceView's window
        holder.setFormat(PixelFormat.TRANSLUCENT);
    }

    /**
     * called from UI thread
     * 重新让背景图显示，并重置mBackgroundView为null
     */
    public void release(ViewGroup viewGroup) {
        if (mBackgroundView != null) {
            mBackgroundView.setVisibility(View.VISIBLE);
            mBackgroundView = null;
        }
        viewGroup.removeView(this);
    }

    /**
     * called from UI thread
     * 有的播放器在播放之前会放置一张背景图，在视频播出第一个画面之后再让背景图消失，
     * 此处记录这个View
     */
    public void setBackgroundView(View view) {
        mBackgroundView = view;
    }

    /**
     * 视频播出第一帧时调用此方法，如果设置过背景图，则让其隐藏，
     * 在VideoView从布局上撤下时，重置mBackgroudView 为null，以免误调
     */
    public void startDisplay() {
        if (mBackgroundView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundView != null) {
                        mBackgroundView.setVisibility(View.GONE);
                        mBackgroundView = null;
                    }
                }
            });
        }
    }

    public boolean reqGetBitmapOfSnapshot() {
        if (mGetSnapshotListener != null) {
            return mGetSnapshotListener.reqGetBitmapOfSnapshot();
        }
        return false;
    }

    public void onGetBitmapOfSnapshot(Bitmap bitmap) {
        if (mSnapshotFinishListener != null)
            mSnapshotFinishListener.onGetBitmap(bitmap);
    }

    @Override
    public void onResume() {
        if (isGLSurfaceConfigured(this))
            super.onResume();
    }

    @Override
    public void onPause() {
        if (isGLSurfaceConfigured(this))
            super.onPause();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (isGLSurfaceConfigured(this)) {
            super.surfaceCreated(holder);
        }
        if (mSurfaceHolderCallback != null) {
            mSurfaceHolderCallback.surfaceCreated();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (isGLSurfaceConfigured(this)) {
            super.surfaceChanged(holder, format, w, h);
        }
        if (mSurfaceHolderCallback != null) {
            mSurfaceHolderCallback.surfaceChanged();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (isGLSurfaceConfigured(this)) {
            super.surfaceDestroyed(holder);
        }
        if (mSurfaceHolderCallback != null) {
            mSurfaceHolderCallback.surfaceDestroyed();
        }
    }

    /**
     * @return 是否此GLSurfaceView已被配置过。
     */
    public static boolean isGLSurfaceConfigured(GLSurfaceView glView) {
        boolean ret = false;
        try {
            Field rendererField = GLSurfaceView.class.getDeclaredField("mGLThread");
            rendererField.setAccessible(true);
            ret = null != rendererField.get(glView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /** 接口 */

    /**
     * 用于处理SurfaceView生命周期各阶段的事务
     */
    public interface SurfaceHolderCallback {
        void surfaceCreated();
        void surfaceChanged();
        void surfaceDestroyed();
    }

    /**
     *  请求获取快照的接口
     */
    public interface GetSnapshotListener {
        /**
         * 请求以Bitmap方式获取快照
         * @return true表示请求成功；否则请求失败。
         */
        boolean reqGetBitmapOfSnapshot();
    }

    /**
     * 快照获取完成的接口
     */
    public interface SnapshotFinishListener {
        void onGetBitmap(Bitmap bitmap);
    }
}
