package com.jeffrey.ogd;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.jeffrey.ogd.util.GLUtils;
import com.jeffrey.ogd.util.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * @author caizhenghe
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    /**
     * 每个浮点数占4个字节
     */
    private static final int BYTES_PER_FLOAT = 4;
    //    private static final String U_COLOR = "u_Color";
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final String A_COLOR = "a_Color";
    private static final String A_POSITION = "a_Position";
    private static final String U_MATRIX = "u_Matrix";
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] mProjectMatrix = new float[16];

    //    private int mUColorLocation;
    private int mAColorLocation;
    private int mAPositionLocation;
    private int mUMatrixLocation;

    /**
     * 三角形扇，中间点进行重用
     * 将顶点位置和颜色记录在一个数组当中
     */
    private float[] mTableVerticesWithTriangles = {
            // triangle fan
            0, 0, 1f, 1f, 1f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            // line1
            -0.5f, 0f, 1f, 0f, 0f,
            0.5f, 0f, 1f, 0f, 0f,
            // mallets
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f,
    };

    /**
     * 使用三角形拼接长方形桌面
     * 逆时针绘制三角形
     * OpenGL会将屏幕映射到[-1, 1]的范围中，左下角是（-1，-1），所以需要对顶点矩阵进行相应的转换
     */
//    private float[] mTableVerticesWithTriangles = {
//            // triangle1
//            -0.5f, -0.5f,
//            0.5f, 0.5f,
//            -0.5f, 0.5f,
//            // triangle2
//            -0.5f, -0.5f,
//            0.5f, -0.5f,
//            0.5f, 0.5f,
//            // line1
//            -0.5f, 0f,
//            0.5f, 0f,
//            // mallets
//            0f, -0.25f,
//            0f, 0.25f,
//    };
    /**
     * 用于将数据存储到本地内存(native)，不受gc控制
     */
    private FloatBuffer mVertexData;

    private Context mContext;
    private int mProgram;
    private String mVertexShaderSource;
    private String mFragmentShaderSource;

    public AirHockeyRenderer(Context context) {
        mContext = context;

        // 判断横竖屏
//        Configuration configuration = context.getResources().getConfiguration();
//        // 横竖屏适配方案一：横屏下动态修改矩阵坐标，保证图像宽高比不变
//        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // 获取屏幕宽高
//            DisplayMetrics dm = context.getResources().getDisplayMetrics();
//            float ratio = dm.heightPixels / (float) dm.widthPixels;
//            // 适配图像的x坐标
//            for (int i = 0; i < mTableVerticesWithTriangles.length; i += 5) {
//                mTableVerticesWithTriangles[i] = mTableVerticesWithTriangles[i] * ratio;
//            }
//        }

        // 创建顶点坐标的本地内存
        mVertexData = ByteBuffer
                .allocateDirect(mTableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexData.put(mTableVerticesWithTriangles);
        // 从文件中读取着色器信息
        mVertexShaderSource = GLUtils.readShaderFromResource(context, R.raw.simple_vertex_shader);
        mFragmentShaderSource = GLUtils.readShaderFromResource(context, R.raw.simple_fragment_shader);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 创建、编译着色器
        int vertexShader = ShaderHelper.compileVertexShader(mVertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(mFragmentShaderSource);
        // 创建、链接Program
        mProgram = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        // 从通道中取出Color属性的位置
//        mUColorLocation = glGetUniformLocation(mProgram, U_COLOR);
        mAColorLocation = glGetAttribLocation(mProgram, A_COLOR);
        // 从通道中取出Position属性的位置
        mAPositionLocation = glGetAttribLocation(mProgram, A_POSITION);
        // 从通道中取出正交投影矩阵的位置
        mUMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX);

        // 将本地内存中的顶点坐标传给OpenGL
        mVertexData.position(0);
        // Arg2：每个属性对应2个数字
        glVertexAttribPointer(mAPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, mVertexData);
        glEnableVertexAttribArray(mAPositionLocation);

        // 将本地内存中的顶点颜色传给OpenGL
        mVertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(mAColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, mVertexData);
        glEnableVertexAttribArray(mAColorLocation);

        // 设置清空屏幕用的颜色
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // 验证程序有效性
        ShaderHelper.validateProgram(mProgram);
        glUseProgram(mProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // 设置视口尺寸（顶点坐标矩阵会映射到该视窗中），View的左下角是原点(0, 0)
        glViewport(0, 0, width, height);

        // 方案二：通过宽高比定义虚拟坐标空间（拓宽x，y轴空间），再通过正交投影矩阵将虚拟坐标转换到归一化设备坐标
        // 创建正交矩阵
        final float ratio = width > height ? width / (float) height : height / (float) width;
        if (width > height) {
            // 横屏状态下拓宽x轴范围
            Matrix.orthoM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, -1f, 1f);
        } else {
            // 竖屏状态下拓宽y轴范围
            Matrix.orthoM(mProjectMatrix, 0, -1f, 1f, -ratio, ratio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // 使用之前定义的擦除颜色来擦除屏幕上的所有颜色
        glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // 将正交矩阵传递给着色器
        glUniformMatrix4fv(mUMatrixLocation, 1, false, mProjectMatrix, 0);
        // 定义绘制的颜色
//        glUniform4f(mUColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        // 使用6个数字绘制2个三角形
//        glDrawArrays(GL_TRIANGLES, 0, 6);
        // 绘制三角形扇
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
        glDrawArrays(GL_LINES, 6, 2);
        glDrawArrays(GL_POINTS, 8, 1);
        glDrawArrays(GL_POINTS, 9, 1);

    }
}
