import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import org.joml.*;

import java.lang.Math;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Engine
{
    //Cube Unfold
    //            7---------4
    //            |         |
    //            |   Top   |
    //            |         |
    //  7---------3---------0---------4---------7
    //  |         |         |         |         |
    //  |  Left   |  Front  |  Right  |  Back   |
    //  |         |         |         |         |
    //  6---------2---------1---------5---------6
    //            |         |
    //            | Bottom  |
    //            |         |
    //            6---------5
    //
    //Coordinates
    // 0:  1.0f,  1.0f,  1.0f
    // 1:  1.0f, -1.0f,  1.0f
    // 2: -1.0f, -1.0f,  1.0f
    // 3: -1.0f,  1.0f,  1.0f
    //
    // 4:  1.0f,  1.0f, -1.0f
    // 5:  1.0f, -1.0f, -1.0f
    // 6: -1.0f, -1.0f, -1.0f
    // 7: -1.0f,  1.0f, -1.0f

    private final float[] vertices = {
            //Positions            //Tex Coords
            //Front
             1.0f,  1.0f,  1.0f,   1.0f, 1.0f, //00 top right
             1.0f, -1.0f,  1.0f,   1.0f, 0.0f, //01 bottom right
            -1.0f, -1.0f,  1.0f,   0.0f, 0.0f, //02 bottom left
            -1.0f,  1.0f,  1.0f,   0.0f, 1.0f, //03 top left

            //Back
            -1.0f,  1.0f, -1.0f,   1.0f, 1.0f, //04 top right
            -1.0f, -1.0f, -1.0f,   1.0f, 0.0f, //05 bottom right
             1.0f, -1.0f, -1.0f,   0.0f, 0.0f, //06 bottom left
             1.0f,  1.0f, -1.0f,   0.0f, 1.0f, //07 top left

            //Bottom
             1.0f, -1.0f,  1.0f,   1.0f, 1.0f, //08 top right
             1.0f, -1.0f, -1.0f,   1.0f, 0.0f, //09 bottom right
            -1.0f, -1.0f, -1.0f,   0.0f, 0.0f, //10 bottom left
            -1.0f, -1.0f,  1.0f,   0.0f, 1.0f, //11 top left

            //Top
             1.0f,  1.0f, -1.0f,   1.0f, 1.0f, //12 top right
             1.0f,  1.0f,  1.0f,   1.0f, 0.0f, //13 bottom right
            -1.0f,  1.0f,  1.0f,   0.0f, 0.0f, //14 bottom left
            -1.0f,  1.0f, -1.0f,   0.0f, 1.0f, //15 top left

            //Left
            -1.0f,  1.0f,  1.0f,   1.0f, 1.0f, //16 top right
            -1.0f, -1.0f,  1.0f,   1.0f, 0.0f, //17 bottom right
            -1.0f, -1.0f, -1.0f,   0.0f, 0.0f, //18 bottom left
            -1.0f,  1.0f, -1.0f,   0.0f, 1.0f, //19 top left

            //Right
            1.0f,  1.0f, -1.0f,   1.0f, 1.0f, //20 top right
            1.0f, -1.0f, -1.0f,   1.0f, 0.0f, //21 bottom right
            1.0f, -1.0f,  1.0f,   0.0f, 0.0f, //22 bottom left
            1.0f,  1.0f,  1.0f,   0.0f, 1.0f, //23 top left
    };

    private final int[] indices = {
            //Front
            0, 1, 3,
            1, 2, 3,

            //Back
            4, 5, 7,
            5, 6, 7,

            //Bottom
            8,  9, 11,
            9, 10, 11,

            //Front
            12, 13, 15,
            13, 14, 15,

            //Left
            16, 17, 19,
            17, 18, 19,

            //Right
            20, 21, 23,
            21, 22, 23,
    };

    private final Vector3f[] cubePositions = {
            new Vector3f( 0.0f,  0.0f,  0.0f),
            new Vector3f( 2.0f,  5.0f, -15.0f),
            new Vector3f(-1.5f, -2.2f, -2.5f),
            new Vector3f(-3.8f, -2.0f, -12.3f),
            new Vector3f( 2.4f, -0.4f, -3.5f),
            new Vector3f(-1.7f,  3.0f, -7.5f),
            new Vector3f( 1.3f, -2.0f, -2.5f),
            new Vector3f( 1.5f,  2.0f, -2.5f),
            new Vector3f( 1.5f,  0.2f, -1.5f),
            new Vector3f(-1.3f,  1.0f, -1.5f)
    };

    private final Window window;
    private final Camera camera;
    private final Timer timer;

    private double deltaTime;

    public Engine()
    {
        Path path = Paths.get("");
        Shader.setShaderSourceDirectory(String.format("%s/%s", path.toAbsolutePath().toString(), "/resources/shaders"));
        Texture.setTextureDirectory(String.format("%s/%s", path.toAbsolutePath().toString(), "/resources/textures"));

        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit())
            throw new IllegalStateException("Unable to init GLFW");

        window = new Window(800, 800, "Learn OpenGL");
        camera = new Camera();
        timer = new Timer();
    }

    public void run()
    {
        System.out.println("LWJGL " + Version.getVersion());
        GL.createCapabilities();
        loop();
    }

    private void loop()
    {
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_CULL_FACE);
        //Accidentally input the vertices CW instead of CCW, fix later
        glFrontFace(GL_CW);

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //Position
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5*Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        //Texture
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5*Float.BYTES, 3*Float.BYTES);
        glEnableVertexAttribArray(1);

        Shader shader = new Shader("simple");

        //Uniforms
        shader.initUniform("model", 16);
        shader.initUniform("view", 16);
        shader.initUniform("projection", 16);


        //Textures
        Texture tex0 = new Texture("container.jpg");
        Texture tex1 = new Texture("awesomeface.png");

        shader.initUniform("tex0", 1);
        shader.initUniform("tex1", 1);

        shader.setUniformInt("tex0", 0);
        shader.setUniformInt("tex1", 1);

        //Scale the positions since my cubes are larger
        for(Vector3f pos : cubePositions)
            pos.mul(2.5f);

        String title = glfwGetWindowTitle(window.getHandle());

        ArrayList<Double> avgFPSBuffer = new ArrayList<Double>();
        while(!window.shouldClose())
        {
            deltaTime = timer.getElapsedTime();
            avgFPSBuffer.add(1.0/deltaTime);

            if(glfwGetTime() >= 1)
            {
                double avgFps = 0;
                for(int i = 0; i < avgFPSBuffer.size(); i++)
                    avgFps += avgFPSBuffer.get(i);
                avgFps /= avgFPSBuffer.size();

                avgFPSBuffer.clear();
                glfwSetTime(0.0);

                glfwSetWindowTitle(window.getHandle(), String.format("%s | FPS: %.2f", title, avgFps));
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            shader.use();

            //Matricies
            Matrix4f proj = new Matrix4f().perspective((float)Math.toRadians(45.0), window.getAspectRatio(), 0.1f, 100.0f);

            Matrix4f view = new Matrix4f().identity();
            view.translate(new Vector3f(0.0f, 0.0f, -6.0f));

            //System.out.println(view.toString());
            //System.out.println(camera.getView().toString());

            //shader.setUniformMatrix4f("view", view);
            shader.setUniformMatrix4f("view", camera.getView());
            shader.setUniformMatrix4f("projection", proj);

            Texture.setActive(0);
            tex0.bind();
            Texture.setActive(1);
            tex1.bind();

            //Draw multiple cubes
            glBindVertexArray(vao);

            //Render a small cube at the position the camera is looking at
            Matrix4f camTgt = new Matrix4f().identity();
            camTgt.translate(camera.getTarget());
            camTgt.scale(0.01f);
            shader.setUniformMatrix4f("model", camTgt);
            glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

            for(int i = 0; i < cubePositions.length; i++)
            {
                //Uncomment to render in wireframe
                //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

                //Build the Model Matrix for each cube
                Matrix4f model = new Matrix4f().identity();
                model.translate(cubePositions[i]);

                //Uncomment to have all cubes rotate about the y axis
                //model.rotate((float)Math.toRadians(rot), new Vector3f(0.0f, 1.0f, 0.0f));

                float angle = 20.0f * i;
                model.rotate((float)Math.toRadians(angle), new Vector3f(1.0f, 0.3f, 0.5f));

                //And send it to the shader
                shader.setUniformMatrix4f("model", model);

                glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
            }

            glfwPollEvents();
            processInputs();
            window.swapBuffers();
        }
    }

    public void cleanup()
    {
        window.cleanup();

        //Terminate GLFW and free its callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    float yaw = 0.0f;
    float pitch = 0.0f;

    private void processInputs()
    {

        float camMoveSpeed = 2.5f * (float)deltaTime;
        float camRotSpeed = 100.0f * (float)deltaTime;

        if(glfwGetKey(window.getHandle(), GLFW_KEY_W) == GLFW_PRESS)
            camera.move(camera.getFront().mul(camMoveSpeed));
        if(glfwGetKey(window.getHandle(), GLFW_KEY_S) == GLFW_PRESS)
            camera.move(camera.getFront().mul(-camMoveSpeed));
        if(glfwGetKey(window.getHandle(), GLFW_KEY_A) == GLFW_PRESS)
            camera.move(camera.getRight().mul(-camMoveSpeed));
        if(glfwGetKey(window.getHandle(), GLFW_KEY_D) == GLFW_PRESS)
            camera.move(camera.getRight().mul(camMoveSpeed));

        if(glfwGetKey(window.getHandle(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
            camera.move(camera.getUp().mul(-camMoveSpeed));
        if(glfwGetKey(window.getHandle(), GLFW_KEY_SPACE) == GLFW_PRESS)
            camera.move(camera.getUp().mul(camMoveSpeed));

        if(glfwGetKey(window.getHandle(), GLFW_KEY_I) == GLFW_PRESS)
            pitch -= camRotSpeed;
        if(glfwGetKey(window.getHandle(), GLFW_KEY_K) == GLFW_PRESS)
            pitch += camRotSpeed;
        if(glfwGetKey(window.getHandle(), GLFW_KEY_J) == GLFW_PRESS)
            yaw -= camRotSpeed;
        if(glfwGetKey(window.getHandle(), GLFW_KEY_L) == GLFW_PRESS)
            yaw += camRotSpeed;

        if(pitch >= 89.0f) pitch = 89.0f;
        else if(pitch <= -89.0f) pitch = -89.0f;

        camera.setRotation(pitch, yaw);
    }

    //This handles inputs that control the window itself, use processInputs to handle
    //Inputs to the engine
    public static void keyCallback(long window, int key, int scancode, int action, int mods)
    {
        if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }
}
