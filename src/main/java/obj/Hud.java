package obj;

import config.Constant;
import graphic.FontTexture;
import graphic.Window;
import org.joml.Vector3f;
import shader.ShaderProgram;

import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author: gq
 * @Date: 2020/12/31 17:31
 */
public class Hud {
    private static final int FONT_COL = 16;

    private static final int FONT_ROW = 16;

    private static final String FONT_TEXTURE = "texture/font_texture.png";

    private final TextItem statusTextItem;
    private final ObjItem compassItem;
    private final TextItem chineseTextItem;

    private final GameObj[] gameObjArr;

    public Hud(String text, ShaderProgram program) {
        this.chineseTextItem = new TextItem("宋体->T", program);
        this.statusTextItem = new TextItem(FONT_TEXTURE, text, FONT_COL, FONT_ROW, program);
        this.compassItem = new ObjItem(Constant.DEFAULT_RESOURCES_DIR + "/models/compass.obj", program);
        compassItem.setScale(40f);
        compassItem.setRotation(new Vector3f(0,0,180));

        gameObjArr = new GameObj[]{statusTextItem, compassItem, chineseTextItem};
    }

    public void updateSize(Window window) {
        this.chineseTextItem.setTranslation(new Vector3f(window.getWidth() - 100f, window.getHeight() - 50f, 0));
        this.statusTextItem.setTranslation(new Vector3f(10f, window.getHeight() - 50f, 0));
        this.compassItem.setTranslation(new Vector3f(window.getWidth() - 40f, 50f, 0));
    }

    public void setText(String text) {
        this.statusTextItem.setText(text);
    }

    public void setCompassRotation(float angle){
        compassItem.setRotation(new Vector3f(0, 0, 180 + angle));
    }

    public GameObj[] getGameObjArr() {
        return gameObjArr;
    }
}
