package obj;

import config.Constant;
import graphic.Material;
import graphic.MaterialMesh;
import graphic.Texture;
import graphic.Window;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import shader.ShaderProgram;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Gq
 * @Date 2021/1/1 16:22
 * @Version 1.0
 **/
public class ObjItem extends GameObj{

    private final ShaderProgram program;
    private MaterialMesh mesh;

    public ObjItem(String fileName, ShaderProgram program) {
        this.program = program;
        ObjModel objModel = ObjModel.loadObj(fileName);
        this.mesh = new MaterialMesh(program.getProgramId(),
                objModel.getVertices(),
                objModel.getIndices(),
                objModel.getTextures(),
                objModel.getNormals());
        Material material = new Material();
        material.setAmbient(new Vector4f(1,0,0,1));
        mesh.setMaterial(material);

        program.createUniform("projection");
        program.createUniform("colour");
        program.createUniform("texture_sampler");
        program.createUniform("hasTexture");
    }

    public MaterialMesh getMesh() {
        return mesh;
    }

    @Override
    public void render(Window window, Camera camera) {
        Matrix4f orthogonalMatrix = new Matrix4f()
                .setOrtho2D(0, window.getWidth(), window.getHeight(),0);

        Matrix4f worldMatrix = new Matrix4f()
                .translate(translation)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(scale);

        Matrix4f projectionMatrix = new Matrix4f(orthogonalMatrix);
        projectionMatrix.mul(worldMatrix);

        program.setUniform("projection", projectionMatrix);
        program.setUniform("colour", mesh.getMaterial().getAmbient());
        program.setUniform("texture_sampler", 0);
        program.setUniform("hasTexture", 0);

        mesh.render();
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
    }


}
