#version 330 core

layout(location = 0) in vec3 pixelPos;
layout(location = 1) in vec3 vertexColours;
layout(location = 2) in vec2 textureCoord;

out vec3 passPixelPos;
out vec3 passVertexColours;
out vec2 passTextureCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    gl_Position = projection * view * model * vec4(pixelPos, 1.0f);
    passPixelPos = pixelPos;
    passVertexColours = vertexColours;
    passTextureCoord = textureCoord;
}
