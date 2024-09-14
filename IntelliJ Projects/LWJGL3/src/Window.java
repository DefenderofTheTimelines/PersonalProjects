import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window
{
    private final long handle;
    private int width, height;

    public Window() { this(800, 600, ""); }

    public Window(int width, int height, String title)
    {
        this.width = width;
        this.height = height;

        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if(handle == NULL)
            throw new RuntimeException("Failed to create the window!");

        //Set the keyCallback
        glfwSetKeyCallback(handle, Engine::keyCallback);

        glfwMakeContextCurrent(handle);

        //0 is uncapped, 1 is vsync
        glfwSwapInterval(1);
        glfwShowWindow(handle);

        //Set the framebuffer callback
        glfwSetFramebufferSizeCallback(handle, this::framebufferCallback);
    }

    public boolean shouldClose()
    {
        return glfwWindowShouldClose(handle);
    }

    public void swapBuffers()
    {
        glfwSwapBuffers(handle);
    }

    public void cleanup()
    {
        //Free and destroy window and its callbacks
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public float getAspectRatio()
    {
        return (float)width/(float)height;
    }

    public long getHandle() {
        return handle;
    }

    private void framebufferCallback(long window, int width, int height)
    {
        this.width = width;
        this.height = height;
        glViewport(0, 0, width, height);
    }
}
