package demo;

import game.GameEngine;
import graphic.light.DirectionalLight;
import graphic.light.PointLight;
import graphic.light.SpotLight;
import obj.GameObj;
import obj.LightBunny;
import obj.LightCube;
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
 * @Date 2020/12/28 21:36
 * @Version 1.0
 **/
public class MoreLightDemo extends GameEngine {


    private ShaderProgram program;
    private PointLight pointLight;
    private DirectionalLight directionalLight;
    private Vector3f ambientLight;
    private float specularPower;
    private float lightAngle;

    @Override
    protected void init() {
        super.init();
        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        specularPower = 1;
        Vector3f lightColour = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, 3);
        float lightIntensity = 1.0f;
        PointLight.Attenuation att = new PointLight.Attenuation(0.6f, 0.2f, 0.2f);
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity, att);

        lightPosition = new Vector3f(-1, 0, 0);
        lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);

        program = new ShaderProgram();
        program.init("more_light.vert", "more_light.frag");
        program.createUniform("world");
        program.createUniform("projection");
        program.createUniform("texture_sampler");

        program.createUniform("ambientLight");
        program.createUniform("specularPower");
        program.createMaterialUniform("material");
        program.createDirectionalLightUniform("directionalLight");
        program.createPointLightsUniform("pointLights",5);
        program.createSpotLightsUniform("spotLights",5);
        gameObjList.add(new LightCube(new Vector3f(-1f,0,-1.5f), new Vector3f(0,0,0),0.3f, program));
        gameObjList.add(new LightBunny(new Vector3f(1f,0,-1.5f), new Vector3f(0,0,0),0.4f, program));
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
// Update directional light direction, intensity and colour
        lightAngle += 1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColour().y = Math.max(factor, 0.9f);
            directionalLight.getColour().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColour().x = 1;
            directionalLight.getColour().y = 1;
            directionalLight.getColour().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    protected void render() {
        //移动相机 灯光不动
        Vector3f cameraPos = camera.getPosition();
        Vector3f cameraRot = camera.getRotation();
        Matrix4f viewMatrix = new Matrix4f()
                .rotateX((float) Math.toRadians(cameraRot.x))
                .rotateY((float) Math.toRadians(cameraRot.y))
                .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        program.bind();
        program.setUniform("ambientLight", ambientLight);
        program.setUniform("specularPower", specularPower);
        PointLight currentPointLight = new PointLight(pointLight);
        Vector3f lightPos = currentPointLight.getPosition();
        Vector4f lightPosInWord = new Vector4f(lightPos, 1);
        lightPosInWord.mul(viewMatrix);
        lightPos.x = lightPosInWord.x;
        lightPos.y = lightPosInWord.y;
        lightPos.z = lightPosInWord.z;
//        program.setUniform("pointLights", new PointLight[]{currentPointLight});

        SpotLight spotLight = new SpotLight(currentPointLight, new Vector3f(1,0,0), 30);

        program.setUniform("spotLights", new SpotLight[]{spotLight});

        DirectionalLight currentDirectionalLight = new DirectionalLight(directionalLight);
        Vector3f lightDirection = currentDirectionalLight.getDirection();
        Vector4f lightDirectionInWord = new Vector4f(lightDirection, 0);
        lightDirectionInWord.mul(viewMatrix);
        lightDirection.x = lightDirectionInWord.x;
        lightDirection.y = lightDirectionInWord.y;
        lightDirection.z = lightDirectionInWord.z;
        program.setUniform("directionalLight", currentDirectionalLight);

        for (GameObj o : gameObjList) {
            o.render(window, camera);
        }
        program.unbind();
    }

    public static void main(String[] args) {
        new MoreLightDemo().run();
    }
}
