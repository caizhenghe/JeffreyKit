package com.jeffrey.ogd;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// 使用静态导入，减少代码量
import static android.opengl.GLES20.*;

/**
 * @author caizhenghe
 */
public class GLRenderer implements GLSurfaceView.Renderer {
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 设置清空屏幕用的颜色
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // 设置视口尺寸（用于渲染的尺寸，其他位置使用clearColor渲染）
        glViewport(0, 0, width/2, height/2);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // 使用之前定义的擦除颜色来擦除屏幕上的所有颜色
        glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
