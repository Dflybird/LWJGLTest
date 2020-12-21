package graphic;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @Author Gq
 * @Date 2020/12/17 21:07
 * @Version 1.0
 **/
public class ObjMesh extends Mesh {
    private final int position;
    private final int inColour;

    //VBOs
    private int positionVboId;
    private int indexVboId;
    private int textureVboId;
    private int normalVboId;

    //VAO
    private final int vaoId;

    private final int vertexCount;

    public ObjMesh(int shaderProgramId, float[] positions, int[] indices, float[] textureCoordinate, float[] normals) {
        this.position = glGetAttribLocation(shaderProgramId, "position");
        this.inColour = glGetAttribLocation(shaderProgramId, "inColour");
        this.vertexCount = indices.length;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        initPositionBuffer(positions);
        initIndexBuffer(indices);

        //解绑VAO
        glBindVertexArray(0);
    }
    @Override
    public void render() {
//        // Activate firs texture bank
//        glActiveTexture(GL_TEXTURE0);
//        // Bind the texture
//        glBindTexture(GL_TEXTURE_2D, );

        glBindVertexArray(vaoId);

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    @Override
    public void cleanup() {
        glDisableVertexAttribArray(position);
        glDisableVertexAttribArray(inColour);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(positionVboId);
        glDeleteBuffers(indexVboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void initPositionBuffer(float[] positions){
        FloatBuffer verticesBuffer = null;
        try {
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            verticesBuffer.put(positions).flip();
            positionVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(position, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(position);
            //解绑VBOs
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        } finally {
            //释放缓存
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer);
            }
        }
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

    private void initTextureBuffer(float[] textureCoordinate) {
        FloatBuffer textureBuffer = null;
        try {
            textureBuffer = MemoryUtil.memAllocFloat(textureCoordinate.length);
            textureBuffer.put(textureCoordinate).flip();
            textureVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(inColour, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(inColour);
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
            glVertexAttribPointer(inColour, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(inColour);
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
