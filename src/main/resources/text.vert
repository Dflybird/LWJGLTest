#version 330

layout (location =0) in vec3 vertexPosition;
layout (location =1) in vec2 textureCoordinate;
layout (location =2) in vec3 vertexNormal;

out vec2 exTextureCoordinate;

uniform mat4 projection;

void main()
{
    gl_Position = projection * vec4(vertexPosition, 1.0);
    exTextureCoordinate = textureCoordinate;
}