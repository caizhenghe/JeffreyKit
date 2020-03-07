package tv.jeff.glnoob;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TriangleRenderer implements GLSurfaceView.Renderer {
    private static String TAG = TriangleRenderer.class.getSimpleName();
    private final float[] mVerticesData = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f};
    private FloatBuffer mVertices;
    private int mWidth;
    private int mHeight;
    // 顶点着色器
    private String mVShaderStr =
            "#version 300 es\n" +
                    "in vec4 vPosition;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   gl_Position = vPosition;\n" +
                    "}\n";
    // 片段着色器
    private String mFShaderStr =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "out vec4 fragColor;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   fragColor = vec4 (1.0f, 0.0f, 0.0f, 1.0f);\n" +
                    "}\n";

    private int mProgramObject;


    public TriangleRenderer() {
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, mVShaderStr);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, mFShaderStr);

        int programObject = GLES30.glCreateProgram();
        int[] linked = new int[1];
        if (programObject == 0) {
            return;
        }
        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);

        // What does index 0 mean?
        GLES30.glBindAttribLocation(programObject, 0, "vPosition");
        GLES30.glLinkProgram(programObject);

        // check link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:");
            Log.e(TAG, GLES30.glGetProgramInfoLog(programObject));
            GLES30.glDeleteProgram(programObject);
            return;
        }

        mProgramObject = programObject;
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glViewport(0, 0, mWidth, mHeight);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glUseProgram(mProgramObject);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, mVertices);
        GLES30.glEnableVertexAttribArray(0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
    }

    private int loadShader(int type, String shaderStr) {
        int shader;
        int[] compiled = new int[1];

        shader = GLES30.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }
        GLES30.glShaderSource(shader, shaderStr);
        GLES30.glCompileShader(shader);
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }
}
