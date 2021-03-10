package obj;

import config.Constant;
import graphic.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import shader.ShaderProgram;


/**
 * @Author Gq
 * @Date 2021/1/30 15:26
 * @Version 1.0
 **/
public class Boat extends GameObj {
    private ShaderProgram program;
    private ObjMesh mesh;

    private ObjModel objModel;

    public Boat(Vector3f position, Vector3f rotation, float scale) {
        super(position, rotation, scale);
        init();
    }

    private void init() {
        program = new ShaderProgram();
        program.init("obj.vert", "obj.frag");

        objModel = ObjModel.loadObj(Constant.DEFAULT_RESOURCES_DIR + "/models/boat2.obj");

        mesh = new ObjMesh(program.getProgramId(),
                objModel.getVertices(),
                objModel.getIndices(),
                objModel.getTextures(),
                objModel.getNormals());

        program.createUniform("projection");
        program.createUniform("world");
        program.createUniform("texture_sampler");
        program.createUniform("useColour");
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
        program.setUniform("useColour", mesh.isTextured()? 0 : 1);

        mesh.render();
        program.unbind();
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
        program.cleanup();
    }
}
