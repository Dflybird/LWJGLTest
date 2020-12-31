package obj;

import graphic.Window;
import org.joml.Vector3f;
import shader.ShaderProgram;

/**
 * @Author: gq
 * @Date: 2020/12/31 17:31
 */
public class Hud {
    private static final int FONT_COL = 16;

    private static final int FONT_ROW = 16;

    private static final String FONT_TEXTURE = "texture/font_texture.png";

    private final TextItem statusTextItem;

    public Hud(String text, ShaderProgram program) {
        this.statusTextItem = new TextItem(FONT_TEXTURE, text, FONT_COL, FONT_ROW, program);
    }

//    public void updateSize(Window window) {
//        this.statusTextItem.setTranslation(new Vector3f(10f, window.getHeight() - 50f, 0));
//    }

    public TextItem getStatusTextItem() {
        return statusTextItem;
    }
}
