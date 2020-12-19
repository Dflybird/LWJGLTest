package game;

import graphic.MouseEvent;
import graphic.Window;
import obj.Camera;
import obj.GameObj;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Gq
 * @Date 2020/12/17 21:11
 * @Version 1.0
 **/
public abstract class GameEngine {

    protected Window window;
    protected List<GameObj> gameObjList;
    protected Camera camera;
    protected MouseEvent mouseEvent;

    public void run(){
        init();
        while (!window.isClosed()) {
            window.clean();

            input();
            step();

            window.render();
        }
        render();
    }

    protected void init(){
        window = new Window(600, 600);
        window.init();

        gameObjList = new ArrayList<>();
        camera = new Camera();

        mouseEvent = new MouseEvent();
        mouseEvent.init(window);
    }

    protected abstract void input();

    protected abstract void step();

    protected abstract void render();

    protected void cleanup(){
        for (GameObj o : gameObjList) {
            o.cleanup();
        }
        window.cleanup();
    }

}
