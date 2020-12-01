import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

/**
 * @Author: gq
 * @Date: 2020/12/1 16:20
 */
public class Mesh {

    private int shaderProgramId;

    //VBOs
    private int positionVboId;
    private int indexVboId;
    private int colourVboId;

    //VAO
    private int vaoId;

    private int vertexCount;

    public Mesh(int shaderProgramId, float[] positions, int[] indices, float[] colours) {
        this.shaderProgramId = shaderProgramId;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        initPositionBuffer(positions);
        initIndexBuffer(indices);
        initColour(colours);

        //解绑VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        //解绑VAO
        glBindVertexArray(0);
    }

    public void render(){

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(glGetAttribLocation(shaderProgramId, "position"));
        glEnableVertexAttribArray(glGetAttribLocation(shaderProgramId, "inColour"));
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
//        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void clearUp(){
        glDisableVertexAttribArray(0);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDeleteBuffers(positionVboId);
        glDeleteBuffers(indexVboId);
        glDeleteBuffers(colourVboId);
    }

    private void initPositionBuffer(float[] positions){
        vertexCount = positions.length / 3;
        FloatBuffer verticesBuffer = null;
        try {
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            verticesBuffer.put(positions).flip();
            positionVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(glGetAttribLocation(shaderProgramId, "position"), 3, GL_FLOAT, false, 0, 0);
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
            glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
            glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(glGetAttribLocation(shaderProgramId, "inColour"), 3, GL_FLOAT, false, 0, 0);
        } finally {
            if (colourBuffer != null) {
                MemoryUtil.memFree(colourBuffer);
            }
        }
    }
}
