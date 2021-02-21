#version 330

layout (location =0) in vec3 vertexPosition;
layout (location =1) in vec2 textureCoordinate;
layout (location =2) in vec3 vertexNormal;

out vec3 exWorldPos;
out vec3 exVertexNormal;
out vec2 exTextureCoordinate;
out mat4 exView;

uniform mat4 world;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    //将模型坐标系中的点坐标和法向量转换到世界坐标系
    vec4 worldPos = view * world * vec4(vertexPosition, 1.0);
    gl_Position = projection * worldPos;

    vec4 worldNor = view * world * vec4(vertexNormal, 0.0);


    exWorldPos = worldPos.xyz;
    exVertexNormal = normalize(worldNor).xyz;
    exTextureCoordinate = textureCoordinate;
    exView = view;
}