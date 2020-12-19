package obj;

import graphic.Mesh;
import graphic.Texture;
import graphic.TextureMesh;
import graphic.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import shader.ShaderProgram;

/**
 * @Author Gq
 * @Date 2020/12/8 20:29
 * @Version 1.0
 **/
public class GrassBlock extends GameObj {

    private final float[] positions = new float[] {
            // V0
            -0.5f, 0.5f, 0.5f,
            // V1
            -0.5f, -0.5f, 0.5f,
            // V2
            0.5f, -0.5f, 0.5f,
            // V3
            0.5f, 0.5f, 0.5f,
            // V4
            -0.5f, 0.5f, -0.5f,
            // V5
            0.5f, 0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,

            // For text coords in top face
            // V8: V4 repeated
            -0.5f, 0.5f, -0.5f,
            // V9: V5 repeated
            0.5f, 0.5f, -0.5f,
            // V10: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V11: V3 repeated
            0.5f, 0.5f, 0.5f,

            // For text coords in right face
            // V12: V3 repeated
            0.5f, 0.5f, 0.5f,
            // V13: V2 repeated
            0.5f, -0.5f, 0.5f,

            // For text coords in left face
            // V14: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V15: V1 repeated
            -0.5f, -0.5f, 0.5f,

            // For text coords in bottom face
            // V16: V6 repeated
            -0.5f, -0.5f, -0.5f,
            // V17: V7 repeated
            0.5f, -0.5f, -0.5f,
            // V18: V1 repeated
            -0.5f, -0.5f, 0.5f,
            // V19: V2 repeated
            0.5f, -0.5f, 0.5f,
    };
    private final int[] indices = new int[]{
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            8, 10, 11, 9, 8, 11,
            // Right face
            12, 13, 7, 5, 12, 7,
            // Left face
            14, 15, 6, 4, 14, 6,
            // Bottom face
            16, 18, 19, 17, 16, 19,
            // Back face
            4, 6, 7, 5, 4, 7,
    };
    private final float[] textureCoordinates = new float[]{
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,

            0.0f, 0.0f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,

            // For text coords in top face
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.0f, 1.0f,
            0.5f, 1.0f,

            // For text coords in right face
            0.0f, 0.0f,
            0.0f, 0.5f,

            // For text coords in left face
            0.5f, 0.0f,
            0.5f, 0.5f,

            // For text coords in bottom face
            0.5f, 0.0f,
            1.0f, 0.0f,
            0.5f, 0.5f,
            1.0f, 0.5f,
    };

    private ShaderProgram program;
    private Mesh mesh;
    private Texture texture;



    public GrassBlock(Vector3f position, Vector3f rotation, float scale) {
        super(position, rotation, scale);
        init();
    }

    private void init(){
        program = new ShaderProgram();
        program.init("texture.vert", "texture.frag");

        texture = new Texture("texture/grassblock.png");
        mesh = new TextureMesh(program.getProgramId(), positions, indices, texture, textureCoordinates);

        program.createUniform("projection");
        program.createUniform("world");
        program.createUniform("texture_sampler");
    }

    @Override
    public void render(Window window, Camera camera) {

        program.bind();
        float fov = (float) Math.toRadians(60);
        float zNear = 0.01f;
        float zFar = 1000f;
        float aspectRatio = (float) window.getWidth()/window.getHeight();

        Matrix4f projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, zNear, zFar);

        Matrix4f worldMatrix = new Matrix4f()
                .translate(translation)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(scale);

        Vector3f cameraPos = camera.getPosition();
        Vector3f cameraRot = camera.getRotation();
        Matrix4f viewMatrix = new Matrix4f()
                .rotateX((float) Math.toRadians(cameraRot.x))
                .rotateY((float) Math.toRadians(cameraRot.y))
                .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        program.setUniform("projection", projectionMatrix);
        program.setUniform("world", viewMatrix.mul(worldMatrix));
        program.setUniform("texture_sampler", 0);

        mesh.render();
        program.unbind();
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
        texture.cleanup();
        program.cleanup();
    }
}
