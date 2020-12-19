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
public abstract class Mesh {

    public abstract void render();

    public abstract void cleanup();
}
