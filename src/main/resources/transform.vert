#version 330

layout (location =0) in vec3 position;
layout (location =1) in vec3 inColour;

uniform mat4 world;
uniform mat4 projection;

out vec3 exColour;

void main()
{
    gl_Position = projection * world * vec4(position, 1.0);
    exColour = inColour;
}