package demo;

import graphic.Mesh;
import graphic.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import shader.ShaderProgram;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

/**
 * @Author Gq
 * @Date 2020/12/6 17:19
 * @Version 1.0
 **/
public class Textures {
    private Window window;
    private ShaderProgram program;
    private Mesh mesh;

    private float deltaY;

    public static void main(String[] args) {
        new Textures().run();
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
        program.init("texture.vert", "texture.frag");


        float[] positions = new float[] {
                // VO
                -0.5f,  0.5f,  0.5f,
                // V1
                -0.5f, -0.5f,  0.5f,
                // V2
                0.5f, -0.5f,  0.5f,
                // V3
                0.5f,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f,  0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };
        int[] indices = new int[] {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };
        float[] colours = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };

        mesh = new Mesh(program.getProgramId(), positions, indices, colours);
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

            //自动旋转
            deltaY += 1.5f;
            if ( deltaY > 360 ) {
                deltaY = 0;
            }

            //平移，旋转，缩放
            Vector3f translation = new Vector3f(0,0,-1.5f);
            Vector3f rotation = new Vector3f(30,deltaY,0);   //角度
            float scale = 0.5f;

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
