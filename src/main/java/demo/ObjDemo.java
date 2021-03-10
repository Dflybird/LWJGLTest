package demo;

import config.Constant;
import game.GameEngine;
import obj.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.*;

/**
 * @Author Gq
 * @Date 2020/12/17 21:08
 * @Version 1.0
 **/
public class ObjDemo extends GameEngine {
    private float deltaY;

    @Override
    protected void init() {
        super.init();
        gameObjList.add(new Cube(new Vector3f(-0.5f,0,-1.5f), new Vector3f(0,0,0),0.2f));
        gameObjList.add(new Bunny(new Vector3f(0.5f,0,-1.5f), new Vector3f(0,0,0),0.3f));
        gameObjList.add(new Boat(new Vector3f(0f,2f,0f), new Vector3f(0,0,0),0.3f));
        gameObjList.add(new LocalModel(Constant.DEFAULT_RESOURCES_DIR + "/models/island.obj",
                new Vector3f(0f,0,-3f), new Vector3f(0,0,0),0.01f));
    }

    private final Vector3f cameraInc = new Vector3f();
    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private boolean isRGBA = true;
    @Override
    protected void input() {
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

        if (glfwGetKey(window.getWindow(), GLFW_KEY_O) == GLFW_PRESS) {
            if (isRGBA) {
                isRGBA = false;
                glPolygonMode( GL_FRONT_AND_BACK, GL_LINE);
            }
        }

        if (glfwGetKey(window.getWindow(), GLFW_KEY_P) == GLFW_PRESS) {
            if (!isRGBA) {
                isRGBA = true;
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            }
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

    @Override
    protected void step() {
        //自动旋转
        deltaY += 1.5f;
        if ( deltaY > 360 ) {
            deltaY = 0;
        }
    }

    @Override
    protected void render() {
        //平移，旋转，缩放
        Vector3f rotation = new Vector3f(30,deltaY,0);   //角度


        for (GameObj o : gameObjList) {
//            o.setRotation(rotation);
            o.render(window, camera);
        }
    }

    public static void main(String[] args) {
        new ObjDemo().run();
    }
}
