package shader;

import config.Constant;

import java.io.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * @Author Gq
 * @Date 2020/12/3 23:14
 * @Version 1.0
 **/
public class ShaderProgram {

    private int programId;

    public void init(){
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
            FileInputStream fileInputStream = new FileInputStream(new File(Constant.DEFAULT_RESOURCES_DIR, "transform.vert"));
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
            FileInputStream fileInputStream = new FileInputStream(new File(Constant.DEFAULT_RESOURCES_DIR, "mesh.frag"));
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
