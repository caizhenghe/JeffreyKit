package tv.jeff.glnoob;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureRenderer implements GLSurfaceView.Renderer {
    private static String TAG = TextureRenderer.class.getSimpleName();
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;
    private int mWidth;
    private int mHeight;

    private int mProgramObject;
    private int mSamplerLoc;
    private int mTextureId;


    public TextureRenderer() {
        final float[] verticesData =
                {
                        -0.5f, 0.5f, 0.0f, // Position 0
                        0.0f, 0.0f, // TexCoord 0
                        -0.5f, -0.5f, 0.0f, // Position 1
                        0.0f, 1.0f, // TexCoord 1
                        0.5f, -0.5f, 0.0f, // Position 2
                        1.0f, 1.0f, // TexCoord 2
                        0.5f, 0.5f, 0.0f, // Position 3
                        1.0f, 0.0f // TexCoord 3
                };

        mVertices = ByteBuffer.allocateDirect(verticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(verticesData).position(0);

        final short[] indicesData =
                {
                        0, 1, 2, 0, 2, 3
                };

        mIndices = ByteBuffer.allocateDirect(indicesData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(indicesData).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 顶点着色器
        String vShaderStr = "#version 300 es\n" +
                "layout(location=0) in vec4 a_position;\n" +
                "layout(location=1) in vec2 a_texCoord;\n" +
                "out vec2 v_texCoord;\n" +
                "void main()\n" +
                "{\n" +
                "   gl_Position = a_position;\n" +
                "   v_texCoord = a_texCoord;\n" +
                "}\n";
        // 片段着色器
        String fShaderStr = "#version 300 es\n" +
                "precision mediump float;\n" +
                "in vec2 v_texCoord;\n" +
                "layout(location=0) out vec4 outColor;\n" +
                "uniform sampler2D s_texture;\n" +
                "void main()\n" +
                "{\n" +
                "   outColor = texture(s_texture, v_texCoord);\n" +
                "}\n";
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vShaderStr);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr);

        int programObject = GLES30.glCreateProgram();
        int[] linked = new int[1];
        if (programObject == 0) {
            return;
        }
        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);
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
        mSamplerLoc = GLES30.glGetAttribLocation(mProgramObject, "s_texture");
        mTextureId = createSimpleTexture2D();

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

        mVertices.position(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 5 * 4, mVertices);
        mVertices.position(3);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 5 * 4, mVertices);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId);

        GLES30.glUniform1i(mSamplerLoc, 0);
        // what dose mIndices mean
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, mIndices);
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

    private int createSimpleTexture2D() {
        int[] textureId = new int[1];
        // 2x2 Image, 3 bytes per pixel (R, G, B)
        byte[] pixels =
                {
                        (byte) 0xff, 0, 0, // Red
                        0, (byte) 0xff, 0, // Green
                        0, 0, (byte) 0xff, // Blue
                        (byte) 0xff, (byte) 0xff, 0 // Yellow
                };
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(4 * 3);
        pixelBuffer.put(pixels).position(0);

        // Use tightly packed data
        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT, 1);
        //  Generate a texture object
        GLES30.glGenTextures(1, textureId, 0);
        // Bind the texture object
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId[0]);
        //  Load the texture
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGB, 2, 2, 0, GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE, pixelBuffer);
        // Set the filtering mode
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
        return textureId[0];
    }
}
