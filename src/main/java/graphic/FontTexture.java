package graphic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @Author Gq
 * @Date 2021/1/4 20:38
 * @Version 1.0
 **/
public class FontTexture {
    private static final String IMAGE_FORMAT = "PNG";

    private static final int CHAR_PADDING = 2;

    private Font font;
    private Charset charset;

    private HashMap<Character, CharInfo> charMap;

    private int height;

    private int width;

    public FontTexture(Font font, String charsetName) throws IOException {
        this(font, Charset.forName(charsetName));
    }

    public FontTexture(Font font, Charset charset) throws IOException {
        this.font = font;
        this.charset = charset;
        charMap = new HashMap<>();

        buildTexture();
    }

    private String getAllAvailableChars(Charset charset) {
        CharsetEncoder encoder = charset.newEncoder();
        StringBuilder builder = new StringBuilder();
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (encoder.canEncode(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private void buildTexture() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setFont(font);
        FontMetrics fontMetrics = graphics2D.getFontMetrics();

        String allChars = getAllAvailableChars(charset);
        this.width = 0;
        this.height = 0;
        //迭代字符集中每个字符更新字符纹理的长宽和内容
        for (char c : allChars.toCharArray()) {
            CharInfo charInfo = new CharInfo(width, fontMetrics.charWidth(c));
            charMap.put(c, charInfo);
            //在字体右边加padding
            width += charInfo.getWidth() + CHAR_PADDING;
        }
        height = fontMetrics.getHeight();
        graphics2D.dispose();

        //创建匹配此字符集的纹理图形
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics2D = bufferedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setFont(font);
        fontMetrics = graphics2D.getFontMetrics();
        graphics2D.setColor(Color.BLACK);
        int startX = 0;

//        for (char c : allChars.toCharArray()) {
//            CharInfo charInfo = charMap.get(c);
//            graphics2D.drawString("" + c, startX, fontMetrics.getAscent());
//            //在字体左边加padding
//            startX += charInfo.getWidth() + CHAR_PADDING;
//        }
        graphics2D.drawString("你好", startX, fontMetrics.getAscent());
        graphics2D.dispose();

        ImageIO.write(bufferedImage, IMAGE_FORMAT, new FileOutputStream("texture/temp.PNG"));
    }

    public static class CharInfo{
        //起始坐标
        private final int startX;
        //每个char宽度
        private final int width;

        public CharInfo(int startX, int width) {
            this.startX = startX;
            this.width = width;
        }

        public int getStartX() {
            return startX;
        }

        public int getWidth() {
            return width;
        }
    }
}
