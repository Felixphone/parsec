#version 330 core

in vec3 passPixelPos;
in vec3 passVertexColours;
in vec2 passTextureCoord;

out vec4 pixelColour;

uniform sampler2D tex;

void main() {
    pixelColour = vec4(passVertexColours * passTextureCoord.r, 0.0f);
}
