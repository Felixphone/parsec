#version 330 core

in vec3 passPixelPos;
in vec3 passVertexColours;
in vec2 passTextureCoord;

out vec4 pixelColour;

uniform sampler2D samp;

void main() {
    vec4 tex = texture(samp, passTextureCoord);
    pixelColour = vec4(tex.r, tex.g, tex.b, tex.a);
}
