package com.jeffrey.ogd.programs;

import android.content.Context;

import com.jeffrey.ogd.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class ColorShaderProgram extends ShaderProgram {
    // Uniform locations
    private int mUMatrixLocation;

    // Attribute locations
    private int mAPositionLocation;
    private int mAColorLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader,
                R.raw.simple_fragment_shader);

        // Retrieve uniform locations for the shader program.
        mUMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX);

        // Retrieve attribute locations for the shader program.
        mAPositionLocation = glGetAttribLocation(mProgram, A_POSITION);
        mAColorLocation = glGetAttribLocation(mProgram, A_COLOR);
    }

    public void setUniforms(float[] matrix) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(mUMatrixLocation, 1, false, matrix, 0);
    }

    public int getPositionAttributeLocation() {
        return mAPositionLocation;
    }

    public int getColorAttributeLocation() {
        return mAColorLocation;
    }


}
