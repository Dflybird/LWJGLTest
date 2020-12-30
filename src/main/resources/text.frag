#version 330

in vec2 exTextureCoordinate;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec3 colour;

void main()
{
    fragColor = colour * texture(texture_sampler, exColour);
}