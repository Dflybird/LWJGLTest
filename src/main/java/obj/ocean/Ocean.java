package obj.ocean;

import graphic.Material;
import graphic.Window;
import obj.Camera;
import obj.GameObj;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import shader.ShaderProgram;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * 使用右手坐标系，XOZ是水平面
 * @Author Gq
 * @Date 2021/2/4 18:49
 * @Version 1.0
 **/
public class Ocean extends GameObj {
    private int vertexPosition;
    private int textureCoordinate;
    private int vertexNormal;

    //VBOs
    private int positionVboId;
    private int indexVboId;
    private int textureVboId;
    private int normalVboId;

    //VAO
    private int vaoId;

    private ShaderProgram program;
    private Material material;

    /** 波一个周期时间长度，将连续色散曲线量化，T越大越接近连续色散曲线，单位：秒 */
    private static final float T = 200;
    private static final float PI = (float) Math.PI;
    private static final float SEA_LEVEL = 0f;
    private float Lx;
    private float Lz;
    /** 2的幂 */
    private int N;
    private int M;
    private int NPlus1;
    private int MPlus1;

    /** 重力加速度 */
    private float g;
    private Wind wind;
    /** 菲利普斯常数 */
    private float A;

    private OceanVertex vertices[];

    private int[] glIndices;
    private float[] glVertices;
    private float[] glNormals;


    public Ocean(float Lx, float Lz, int N, int M, Wind wind, float A) {
        this.Lx = Lx;
        this.Lz = Lz;
        this.N = N;
        this.M = M;
        this.wind = wind;
        this.A = A;
        this.g = 9.81f;
        this.NPlus1 = N + 1;
        this.MPlus1 = M + 1;
        this.glIndices = new int[N * M * 10];
        this.glVertices = new float[NPlus1 * MPlus1 * 3];
        this.glNormals = new float[NPlus1 * MPlus1 * 3];

        vertices = new OceanVertex[NPlus1 * MPlus1];
        int index;
        Complex hTilde0, hTilde0Conj;
        for (int m = 0; m < MPlus1; m++) {
            for (int n = 0; n < NPlus1; n++) {
                index = m * NPlus1 + n;

                hTilde0 = hTilde0(n, m);
                hTilde0Conj = hTilde0(-n, -m).conjugate();

                OceanVertex vertex = new OceanVertex();

                vertex.hx = (float) hTilde0.re();
                vertex.hy = (float) hTilde0.im();

                vertex.cx = (float) hTilde0Conj.re();
                vertex.cy = (float) hTilde0Conj.im();

                vertex.ox = vertex.vx = (n - N / 2f) * Lx / N;
                vertex.oy = vertex.vy = SEA_LEVEL;
                vertex.oz = vertex.vz = (m - M / 2f) * Lz / M;

                vertex.nx = 0;
                vertex.ny = 1;
                vertex.nz = 0;

                vertices[index] = vertex;

                glVertices[index * 3] = vertex.vx;
                glVertices[index * 3 + 1] = vertex.vy;
                glVertices[index * 3 + 2] = vertex.vz;
                glNormals[index * 3] = vertex.nx;
                glNormals[index * 3 + 1] = vertex.ny;
                glNormals[index * 3 + 2] = vertex.nz;
            }
        }

        int count = 0;
        for (int m = 0; m < M; m++) {
            for (int n = 0; n < N; n++) {
                index = m * NPlus1 + n;

                //index+NPlus1 -- index+NPlus1+1
                //    |          /      |
                //    |        /        |
                //    |      /          |
                //    |    /            |
                //  index ----------- index+1
                glIndices[count++] = index;
                glIndices[count++] = index + NPlus1;
                glIndices[count++] = index + NPlus1 + 1;
                glIndices[count++] = index;
                glIndices[count++] = index + NPlus1 + 1;
                glIndices[count++] = index + 1;
            }
        }

        init();
    }

    public void init() {
        program = new ShaderProgram();
        program.init("ocean.vert", "ocean.frag");
        program.createUniform("world");
        program.createUniform("view");
        program.createUniform("projection");
        program.createUniform("water");
        program.createUniform("light_position");

        this.vertexPosition = glGetAttribLocation(program.getProgramId(), "vertexPosition");
        this.textureCoordinate = glGetAttribLocation(program.getProgramId(), "textureCoordinate");
        this.vertexNormal = glGetAttribLocation(program.getProgramId(), "vertexNormal");

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        initPositionBuffer(glVertices);
        initIndexBuffer(glIndices);
        initNormalBuffer(glNormals);
    }


    @Override
    public void render(Window window, Camera camera) {
        program.bind();
        float fov = (float) Math.toRadians(60);
        float zNear = 0.01f;
        float zFar = 1000f;
        float aspectRatio = (float) window.getWidth()/(float) window.getHeight();

        Matrix4f projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, zNear, zFar);

        Matrix4f worldMatrix = new Matrix4f()
                .translate(translation)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(scale);
//        Matrix4f worldMatrix = new Matrix4f().identity();

        Vector3f cameraPos = camera.getPosition();
        Vector3f cameraRot = camera.getRotation();
        Matrix4f viewMatrix = new Matrix4f()
                .rotateX((float) Math.toRadians(cameraRot.x))
                .rotateY((float) Math.toRadians(cameraRot.y))
                .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        program.setUniform("projection", projectionMatrix);
        program.setUniform("world", worldMatrix);
        program.setUniform("view", viewMatrix);
        program.setUniform("water", 0);
        program.setUniform("light_position", new Vector3f(1000.0f, 100.0f, -1000.0f));

        //render
        glBindVertexArray(vaoId);

        int index;
        for (int m = 0; m < MPlus1; m++) {
            for (int n = 0; n < NPlus1; n++) {
                index = m * NPlus1 + n;
                OceanVertex vertex = vertices[index];

                glVertices[index * 3] = vertex.vx;
                glVertices[index * 3 + 1] = vertex.vy;
                glVertices[index * 3 + 2] = vertex.vz;
                glNormals[index * 3] = vertex.nx;
                glNormals[index * 3 + 1] = vertex.ny;
                glNormals[index * 3 + 2] = vertex.nz;
            }
        }

        FloatBuffer verticesBuffer = null;
        FloatBuffer normalsBuffer = null;
        try {
            verticesBuffer = MemoryUtil.memAllocFloat(glVertices.length);
            verticesBuffer.put(glVertices).flip();
            glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

            glVertexAttribPointer(vertexPosition, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(vertexPosition);

            normalsBuffer = MemoryUtil.memAllocFloat(glNormals.length);
            normalsBuffer.put(glNormals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
            glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(vertexNormal, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(vertexNormal);

            //解绑VBOs
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        } finally {
            //释放缓存
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer);
            }
            if (normalsBuffer != null) {
                MemoryUtil.memFree(normalsBuffer);
            }
        }


        //平铺
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                worldMatrix.identity();
                worldMatrix.scale(new Vector3f(5,5,5));
                worldMatrix.translate(new Vector3f(Lx * i, 0, Lz * -j));
                program.setUniform("world", worldMatrix);
                glDrawElements(GL_TRIANGLES, N * M * 6, GL_UNSIGNED_INT, 0);
            }
        }
//        glDrawElements(GL_TRIANGLES, N * M * 6, GL_UNSIGNED_INT, 0);


        glBindVertexArray(0);

        program.unbind();
    }

    @Override
    public void cleanup() {
        program.cleanup();
    }


    /**
     * 计算色散频率
     * @param n
     * @param m
     * @return
     */
    public float dispersion(int n, int m) {
        float w0 = 2 * PI / T;
        float kx = PI * (2 * n - N) / Lx;
        float kz = PI * (2 * m - M) / Lz;
        float k = (float) Math.sqrt(kx * kx + kz * kz);
        float w = (float) Math.sqrt(g * k);
        return (float) (Math.floor(w / w0) * w0);
    }

    /**
     * 计算菲利普斯波谱
     * @return
     */
    public float phillips(int n, int m) {
        float kx = PI * (2 * n - N) / Lx;
        float kz = PI * (2 * m - M) / Lz;
        Vector2f k = new Vector2f(kx, kz);
        float kL = k.length();
        if (kL < 0.000001) {
            return 0;
        }

        float k2 = kL * kL;
        float k4 = k2 * k2;
        float kw = k.normalize().dot(wind.direction);
        float kw2 = kw * kw;

        float L = wind.velocity * wind.velocity / g;
        float L2 = L * L;

        //衰减
        float damping = 0.001f;
        float l2 = L2 * damping * damping;

        return (float) (A* Math.exp(-1f / (k2 * L2)) / k4 * kw2 * Math.exp(-k2 * l2));
    }

    public Complex hTilde0(int n, int m) {
        Random random = new Random(System.currentTimeMillis());
        double re = random.nextGaussian();
        double im = random.nextGaussian();
        Complex complex = new Complex(re, im);
        return complex.scale(Math.sqrt(phillips(n, m) / 2));
    }

    /**
     *
     * @param t 时间，单位：秒
     * @param n
     * @param m
     * @return
     */
    public Complex hTilde(float t, int n, int m) {
        //计算具体的点
        int index = m * NPlus1 + n;
        OceanVertex v = vertices[index];

        Complex hTilde0 = new Complex(v.hx, v.hy);
        Complex hTilde0Conj = new Complex(v.cx, v.cy);

        //TODO 简化计算？
        float omega = dispersion(n, m) * t;

        float cosOmega = (float) Math.cos(omega);
        float sinOmega = (float) Math.sin(omega);

        //$e^{ix}=\cos x+i\sin x$
        Complex e0 = new Complex(cosOmega, sinOmega);
        Complex e1 = new Complex(cosOmega, -sinOmega);

        Complex res0 = hTilde0.times(e0);
        Complex res1 = hTilde0Conj.times(e1);
        return res0.plus(res1);
    }

    public HeightDisplaceNormal evaluateHDN(Vector2f x, float t) {
        Complex height = new Complex(0,0);
        Vector2f displacement = new Vector2f(0,0);
        Vector3f normal = new Vector3f(0,0,0);

        Complex c, hTildeC;
        float kDotX;
        for (int m = 0; m < M; m++) {
            float kz = PI * (2 * m - M) / Lz;
            for (int n = 0; n < N; n++) {
                float kx = PI * (2 * n - N) / Lx;
                Vector2f k = new Vector2f(kx, kz);
                float kL = k.length();
                kDotX = k.dot(x);

                float cosKX = (float) Math.cos(kDotX);
                float sinKX = (float) Math.sin(kDotX);
                //$e^{ix}=\cos x+i\sin x$
                c = new Complex(cosKX, sinKX);

                hTildeC = hTilde(t, n, m).times(c);

                height.plusEqual(hTildeC);
                normal.add(-kx * (float) hTildeC.im(), 0, -kz * (float) hTildeC.im());

                if (kL < 0.000001f) {
                    continue;
                }
                displacement.add(kx / kL * (float) hTildeC.im(), kz / kL * (float) hTildeC.im());
            }
        }

        HeightDisplaceNormal hdn = new HeightDisplaceNormal();
        hdn.height = height;
        hdn.displacement = displacement;
        hdn.normal = new Vector3f(0,1,0).sub(normal).normalize();
        return hdn;
    }

    public void evaluateWaves(float t) {
        //坐标x平移方向
        float lambda = -1;
        int index;
        Vector2f x;
        HeightDisplaceNormal hdn;
        OceanVertex vertex, border;

        for (int m = 0; m < M; m++) {
            for (int n = 0; n < N; n++) {
                index = m * NPlus1 + n;

                vertex = vertices[index];
                x = new Vector2f(vertex.vx, vertex.vz);

                hdn = evaluateHDN(x, t);

                //x处海浪高度
                vertex.vy = (float) hdn.height.re();
                //x处海浪水平位移
                vertex.vx = vertex.ox + lambda * hdn.displacement.x;
                vertex.vz = vertex.oz + lambda * hdn.displacement.y;

                //法向量
                vertex.nx = hdn.normal.x;
                vertex.ny = hdn.normal.y;
                vertex.nz = hdn.normal.z;

                //将最后一个边界点赋值为当前点，平滑N*M海面范围边界
                if (n == 0 && m == 0) {
                    border = vertices[index + M * NPlus1 + N];
                    border.vy = (float) hdn.height.re();
                    border.vx = border.ox + lambda * hdn.displacement.x;
                    border.vz = border.oz + lambda * hdn.displacement.y;

                    border.nx = hdn.normal.x;
                    border.ny = hdn.normal.y;
                    border.nz = hdn.normal.z;
                }
                if (n == 0) {
                    border = vertices[index + N];
                    border.vy = (float) hdn.height.re();
                    border.vx = border.ox + lambda * hdn.displacement.x;
                    border.vz = border.oz + lambda * hdn.displacement.y;

                    border.nx = hdn.normal.x;
                    border.ny = hdn.normal.y;
                    border.nz = hdn.normal.z;
                }
                if (m == 0) {
                    border = vertices[index + M * NPlus1];
                    border.vy = (float) hdn.height.re();
                    border.vx = border.ox + lambda * hdn.displacement.x;
                    border.vz = border.oz + lambda * hdn.displacement.y;

                    border.nx = hdn.normal.x;
                    border.ny = hdn.normal.y;
                    border.nz = hdn.normal.z;
                }
            }
        }
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

    public static class Wind {
        float velocity;
        Vector2f direction;

        public Wind(float velocity, Vector2f direction) {
            this.velocity = velocity;
            this.direction = direction.normalize();
        }
    }

    public static class OceanVertex {
        float vx, vy, vz;   //vertex
        float nx, ny, nz;   //normal
        float hx, hy, hz;   //hTilde0
        float cx, cy, cz;   //hTilde0 conjugate
        float ox, oy, oz;   //original position
    }

    public static class HeightDisplaceNormal{
        Complex height; //wave height
        Vector2f displacement;
        Vector3f normal;
    }
}
