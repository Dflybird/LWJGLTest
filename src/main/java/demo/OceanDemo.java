package demo;

import game.GameEngine;
import obj.ocean.Ocean;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TimeUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;

/**
 * @Author Gq
 * @Date 2021/2/4 18:40
 * @Version 1.0
 **/
public class OceanDemo extends GameEngine {
    private final Logger logger = LoggerFactory.getLogger(OceanDemo.class);

    private final Vector3f cameraInc = new Vector3f();
    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private boolean isRGBA = true;

    private Ocean ocean;

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

        //修改风力
        if (glfwGetKey(window.getWindow(), GLFW_KEY_N) == GLFW_PRESS) {
            Ocean.Wind wind = ocean.getWind();
            wind.setVelocity(wind.getVelocity()-0.1f);
            ocean.setWind(wind);
        } else if (glfwGetKey(window.getWindow(), GLFW_KEY_M) == GLFW_PRESS) {
            Ocean.Wind wind = ocean.getWind();
            wind.setVelocity(wind.getVelocity()+0.1f);
            ocean.setWind(wind);
        }
        if (glfwGetKey(window.getWindow(), GLFW_KEY_J) == GLFW_PRESS) {
            Ocean.Wind wind = ocean.getWind();
            wind.getDirection().x+=1;
            wind.setDirection(wind.getDirection().normalize());
            ocean.setWind(wind);
        } else if (glfwGetKey(window.getWindow(), GLFW_KEY_K) == GLFW_PRESS) {
            Ocean.Wind wind = ocean.getWind();
            wind.getDirection().x-=1;
            wind.setDirection(wind.getDirection().normalize());
            ocean.setWind(wind);
        }
    }

    @Override
    protected void init() {
        super.init();
        ocean = new Ocean(256, 256, 128, 128, new Ocean.Wind(30, new Vector2f(1,0)), 0.000005f);
        camera.setPosition(0,100,0);

    }

    @Override
    protected void step() {
        double st = TimeUtil.currentTime();
//        ocean.evaluateWaves((float) timer.getTime());
        ocean.evaluateWavesFFT((float) timer.getTime());
        double et = TimeUtil.currentTime();
        logger.info("evaluate time: {}s", et - st);
    }

    @Override
    protected void render() {
        ocean.render(window, camera);
    }

    public static void main(String[] args) {
        new OceanDemo().run();
    }
}
