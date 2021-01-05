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
            FontTexture fontTexture = new FontTexture(font, charset, "宋体");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test(){
        BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D img = image.createGraphics();
        img.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        img.setFont(new Font("宋体", Font.PLAIN, 20));
        FontMetrics fontMetrics = img.getFontMetrics();
        img.drawString("你好我好", 0, fontMetrics.getAscent());
        try {
            ImageIO.write(image, "png", new FileOutputStream("texture/test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
