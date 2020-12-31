package obj;

import graphic.*;
import org.joml.Matrix4f;
import shader.ShaderProgram;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: gq
 * @Date: 2020/12/30 14:02
 */
public class TextItem extends GameObj {
    private static final float Z_POS = 0.0f;
    private static final int VERTICES_PER_QUAD = 4;

    private String text;
    private final int numCol;
    private final int numRow;
    private final ShaderProgram program;
    private MaterialMesh mesh;

    public TextItem(String fontFile, String text, int numCol, int numRow, ShaderProgram program) {
        this.text = text;
        this.numCol = numCol;
        this.numRow = numRow;
        this.program = program;
        Texture texture = new Texture(fontFile);
        this.mesh = buildMesh(texture);

        program.createUniform("projection");
        program.createUniform("colour");
        program.createUniform("texture_sampler");
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

        mesh.render();
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
    }

    private MaterialMesh buildMesh(Texture texture){
        byte[] chars = text.getBytes(StandardCharsets.ISO_8859_1);
        int numChars = chars.length;

        List<Float> positions = new ArrayList();
        List<Float> textCords = new ArrayList();
        float[] normals   = new float[0];
        List<Integer> indices   = new ArrayList();

        float tileWidth = (float) texture.getWidth() / numCol;
        float tileHeight = (float) texture.getHeight() / numRow;

        for (int i = 0; i < numChars; i++) {
            byte currChar = chars[i];
            int col = currChar % numCol;
            int row = currChar / numCol;


            // 0 --- 3
            // | \   |
            // |   \ |
            // 1 --- 2
            //position 0
            positions.add((float)i*tileWidth); // x
            positions.add(0.0f); //y
            positions.add(Z_POS); //z
            textCords.add((float)col / (float)numCol);
            textCords.add((float)row / (float)numRow);

            //position 1
            positions.add((float)i*tileWidth); // x
            positions.add(tileHeight); //y
            positions.add(Z_POS); //z
            textCords.add((float)col / (float)numCol);
            textCords.add((float)(row + 1) / (float)numRow);

            //position 2
            positions.add((float)i*tileWidth + tileWidth); // x
            positions.add(tileHeight); //y
            positions.add(Z_POS); //z
            textCords.add((float)(col + 1)/ (float)numCol);
            textCords.add((float)(row + 1) / (float)numRow);

            //position 3
            positions.add((float)i*tileWidth + tileWidth); // x
            positions.add(0.0f); //y
            positions.add(Z_POS); //z
            textCords.add((float)(col + 1)/ (float)numCol);
            textCords.add((float)row / (float)numRow);

            // Add indices
            //0 1 2
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 1);
            indices.add(i*VERTICES_PER_QUAD + 2);
            //3 0 2
            indices.add(i*VERTICES_PER_QUAD + 3);
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);

        }

        float[] positionArr = new float[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            positionArr[i] = positions.get(i);
        }
        float[] textCordsArr = new float[textCords.size()];
        for (int i = 0; i < textCords.size(); i++) {
            textCordsArr[i] = textCords.get(i);
        }
        int[] indicesArr = indices.stream().mapToInt((Integer i) -> i).toArray();

        MaterialMesh mesh = new MaterialMesh(program.getProgramId(), positionArr, indicesArr, textCordsArr, normals);
        mesh.setMaterial(new Material(texture));
        return mesh;
    }
}
