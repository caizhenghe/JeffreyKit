package com.jeffrey.ogd;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.jeffrey.ogd.model.Mallet;
import com.jeffrey.ogd.model.Table;
import com.jeffrey.ogd.programs.ColorShaderProgram;
import com.jeffrey.ogd.programs.TextureShaderProgram;
import com.jeffrey.ogd.util.MatrixHelper;
import com.jeffrey.ogd.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

// 使用静态导入，减少代码量

/**
 * @author caizhenghe
 */
public class AirHockeyTextureRenderer implements GLSurfaceView.Renderer {
    private Context mContext;
    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    private Table mTable;
    private Mallet mMallet;

    private TextureShaderProgram mTextureProgram;
    private ColorShaderProgram mColorProgram;
    private int mTexture;

    public AirHockeyTextureRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 设置清空屏幕用的颜色
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mTable = new Table();
        mMallet = new Mallet();

        mTextureProgram = new TextureShaderProgram(mContext);
        mColorProgram = new ColorShaderProgram(mContext);

        mTexture = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // 设置视口尺寸（用于渲染的尺寸，其他位置使用clearColor渲染）
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(mProjectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);

        setIdentityM(mModelMatrix, 0);
        translateM(mModelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(mModelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, mProjectionMatrix, 0, mModelMatrix, 0);
        System.arraycopy(temp, 0, mProjectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Clear the rendering surface
        glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Draw the table.
        mTextureProgram.useProgram();
        mTextureProgram.setUniforms(mProjectionMatrix, mTexture);
        mTable.bindData(mTextureProgram);
        mTable.draw();

        // Draw the mallets
        mColorProgram.useProgram();
        mColorProgram.setUniforms(mProjectionMatrix);
        mMallet.bindData(mColorProgram);
        mMallet.draw();

    }
}
