package demo;

import graphic.ColourMesh;
import graphic.Mesh;
import graphic.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import shader.ShaderProgram;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

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
        window = new Window(600, 600);
        program = new ShaderProgram();
        window.init();
        program.init("transform.vert", "mesh.frag");


        float[] positions = new float[]{
                -0.1f,  0.1f, 0f,
                -0.1f, -0.1f, 0f,
                0.1f, -0.1f, 0f,
                0.1f,  0.1f, 0f,
                0.2f,    0,   0f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,2,3,4
        };
        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.0f,
        };

        mesh = new ColourMesh(program.getProgramId(), positions, indices, colours);
    }

    private void loop(){

        while (!window.isClosed()) {
            window.clean();

            program.bind();

            float fov = (float) Math.toRadians(60);
            float zNear = 0.01f;
            float zFar = 1000f;
            float aspectRatio = (float) window.getWidth()/window.getHeight();

            Matrix4f projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, zNear, zFar);

            //设置uniform
            try (MemoryStack memoryStack = MemoryStack.stackPush()){
                FloatBuffer floatBuffer = memoryStack.callocFloat(16);
                projectionMatrix.get(floatBuffer);
                int projection = glGetUniformLocation(program.getProgramId(), "projection");
                glUniformMatrix4fv(projection, false, floatBuffer);
            }

            //平移，旋转，缩放
            Vector3f translation = new Vector3f(0,0f,-1f);
            Vector3f rotation = new Vector3f(0,45,0);   //角度
            float scale = 1f;

            Matrix4f worldMatrix = new Matrix4f()
                    .translate(translation)
                    .rotateX((float) Math.toRadians(rotation.x))
                    .rotateY((float) Math.toRadians(rotation.y))
                    .rotateZ((float) Math.toRadians(rotation.z))
                    .scale(scale);

            try (MemoryStack memoryStack = MemoryStack.stackPush()){
                FloatBuffer floatBuffer = memoryStack.callocFloat(16);
                worldMatrix.get(floatBuffer);
                int world = glGetUniformLocation(program.getProgramId(), "world");
                glUniformMatrix4fv(world, false, floatBuffer);
            }

            mesh.render();

            program.unbind();

            window.render();
        }
    }

    private void cleanup(){
        mesh.cleanup();
        program.cleanup();
        window.cleanup();
    }
}
