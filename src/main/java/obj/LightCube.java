package obj;

import config.Constant;
import graphic.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import shader.ShaderProgram;

/**
 * @Author Gq
 * @Date 2020/12/22 23:20
 * @Version 1.0
 **/
public class LightCube extends GameObj {
    private ShaderProgram program;
    private MaterialMesh mesh;
    private Material material;

    public LightCube(Vector3f position, Vector3f rotation, float scale, ShaderProgram program) {
        super(position, rotation, scale);
        init(program);
    }

    private void init(ShaderProgram program){
        this.program = program;

        ObjModel objModel = ObjModel.loadObj(Constant.DEFAULT_RESOURCES_DIR + "/models/cube.obj");

        Texture texture = new Texture("texture/grassblock.png");
        mesh = new MaterialMesh(program.getProgramId(),
                objModel.getVertices(),
                objModel.getIndices(),
                objModel.getTextures(),
                objModel.getNormals());
        material = new Material(1f, texture);
        mesh.setMaterial(material);
    }

    @Override
    public void render(Window window, Camera camera) {
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
        program.setUniform("material", material);

        mesh.render();
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
    }
}
