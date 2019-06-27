package com.jeffrey.ogd;

import android.util.Log;

import static android.opengl.GLES20.*;

/**
 * @author caizhenghe
 */
public class ShaderHelper {
    private static final String TAG = ShaderHelper.class.getSimpleName();

    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.d(TAG, "Validate program: " + validateStatus[0] + "; info = " + glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjectId = glCreateProgram();
        if (programObjectId == 0) {
            Log.e(TAG, "Create program ERROR!");
            return 0;
        }
        // 附上着色器
        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);
        // 链接程序
        glLinkProgram(programObjectId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        Log.d(TAG, "Link status: " + linkStatus[0] + "; log = " + glGetProgramInfoLog(programObjectId));
        if (linkStatus[0] == 0) {
            Log.e(TAG, "Link program ERROR!");
            glDeleteProgram(programObjectId);
            return 0;

        }
        return programObjectId;
    }

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        // 创建shader对象
        final int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {
            Log.e(TAG, "Create shader ERROR!");
            return 0;
        }
        final int[] compileStatus = new int[1];
        // 给对象上传对应的源码
        glShaderSource(shaderObjectId, shaderCode);
        // 编译shader对象
        glCompileShader(shaderObjectId);
        // 获取编译状态
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        Log.d(TAG, "Compile status: " + compileStatus[0] + "; log = " + glGetShaderInfoLog(shaderObjectId));
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Compile shader ERROR!");
            glDeleteShader(shaderObjectId);
            return 0;
        }

        return shaderObjectId;
    }
}
