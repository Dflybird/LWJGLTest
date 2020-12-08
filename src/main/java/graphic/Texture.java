package graphic;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * @Author Gq
 * @Date 2020/12/8 12:16
 * @Version 1.0
 **/
public class Texture {
    Logger logger = LoggerFactory.getLogger(Texture.class);

    private int textureId;

    public Texture(String textureFile) {


    }

    public void cleanup(){

    }

    private void init(String textureFile){
        File file = new File(textureFile);
        try {
            PNGDecoder decoder = new PNGDecoder(new FileInputStream(file));
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                    decoder.getHeight()*decoder.getWidth()*4);
            decoder.decode(byteBuffer, 4*decoder.getWidth(), PNGDecoder.Format.RGBA);
            byteBuffer.flip();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //创建纹理
        textureId = glGenTextures();

        //绑定纹理
        glBindTexture(GL_TEXTURE_2D, textureId);

        //解压缩RGBA
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //上传纹理数据
    }

}
