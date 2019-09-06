package com.jeffrey.ogd.programs;

import android.content.Context;

import com.jeffrey.ogd.util.GLUtils;
import com.jeffrey.ogd.util.ShaderHelper;

import static android.opengl.GLES20.glUseProgram;

/**
 * @author caizhenghe
 */
public class ShaderProgram {
    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    // Shader program
    protected int mProgram;

    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        mProgram = ShaderHelper.buildProgram(
                GLUtils.readShaderFromResource(
                        context, vertexShaderResourceId
                ),
                GLUtils.readShaderFromResource(
                        context, fragmentShaderResourceId
                )
        );
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(mProgram);
    }
}
