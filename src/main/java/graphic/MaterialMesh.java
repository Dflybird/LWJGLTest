package graphic;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @Author Gq
 * @Date 2020/12/17 21:07
 * @Version 1.0
 **/
public class MaterialMesh extends Mesh {
    private final int vertexPosition;
    private final int textureCoordinate;
    private final int vertexNormal;

    //VBOs
    private int positionVboId;
    private int indexVboId;
    private int textureVboId;
    private int normalVboId;

    //VAO
    private final int vaoId;

    private final int vertexCount;

    private Material material;

    private float[] pos;

    public MaterialMesh(int shaderProgramId, float[] positions, int[] indices, float[] textureCoordinate, float[] normals) {
        this.vertexPosition = glGetAttribLocation(shaderProgramId, "vertexPosition");
        this.textureCoordinate = glGetAttribLocation(shaderProgramId, "textureCoordinate");
        this.vertexNormal = glGetAttribLocation(shaderProgramId, "vertexNormal");
        this.vertexCount = indices.length;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        pos = positions;
        initPositionBuffer(positions);
        initIndexBuffer(indices);
        initTextureBuffer(textureCoordinate);
        initNormalBuffer(normals);

        //解绑VAO
        glBindVertexArray(0);
    }
    @Override
    public void render() {
        Texture texture = material.getTexture();
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
        }

        glBindVertexArray(vaoId);

//
//        for (int i = 0; i < pos.length; i++) {
//            pos[i] *= 1.001;
//        }
//        FloatBuffer verticesBuffer = null;
//        try {
//            verticesBuffer = MemoryUtil.memAllocFloat(pos.length);
//            verticesBuffer.put(pos).flip();
//            glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
//            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
//            glVertexAttribPointer(vertexPosition, 3, GL_FLOAT, false, 0, 0);
//            glEnableVertexAttribArray(vertexPosition);
//
//
//            //解绑VBOs
//            glBindBuffer(GL_ARRAY_BUFFER, 0);
//        } finally {
//            //释放缓存
//            if (verticesBuffer != null) {
//                MemoryUtil.memFree(verticesBuffer);
//            }
//        }

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    @Override
    public void cleanup() {
        glDisableVertexAttribArray(vertexPosition);
        glDisableVertexAttribArray(textureCoordinate);
        glDisableVertexAttribArray(vertexNormal);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(positionVboId);
        glDeleteBuffers(indexVboId);
        glDeleteBuffers(textureVboId);
        glDeleteBuffers(normalVboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);

        glBindTexture(GL_TEXTURE_2D, 0);

        material.getTexture().cleanup();
    }

    private void initPositionBuffer(float[] positions){
        FloatBuffer verticesBuffer = null;
        try {
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            verticesBuffer.put(positions).flip();
            positionVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(vertexPosition, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(vertexPosition);
            //解绑VBOs
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        } finally {
            //释放缓存
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer);
            }
        }
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    private void initIndexBuffer(int[] indices){
        IntBuffer indicesBuffer = null;
        try {
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            indexVboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        } finally {
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    private void initTextureBuffer(float[] textureCoordinates) {
        FloatBuffer textureBuffer = null;
        try {
            textureBuffer = MemoryUtil.memAllocFloat(textureCoordinates.length);
            textureBuffer.put(textureCoordinates).flip();
            textureVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(textureCoordinate, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(textureCoordinate);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        } finally {
            if (textureBuffer != null) {
                MemoryUtil.memFree(textureBuffer);
            }
        }
    }

    private void initNormalBuffer(float[] normals) {
        FloatBuffer normalsBuffer = null;

        try {
            normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            normalsBuffer.put(normals).flip();
            normalVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
            glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(vertexNormal, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(vertexNormal);
            //解绑VBOs
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        } finally {
            //释放缓存
            if (normalsBuffer != null) {
                MemoryUtil.memFree(normalsBuffer);
            }
        }
    }

}
