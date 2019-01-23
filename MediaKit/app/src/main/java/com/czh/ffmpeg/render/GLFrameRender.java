package com.czh.ffmpeg.render;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.czh.ffmpeg.common.ByteArray;
import com.czh.ffmpeg.jni.AVFrame;
import com.czh.ffmpeg.jni.JNIGLProgram;
import com.czh.ffmpeg.jni.JNIShaderBuildOption;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Copyright (C), 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * @author caizhenghe
 * @ClassName: GLFrameRender
 * @Description: Version 1.0.0, 2019-01-23, caizhenghe create file.
 */

public class GLFrameRender implements GLSurfaceView.Renderer {
    private static final String TAG = GLFrameRender.class.getSimpleName();

    public final static int TPPLAYER_DISPLAY_ZOOM_STATUS_IDLE = 0;
    public final static int TPPLAYER_DISPLAY_ZOOM_STATUS_ZOOMED_GENERAL = 1;
    public final static int TPPLAYER_DISPLAY_ZOOM_STATUS_ZOOMED_LEFTMOST = 2;
    public final static int TPPLAYER_DISPLAY_ZOOM_STATUS_ZOOMED_RIGHTMOST = 3;

    private GLVideoView mSurfaceView;
    private GLProgram mGLProgram;
    private int mDisplayMode;
    private int mScreenWidth, mScreenHeight;
    private AVFrame mFrame;
    private boolean mFrameHasBuiltTexture;
    /**表示如下图像处理相关参数是否有变化，如果为true，则在onDrawFrame回调时重建Shader程序，并重新置为false*/
    private boolean mNeedUpdateShaderProgramOption;
    /**渲染相关变量的锁，防止GLThread线程和外部调用的线程(如UI线程）访问冲突*/
    private final Object mLock;
    /** 表示是否停止动画 */
    private boolean mStopAnimation;
    //private TPDisplayInfo mDisplayInfo;


    public GLFrameRender(Context context, GLVideoView view) {
        mSurfaceView = view;
        mSurfaceView.setRenderer(this);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //mDisplayMode = TPPlayerCommon.TPPLAYER_DISPLAY_MODE_ORIGIN;
        mFrame = new AVFrame();
        mNeedUpdateShaderProgramOption = true;
        mLock = new Object();
        mStopAnimation = false;
    }

    public void renderFrame(AVFrame frame) {
        /* 在GLThread线程里会访问mFrame进行渲染，所以这里要加锁避免访问冲突 */
        synchronized (mLock) {
            mFrame.RefFrom(frame);
            mFrameHasBuiltTexture = false;
            mSurfaceView.requestRender();
        }
    }

    public void setDisplayMode(int mode) {
        synchronized (mLock) {
            if (mDisplayMode != mode) {
                mDisplayMode = mode;
                mNeedUpdateShaderProgramOption = true;
                mSurfaceView.requestRender();
            }
        }
    }

    //public void setDisplayInfo(TPDisplayInfo displayInfo) {
//        mDisplayInfo = displayInfo;
//    }

    /**
     * 拖动画面，适用单指手势
     * 传入的坐标为相对于glSurfaceView坐标系，即以屏幕像素为单位，需要转换为顶点坐标系中的坐标
     * @param touchAction 手指动作，如按下、移动、松开
     * @param x 触发动作时的手指坐标
     * @param y
     * @return GLProgram.TPPLAYER_GL_ZOOM_STATUS_XXX, 表示经过手势动作后，画面的Zoom状态
     */
    public int singleTouch(int touchAction, int x, int y) {
        int ret = TPPLAYER_DISPLAY_ZOOM_STATUS_IDLE;
        /* 这里会更改渲染相关的参数，避免渲染过程中更改参数值，所以加锁 */
        synchronized (mLock) {
            if (mGLProgram != null) {
                ret = mGLProgram.singleTouch(touchAction, changeXCoordinate2Vertices(x), changeYCoordinate2Vertices(y));
                mSurfaceView.requestRender();
            }
        }
        return ret;
    }

    /**
     * 放缩显示，适用双指手势，
     * 传入的坐标为相对于glSurfaceView坐标系，即以屏幕像素为单位，需要转换为顶点坐标系中的坐标
     * @param touchAction 手指动作，如按下、移动、松开
     * @param x1  第一个手指的坐标
     * @param y1
     * @param x2  第二个手指的坐标
     * @param y2
     * @return GLProgram.TPPLAYER_GL_ZOOM_STATUS_XXX, 表示经过手势动作后，画面的Zoom状态
     */
    public int doubleTouch(int touchAction, int x1, int y1, int x2, int y2) {
        int ret = TPPLAYER_DISPLAY_ZOOM_STATUS_IDLE;
        //这里需要加锁，以免surface大小变化的同时调用以下代码
        synchronized (mLock) {
            if (mGLProgram != null) {
                ret = mGLProgram.doubleTouch(touchAction, changeXCoordinate2Vertices(x1),
                        changeYCoordinate2Vertices(y1), changeXCoordinate2Vertices(x2), changeYCoordinate2Vertices(y2));
                mSurfaceView.requestRender();
            }
        }
        return ret;
    }

    /**
     * 取消放大
     */
    public int cancelZoom() {
        int ret = TPPLAYER_DISPLAY_ZOOM_STATUS_IDLE;
        //这里需要加锁，以免surface大小变化的同时调用以下代码
        synchronized (mLock) {
            if (mGLProgram != null) {
                ret = mGLProgram.cancelZoom();
                mSurfaceView.requestRender();
            }
        }
        return ret;
    }

    /**
     * 双击画面
     * @param x 触发动作时的手指坐标
     * @param y
     * @return GLProgram.TPPLAYER_GL_ZOOM_STATUS_XXX, 表示经过双击后，画面的Zoom状态
     */
    public int doubleClick(int x, int y) {
        int ret = TPPLAYER_DISPLAY_ZOOM_STATUS_IDLE;
        synchronized (mLock) {
            if (mGLProgram != null) {
                ret = mGLProgram.doubleClick(changeXCoordinate2Vertices(x), changeYCoordinate2Vertices(y));
                mSurfaceView.requestRender();
            }
        }
        return ret;
    }

    public int getDisplayMode() {
        return mDisplayMode;
    }

    /**
     * 获取当前显示参数的长度，单位为字节
     */
    public int getDisplayParamsLength() {
        int paramsLength = 0;
        synchronized (mLock) {
            if (mGLProgram != null) {
                paramsLength = mGLProgram.getDisplayParamsLength();
            }
        }
        return paramsLength;
    }

    /**
     * 获取当前显示参数，写入byteArray中
     * @param byteArray 用于写入显示参数，会从byteArray的起始位置开始写
     */
    public void getDisplayParams(ByteArray byteArray) {
        synchronized (mLock) {
            if (mGLProgram != null) {
                mGLProgram.getDisplayParams(byteArray);
            }
        }
    }

    public void stopAnimation() {
        mStopAnimation = true;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        synchronized (mLock) {
        /* surface重建，glprogram也需要重建 */
            if (mGLProgram != null) {
                buildProgram(false);
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        synchronized (mLock) {
            mScreenWidth = width;
            mScreenHeight = height;
            JNIGLProgram.setViewPort(0, 0, width, height);
            if (mGLProgram != null) {
                mGLProgram.setScreenRatio((float) mScreenHeight / mScreenWidth);
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        int drawFrameResult;
        synchronized (mLock) {
            if (mGLProgram == null) {
                createProgram(mFrame.format);
                // 防止创建失败，仍然判断一次
                if (mGLProgram != null) {
                    buildProgram(true);
                } else {
                    return;
                }
            }

            if (mNeedUpdateShaderProgramOption) {
                buildProgram(true);
            }

            // 若未创建纹理或获得新的frame，则创建纹理
            if (!mGLProgram.hasTextureBuilt() || !mFrameHasBuiltTexture) {
                Log.d(TAG, "buildTexture, width = " + mFrame.width + ", height = " + mFrame.height);
                mGLProgram.buildTextures(mFrame);
                if (!mFrameHasBuiltTexture) {
                    mFrameHasBuiltTexture = true;
                }
            }
            drawFrameResult = mGLProgram.drawFrame();
            if ((drawFrameResult == GLProgram.TPPLAYER_GL_DRAW_FRAME_EC_CRUISE
                    || drawFrameResult == GLProgram.TPPLAYER_GL_DRAW_FRAME_EC_TRANSITION_ANIMATION
                    || drawFrameResult == GLProgram.TPPLAYER_GL_DRAW_FRAME_EC_INERTIA)
                    && !mStopAnimation) {
                mSurfaceView.requestRender();
            }

            mSurfaceView.startDisplay();
        }
    }

    private void createProgram(int colorFormat) {
        switch (colorFormat) {
            case AVFrame.TPAVFRAME_FORMAT_I420:
                mGLProgram = new JNIGLProgram(JNIGLProgram.GLPROGRAM_TYPE_I420);
                break;
            case AVFrame.TPAVFRAME_FORMAT_NV12:
                mGLProgram = new JNIGLProgram(JNIGLProgram.GLPROGRAM_TYPE_NV12);
                break;
            default:
                break;
        }
        if (mGLProgram != null) {
            mGLProgram.setBackgroundColor(mSurfaceView.getVideoBackgroundColor());
            mGLProgram.setScaleMode(mSurfaceView.getScaleMode(), mSurfaceView.getDisplayRatio(), (float)mSurfaceView.getVerticalOffset() / mScreenHeight);
            mGLProgram.setScreenRatio((float)mScreenHeight / mScreenWidth);
        }
    }

    private void buildProgram(boolean buildWithOption) {
        if (buildWithOption) {
            JNIShaderBuildOption option = new JNIShaderBuildOption(false, false,
                    false, false, 0, mDisplayMode);
            mGLProgram.buildProgram(option);
            mNeedUpdateShaderProgramOption = false;
        } else {
            mGLProgram.buildProgram();
        }
    }

    /**将x轴坐标从屏幕坐标系变换到顶点坐标的坐标系*/
    private float changeXCoordinate2Vertices(int x) {
        return (2.0f * x - mScreenWidth) / mScreenWidth;
    }

    /**将y轴坐标从屏幕的坐标系变换到顶点坐标的坐标系*/
    private float changeYCoordinate2Vertices(int y) {
        return (mScreenHeight - 2.0f * y) / mScreenHeight;
    }
}
