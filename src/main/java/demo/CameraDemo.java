package demo;

import graphic.MouseEvent;
import graphic.Window;
import obj.Camera;
import obj.GameObj;
import obj.GrassBlock;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @Author Gq
 * @Date 2020/12/8 20:19
 * @Version 1.0
 **/
public class CameraDemo {
    private Window window;
    private List<GameObj> gameObjList;
    private Camera camera;
    private MouseEvent mouseEvent;

    public static void main(String[] args) {
        new CameraDemo().run();
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
        camera = new Camera();
        mouseEvent = new MouseEvent();
        mouseEvent.init(window);

//        glfwSetKeyCallback(window.getWindow(), (window, key, scancode, action, mods) -> {
//            if (key == GLFW_KEY_W && action == GLFW_PRESS) {
//                cameraInc.z = -1;
//            } else if (key == GLFW_KEY_S && action == GLFW_PRESS){
//                cameraInc.z = 1;
//            }
//        });
    }

    private final Vector3f cameraInc = new Vector3f();
    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private void input(){
        cameraInc.set(0,0,0);
        if (glfwGetKey(window.getWindow(), GLFW_KEY_W) == GLFW_PRESS) {
            cameraInc.z = -1;
        } else if (glfwGetKey(window.getWindow(), GLFW_KEY_S) == GLFW_PRESS) {
            cameraInc.z = 1;
        }
        if (glfwGetKey(window.getWindow(), GLFW_KEY_A) == GLFW_PRESS) {
            cameraInc.x = -1;
        } else if (glfwGetKey(window.getWindow(), GLFW_KEY_D) == GLFW_PRESS) {
            cameraInc.x = 1;
        }
        if (glfwGetKey(window.getWindow(), GLFW_KEY_Z) == GLFW_PRESS) {
            cameraInc.y = -1;
        } else if (glfwGetKey(window.getWindow(), GLFW_KEY_X) == GLFW_PRESS) {
            cameraInc.y = 1;
        }
        //修改相机
        camera.movePosition(
                cameraInc.x * CAMERA_POS_STEP,
                cameraInc.y * CAMERA_POS_STEP,
                cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        mouseEvent.input(window);
        if (mouseEvent.isRightButtonPressed()) {
            Vector2f rotVec = mouseEvent.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }

    private float deltaY;
    private void loop(){
        while (!window.isClosed()) {
            window.clean();

            input();

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
                o.render(window, camera);
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
