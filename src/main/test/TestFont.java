import graphic.FontTexture;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author Gq
 * @Date 2021/1/4 21:15
 * @Version 1.0
 **/
public class TestFont {
    @Test
    public void testBuildTexture(){
        Font font = new Font("宋体", Font.PLAIN, 20);

        Charset charset =  StandardCharsets.UTF_8;
        try {
            FontTexture fontTexture = new FontTexture(font, charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test(){
        BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
        Graphics img = image.getGraphics();
        img.setFont(new Font("宋体", Font.BOLD, 20));
        img.drawString("你好我好", 30, 30);
        img.drawString("\u5b8b\u4f53", 30, 80);
        try {
            ImageIO.write(image, "JPEG", new FileOutputStream("texture/test.PNG"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
