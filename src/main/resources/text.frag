#version 330

in vec2 exTextureCoordinate;

out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec4 colour;
uniform int hasTexture;

void main()
{
    if (hasTexture == 1) {
        fragColor = colour * texture(texture_sampler, exTextureCoordinate);
    } else {
        fragColor = colour;
    }
}