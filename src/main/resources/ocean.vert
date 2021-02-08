#version 330

layout (location =0) in vec3 vertexPosition;
layout (location =1) in vec2 textureCoordinate;
layout (location =2) in vec3 vertexNormal;

uniform mat4 world;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 light_position;

out vec3 light_vector;
out vec3 normal_vector;
out vec3 halfway_vector;
out float fog_factor;
out vec2 tex_coord;

void main() {
    gl_Position = view * world * vec4(vertexPosition, 1.0);
    fog_factor = min(-gl_Position.z/500.0, 1.0);
    gl_Position = projection * gl_Position;

    vec4 v = view * world * vec4(vertexPosition, 1.0);
    vec3 normal = normalize(vertexNormal);

    light_vector = normalize((view * vec4(light_position, 1.0)).xyz - v.xyz);
    normal_vector = (inverse(transpose(view * world)) * vec4(normal, 0.0)).xyz;
    halfway_vector = light_vector + normalize(-v.xyz);

    tex_coord = textureCoordinate;
}