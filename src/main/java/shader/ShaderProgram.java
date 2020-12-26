package shader;

import config.Constant;
import graphic.Material;
import graphic.PointLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * 着色器，用于管理着色文件，配置，连接，绑定着色器程序
 * @Author Gq
 * @Date 2020/12/3 23:14
 * @Version 1.0
 **/
public class ShaderProgram {

    private int programId;

    private Map<String, Integer> uniforms;

    public void init(String vertFile, String fragFile){
        uniforms = new HashMap<>();

        programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader");
        }

        //vertex着色器
        int vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        if (vertexShaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + GL_VERTEX_SHADER);
        }
        //读取文件
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(Constant.DEFAULT_RESOURCES_DIR, vertFile));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        glShaderSource(vertexShaderId, builder.toString());
        glCompileShader(vertexShaderId);
        if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(vertexShaderId, 1024));
        }
        glAttachShader(programId, vertexShaderId);

        //fragment着色器
        int fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        if (fragmentShaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + GL_FRAGMENT_SHADER);
        }
        //读文件
        builder.setLength(0);
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(Constant.DEFAULT_RESOURCES_DIR, fragFile));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        glShaderSource(fragmentShaderId, builder.toString());
        glCompileShader(fragmentShaderId);
        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(fragmentShaderId, 1024));
        }
        glAttachShader(programId, fragmentShaderId);

        //链接程序
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    public void createUniform(String uniform){
        uniforms.put(uniform, glGetUniformLocation(programId, uniform));
    }

    public void createPointLightUniforms(String uniform) {
        createUniform(uniform + ".colour");
        createUniform(uniform + ".position");
        createUniform(uniform + ".intensity");
        createUniform(uniform + "att.constant");
        createUniform(uniform + "att.linear");
        createUniform(uniform + "att.exponent");
    }

    public void createMaterialUniforms(String uniform) {
        createUniform(uniform + ".ambient");
        createUniform(uniform + ".diffuse");
        createUniform(uniform + ".specular");
        createUniform(uniform + ".hasTexture");
        createUniform(uniform + ".reflectance");
    }

    public void setUniform(String uniform, Matrix4f value){
        try (MemoryStack memoryStack = MemoryStack.stackPush()){
            FloatBuffer floatBuffer = memoryStack.callocFloat(16);
            value.get(floatBuffer);
            Integer uniformIndex = uniforms.get(uniform);
            glUniformMatrix4fv(uniformIndex, false, floatBuffer);
        }
    }

    public void setUniform(String uniform, int value){
        Integer uniformIndex = uniforms.get(uniform);
        glUniform1i(uniformIndex, value);
    }

    public void setUniform(String uniform, float value){
        Integer uniformIndex = uniforms.get(uniform);
        glUniform1f(uniformIndex, value);
    }


    public void setUniform(String uniform, Vector3f value){
        Integer uniformIndex = uniforms.get(uniform);
        glUniform3f(uniformIndex, value.x, value.y, value.z);
    }

    public void setUniform(String uniform, Vector4f value){
        Integer uniformIndex = uniforms.get(uniform);
        glUniform4f(uniformIndex, value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniform, PointLight pointLight){
        setUniform(uniform + ".colour", pointLight.getColour());
        setUniform(uniform + ".position", pointLight.getPosition());
        setUniform(uniform + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAtt();
        setUniform(uniform + "att.constant", att.getConstant());
        setUniform(uniform + "att.linear", att.getLinear());
        setUniform(uniform + "att.exponent", att.getExponent());

    }

    public void setUniform(String uniform, Material material) {
        setUniform(uniform + ".ambient", material.getAmbient());
        setUniform(uniform + ".diffuse", material.getDiffuse());
        setUniform(uniform + ".specular", material.getSpecular());
        setUniform(uniform + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniform(uniform + ".reflectance", material.getReflectance());
    }

    public void bind() {
        //使用着色器程序
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup(){
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    public int getProgramId() {
        return programId;
    }

}
