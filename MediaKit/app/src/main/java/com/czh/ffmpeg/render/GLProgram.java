package com.czh.ffmpeg.render;


import android.graphics.Bitmap;

import com.czh.ffmpeg.common.ByteArray;
import com.czh.ffmpeg.jni.AVFrame;
import com.czh.ffmpeg.jni.JNIShaderBuildOption;

public interface GLProgram {
    int TPPLAYER_GL_DRAW_FRAME_EC_INVALID = -1;
    int TPPLAYER_GL_DRAW_FRAME_EC_OK = 0;
    int TPPLAYER_GL_DRAW_FRAME_EC_TRANSITION_ANIMATION = 1;
    int TPPLAYER_GL_DRAW_FRAME_EC_CRUISE = 2;
    int TPPLAYER_GL_DRAW_FRAME_EC_INERTIA = 3;

    void buildProgram();
    void buildProgram(JNIShaderBuildOption option);

    /**
     * @param option Shader相关选项
     * @param byteArray4DisplayParams 显示参数，长度为byteArray4DisplayParams.size()
     */
    void buildProgram(JNIShaderBuildOption option, ByteArray byteArray4DisplayParams);
    void buildTextures(AVFrame frame);
    int drawFrame();
    void setScaleMode(int iMode);
    void setScaleMode(int iMode, float fDisplayRatio, float fVerticalOffsetRatio);
    void setScreenRatio(float ratio);
    void setDisplayInfo(ByteArray byteArray4DisplayInfo);
    void setBackgroundColor(int color);
    int singleTouch(int touchAction, float x, float y);
    int singleTouch(int touchAction, float x, float y, long touchTime);
    int doubleTouch(int touchAction, float x1, float y1, float x2, float y2);
    int doubleClick(float x, float y);
    int cancelZoom();
    void setCruise(int bEnable);
    boolean hasTextureBuilt();

    /**
     * 获取当前显示参数的字节数。调用者应先调用此方法获取长度后，分配相应大小的byteArray用于获取具体参数。
     */
    int getDisplayParamsLength();

    /**
     * 获取当前的显示参数，并写入byteArray中
     * @param byteArray
     * @return 参数的长度，单位为字节
     */
    int getDisplayParams(ByteArray byteArray);

    boolean isInTransition();

    void cancelTransition();

    /**
     * 从OpenGL读取当前正在显示的画面像素，以RGBA8888格式写入bitmap中
     * 直接传入Bitmap对象，是为了可以直接将OpenGL的像素读取到Bitmap缓存中，减少拷贝次数
     * @param x,y 需要读取像素的画面左下角坐标
     * @param width,height 需要读取像素的画面宽高
     * @param bitmap 待写入的Bitmap对象
     * @return 返回true表示获取显示的像素成功，否则失败
     */
    boolean readPixelsRGBA8888(int x, int y, int width, int height, Bitmap bitmap);

    /**
     * 从OpenGL读取当前正在显示的画面像素，并以jpg格式写入指定的文件
     * @param x,y 需要读取像素的画面左下角坐标
     * @param width,height 需要读取像素的画面宽高
     * @param jpgUri 待写入的文件路径
     * @return 返回true表示成功读取到像素并写入文件，否则失败
     */
    boolean readPixels2JpgFile(int x, int y, int width, int height, String jpgUri, boolean offsetScreenRender);
}
