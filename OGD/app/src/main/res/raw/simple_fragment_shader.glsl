// 指定精度为浮点数
precision mediump float;

// uniform vec4 u_Color;
varying vec4 v_Color;

void main()
{
    gl_FragColor = v_Color;
}