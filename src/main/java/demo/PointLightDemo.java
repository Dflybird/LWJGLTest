package demo;

import game.GameEngine;
import graphic.PointLight;
import obj.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import shader.ShaderProgram;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;

/**
 * @Author Gq
 * @Date 2020/12/24 21:40
 * @Version 1.0
 **/
public class PointLightDemo extends GameEngine {

    private ShaderProgram program;
    private PointLight pointLight;
    private Vector3f ambientLight;
    private float specularPower;

    @Override
    protected void init() {
        super.init();
        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        specularPower = 1;
        Vector3f lightColour = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(10, 0, 1);
        float lightIntensity = 1.0f;
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity, att);
        program = new ShaderProgram();
        program.init("point_light.vert", "point_light.frag");
        program.createUniform("world");
        program.createUniform("projection");
        program.createUniform("texture_sampler");

        program.createUniform("ambientLight");
        program.createUniform("specularPower");
        program.createMaterialUniforms("material");
        program.createPointLightUniforms("pointLight");
//        gameObjList.add(new LightCube(new Vector3f(-0.5f,0,-1.5f), new Vector3f(0,0,0),0.2f, program));
        gameObjList.add(new LightBunny(new Vector3f(0.5f,0,-1.5f), new Vector3f(0,0,0),0.3f, program));
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        program.cleanup();
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

        //修改光源位置
        float lightPos = pointLight.getPosition().z;
        if (glfwGetKey(window.getWindow(), GLFW_KEY_N) == GLFW_PRESS) {
            this.pointLight.getPosition().z = lightPos + 0.1f;
        } else if (glfwGetKey(window.getWindow(), GLFW_KEY_M) == GLFW_PRESS) {
            this.pointLight.getPosition().z = lightPos - 0.1f;
        }
    }

    @Override
    protected void step() {

    }

    @Override
    protected void render() {
        program.bind();
        program.setUniform("ambientLight", ambientLight);
        program.setUniform("specularPower", specularPower);
        PointLight currentPointLight = new PointLight(pointLight);
        Vector3f lightPos = currentPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        //移动相机 灯光不动
        Vector3f cameraPos = camera.getPosition();
        Vector3f cameraRot = camera.getRotation();
        Matrix4f viewMatrix = new Matrix4f()
                .rotateX((float) Math.toRadians(cameraRot.x))
                .rotateY((float) Math.toRadians(cameraRot.y))
                .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        program.setUniform("pointLight", currentPointLight);

        for (GameObj o : gameObjList) {
            o.render(window, camera);
        }
        program.unbind();
    }

    public static void main(String[] args) {
        new PointLightDemo().run();
    }
}