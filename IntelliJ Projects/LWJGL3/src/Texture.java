import org.lwjgl.stb.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;


public class Texture
{
    private static String dir = null;

    private final int id;

    public Texture(String file)
    {
        if(dir == null)
            throw new RuntimeException("Texture base directory unspecified, please specify what folder holds all textures");

        String texPath = String.format("%s/%s", dir, file);

        try(MemoryStack stack = stackPush())
        {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer n = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer data = STBImage.stbi_load(texPath, w, h, n, 0);

            if(data == null)
                throw new RuntimeException("Unable to find/open: " + texPath);

            id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            if(file.toUpperCase().contains(".png".toUpperCase()))
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w.get(0), h.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
            else
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w.get(0), h.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, data);

            glGenerateMipmap(GL_TEXTURE_2D);

            STBImage.stbi_image_free(data);
        }
    }

    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public static void setActive(int i)
    {
        glActiveTexture(GL_TEXTURE0 + i);
    }

    public static void setTextureDirectory(String directory)
    {
        dir = directory;
    }
}
