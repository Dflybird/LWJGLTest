package demo;

import graphic.Mesh;
import graphic.Window;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import shader.ShaderProgram;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @Author: gq
 * @Date: 2020/12/3 14:45
 */
public class Transform {

    private Window window;
    private ShaderProgram program;
    private Mesh mesh;

    public static void main(String[] args) {
        new Transform().run();
    }

    private void run(){
        init();
        loop();
        cleanup();
    }

    private void init(){
        window = new Window(300, 300);
        program = new ShaderProgram();
        window.init();
        program.init();


        float[] positions = new float[]{
                -0.5f,  0.5f, -5.05f,
                -0.5f, -0.5f, -5.05f,
                0.5f, -0.5f, -5.05f,
                0.5f,  0.5f, -5.05f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
        };
        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };

        mesh = new Mesh(program.getProgramId(), positions, indices, colours);
    }

    private void loop(){
        // Set the clear color
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        while (!window.isClosed()) {
            window.clean();

            program.bind();

            float fov = (float) Math.toRadians(60);
            float zNear = 0.01f;
            float zFar = 1000f;
            float aspectRatio = (float) window.getWidth()/window.getHeight();

            Matrix4f projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, zNear, zFar);

            try (MemoryStack memoryStack = MemoryStack.stackPush()){
                FloatBuffer floatBuffer = memoryStack.callocFloat(16);
                projectionMatrix.get(floatBuffer);
                int projection = glGetUniformLocation(program.getProgramId(), "projection");
                glUniformMatrix4fv(projection, false, floatBuffer);
            }

            mesh.render();

            program.unbind();

            window.render();
        }
    }

    private void cleanup(){
        mesh.clearup();
        program.cleanup();
        window.cleanup();
    }
}
