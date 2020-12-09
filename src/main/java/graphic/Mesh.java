package graphic;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

/**
 * 管理显卡顶点，颜色等的缓存空间
 * @Author: gq
 * @Date: 2020/12/1 16:20
 */
public class Mesh {

    private int shaderProgramId;

    private int position;
    private int inColour;

    //VBOs
    private int positionVboId;
    private int indexVboId;
    private int colourVboId;
    private int textureVboId;

    //VAO
    private int vaoId;

    private int vertexCount;

    private final boolean isTex;
    private Texture texture;

    public Mesh(int shaderProgramId, float[] positions, int[] indices, float[] colours) {
        this.shaderProgramId = shaderProgramId;
        this.position = glGetAttribLocation(shaderProgramId, "position");
        this.inColour = glGetAttribLocation(shaderProgramId, "inColour");
        this.vertexCount = indices.length;
        this.isTex = false;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        initPositionBuffer(positions);
        initIndexBuffer(indices);
        initColour(colours);

        //解绑VAO
        glBindVertexArray(0);
    }

    public Mesh(int shaderProgramId, float[] positions, int[] indices, Texture texture, float[] textureCoordinate) {
        this.shaderProgramId = shaderProgramId;
        this.position = glGetAttribLocation(shaderProgramId, "position");
        this.inColour = glGetAttribLocation(shaderProgramId, "inColour");
        this.vertexCount = indices.length;
        this.isTex = true;
        this.texture = texture;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        initPositionBuffer(positions);
        initIndexBuffer(indices);
        initTexture(textureCoordinate);

        //解绑VAO
        glBindVertexArray(0);
    }

    public void render(){

        if (isTex) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
        }

        glBindVertexArray(vaoId);

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    public void cleanup(){
        glDisableVertexAttribArray(position);
        glDisableVertexAttribArray(inColour);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(positionVboId);
        glDeleteBuffers(indexVboId);
        if (isTex) {
            glDeleteBuffers(textureVboId);
        } else {
            glDeleteBuffers(colourVboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
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

    private void initColour(float[] colours) {
        FloatBuffer colourBuffer = null;
        try {
            colourBuffer = MemoryUtil.memAllocFloat(colours.length);
            colourBuffer.put(colours).flip();
            colourVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, colourVboId);
            glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(inColour, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(inColour);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        } finally {
            if (colourBuffer != null) {
                MemoryUtil.memFree(colourBuffer);
            }
        }
    }

    private void initTexture(float[] textureCoordinate) {
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
}
