#version 330

in vec3 normal_vector;
in vec3 light_vector;
in vec3 halfway_vector;
in vec2 tex_coord;
in float fog_factor;
uniform sampler2D water;
out vec4 fragColor;

void main (void) {
	//fragColor = vec4(1.0, 1.0, 1.0, 1.0);

	vec3 normal1         = normalize(normal_vector);
	vec3 light_vector1   = normalize(light_vector);
	vec3 halfway_vector1 = normalize(halfway_vector);

	vec4 c = vec4(1,1,1,1);//texture(water, tex_coord);

	vec4 ambient_color  = vec4(0.0, 0.65, 0.75, 1.0);
	vec4 diffuse_color  = vec4(0.5, 0.65, 0.75, 1.0);
	vec4 diffuse_color  = vec4(1.0, 1.0, 1.0,  1.0);
	vec4 specular_color = vec4(1.0, 1.0, 1.0,  1.0);

	float emissive_contribution = 0.00;
	float ambient_contribution  = 0.30;
	float diffuse_contribution  = 0.30;
	float specular_contribution = 1.80;

	float d = dot(normal1, light_vector1);
	bool facing = d > 0.0;

//	fragColor = emissive_color * emissive_contribution +
//		    ambient_color  * ambient_contribution  * c +
//		    diffuse_color  * diffuse_contribution  * c * max(d, 0) +
//                    (facing ?
//			specular_color * specular_contribution * c * max(pow(dot(normal1, halfway_vector1), 120.0), 0.0) :
//			vec4(0.0, 0.0, 0.0, 0.0));

	vec4 diffuseColour = diffuse_color  * diffuse_contribution * max(d, 0);
	vec4 specularColour = specular_color * specular_contribution * max(pow(dot(normal1, halfway_vector1), 120.0), 0.0);
	vec4 ambientColour = ambient_color  * ambient_contribution;

	fragColor = ambientColour + specularColour;

	fragColor = fragColor * fogFactor + vec4(0.7f, 0.7f, 0.7f, 1f) * (1.0 - fogFactor);

	fragColor.a = 1.0;
}