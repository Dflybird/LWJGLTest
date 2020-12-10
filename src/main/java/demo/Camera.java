package demo;

import graphic.Window;
import obj.GameObj;
import obj.GrassBlock;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Gq
 * @Date 2020/12/8 20:19
 * @Version 1.0
 **/
public class Camera {
    private Window window;
    private List<GameObj> gameObjList;

    public static void main(String[] args) {
        new Camera().run();
    }

    private void run(){
        init();
        loop();
        cleanup();
    }

    private void init(){
        gameObjList = new ArrayList<>();
        window = new Window(600, 600);
        window.init();

        gameObjList.add(new GrassBlock(new Vector3f(0,0,0), new Vector3f(0,0,0),0.5f));

    }

    private float deltaY;
    private void loop(){
        while (!window.isClosed()) {
            window.clean();

            //自动旋转
            deltaY += 1.5f;
            if ( deltaY > 360 ) {
                deltaY = 0;
            }

            //平移，旋转，缩放
            Vector3f translation = new Vector3f(0,0,-1.5f);
            Vector3f rotation = new Vector3f(30,deltaY,0);   //角度


            for (GameObj o : gameObjList) {
                o.setTranslation(translation);
                o.setRotation(rotation);
                o.render(window);
            }

            window.render();
        }
    }

    private void cleanup(){
        for (GameObj o : gameObjList) {
            o.cleanup();
        }
        window.cleanup();

    }
}
