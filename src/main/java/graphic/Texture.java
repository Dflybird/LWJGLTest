package graphic;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @Author Gq
 * @Date 2020/12/8 12:16
 * @Version 1.0
 **/
public class Texture {
    Logger logger = LoggerFactory.getLogger(Texture.class);

    private int textureId;

    private int width;
    private int height;

    public Texture(String textureFile) {
        init(textureFile);
    }

    public void cleanup(){
        glDeleteTextures(textureId);
    }

    private void init(String textureFile){
        File file = new File(textureFile);
        try {
            PNGDecoder decoder = new PNGDecoder(new FileInputStream(file));
            this.width = decoder.getWidth();
            this.height = decoder.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                    decoder.getHeight()*decoder.getWidth()*4);
            decoder.decode(byteBuffer, 4*decoder.getWidth(), PNGDecoder.Format.RGBA);
            byteBuffer.flip();


            //创建纹理
            textureId = glGenTextures();

            //绑定纹理
            glBindTexture(GL_TEXTURE_2D, textureId);

            //解压缩RGBA
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            //上传纹理数据
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);

            //生成mipmap
            glGenerateMipmap(GL_TEXTURE_2D);

        } catch (IOException e) {
            logger.error("Failed to load texture file");
        }
    }

    public int getTextureId() {
        return textureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
