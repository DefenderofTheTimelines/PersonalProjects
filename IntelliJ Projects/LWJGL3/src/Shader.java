import org.joml.*;
import org.lwjgl.system.*;

import java.io.*;
import java.nio.*;
import java.util.*;

import static org.lwjgl.opengl.GL20.*;

public class Shader
{
    private final int shaderProgram;

    private static String dir = null;

    private ArrayList<UniformInfo> uniforms;

    ///Assumes the shader files are named shaderName.frag
    ///and shaderName.vert for the fragment and vertex
    ///shader sources.
    public Shader(String shaderName)
    {
        if(dir == null)
            throw new RuntimeException("Shader directory unspecified, please specify a directory");

        uniforms = new ArrayList<UniformInfo>();

        String vertPath = String.format("%s/%s.vert", dir, shaderName);
        String fragPath = String.format("%s/%s.frag", dir, shaderName);

        //Create and compile the vertex shader
        String vertSource = readFile(vertPath);
        if(vertSource == null) throw new RuntimeException("Unable to find/open the vertex shader source file");

        int vertShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertShader,  vertSource);
        glCompileShader(vertShader);

        //Check status of shader
        int status = glGetShaderi(vertShader, GL_COMPILE_STATUS);
        String log = glGetShaderInfoLog(vertShader);

        if(status == GL_FALSE)
            throw new RuntimeException("Failed to compile vertex shader:\n" + log);

        //Create and compile the fragment shader
        String fragSource = readFile(fragPath);
        if(fragSource == null) throw new RuntimeException("Unable to find/open the fragment shader source file");

        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader,  fragSource);
        glCompileShader(fragShader);

        //Check status of shader
        status = glGetShaderi(fragShader, GL_COMPILE_STATUS);
        log = glGetShaderInfoLog(fragShader);

        if(status == GL_FALSE)
            throw new RuntimeException("Failed to compile fragment shader:\n" + log);

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertShader);
        glAttachShader(shaderProgram, fragShader);
        glLinkProgram(shaderProgram);

        status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        log = glGetProgramInfoLog(shaderProgram);

        if(status == GL_FALSE)
            throw new RuntimeException("Failed to link the shader:\n" + log);

        glDeleteShader(vertShader);
        glDeleteShader(fragShader);
    }

    private String readFile(String filePath)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.lineSeparator();

            while((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            reader.close();
            return stringBuilder.toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void use()
    {
        glUseProgram(shaderProgram);
    }

    public int getID()
    {
        return  shaderProgram;
    }

    public void initUniform(String name, int params)
    {
        this.use();
        uniforms.add(new UniformInfo(this, name, params));
    }

    public void setUniformInt(String name, int... i)
    {
        this.use();
        Optional<UniformInfo> found = uniforms.stream().filter(c -> c.getName().equals(name)).findAny();
        if(found.isPresent() && found.get().getParamCount() == i.length)
            switch(i.length) {
                case 1 -> glUniform1i(found.get().getLocation(), i[0]);
                case 2 -> glUniform2i(found.get().getLocation(), i[0], i[1]);
                case 3 -> glUniform3i(found.get().getLocation(), i[0], i[1], i[2]);
                case 4 -> glUniform4i(found.get().getLocation(), i[0], i[1], i[2], i[3]);
                default -> throw new RuntimeException("Invalid number of arguments min: 1, max: 4, given: " + i.length);
            }
        else {
            if(found.isEmpty())
                throw new RuntimeException(String.format("Failed to set Uniform, no Uniform accepting %d int(s) named: %s", i.length, name));
            else if(found.get().getParamCount() != i.length)
                throw new RuntimeException(String.format("Failed to set Uniform, Uniform %s expects %d args, was given %d", name, found.get().getParamCount(), i.length));
        }
    }

    public void setUniformFloat(String name, float... f)
    {
        this.use();
        Optional<UniformInfo> found = uniforms.stream().filter(c -> c.getName().equals(name)).findAny();
        if(found.isPresent() && found.get().getParamCount() == f.length)
            switch(f.length) {
                case 1 -> glUniform1f(found.get().getLocation(), f[0]);
                case 2 -> glUniform2f(found.get().getLocation(), f[0], f[1]);
                case 3 -> glUniform3f(found.get().getLocation(), f[0], f[1], f[2]);
                case 4 -> glUniform4f(found.get().getLocation(), f[0], f[1], f[2], f[3]);
                default -> throw new RuntimeException("Invalid number of arguments min: 1, max: 4, given: " + f.length);
            }
        else {
            if(found.isEmpty())
                throw new RuntimeException(String.format("Failed to set Uniform, no Uniform accepting %d float(s) named: %s", f.length, name));
            else if(found.get().getParamCount() != f.length)
                throw new RuntimeException(String.format("Failed to set Uniform, Uniform %s expects %d args, was given %d", name, found.get().getParamCount(), f.length));
            throw new RuntimeException();
        }
    }

    public void setUniformMatrix2f(String name, Matrix2f m)
    {
        float[] array = new float[4];
        array = m.get(array);
        setUniformMatrix(name, array);
    }

    public void setUniformMatrix3f(String name, Matrix3f m)
    {
        float[] array = new float[9];
        array = m.get(array);
        setUniformMatrix(name, array);
    }

    public void setUniformMatrix4f(String name, Matrix4f m)
    {
        float[] array = new float[16];
        array = m.get(array);
        setUniformMatrix(name, array);
    }

    private void setUniformMatrix(String name, float... m)
    {
        this.use();
        Optional<UniformInfo> found = uniforms.stream().filter(c -> c.getName().equals(name)).findAny();
        if(found.isPresent() && found.get().getParamCount() == m.length)
            switch(m.length) {
                case 4  -> glUniformMatrix2fv(found.get().getLocation(), false, m);
                case 9  -> glUniformMatrix3fv(found.get().getLocation(), false, m);
                case 16 -> glUniformMatrix4fv(found.get().getLocation(), false, m);
                default -> throw new RuntimeException("Invalid number of arguments possible values: 4 9 16, given: " + m.length);
            }
        else {
            if(found.isEmpty())
                throw new RuntimeException(String.format("Failed to set Uniform, no Uniform accepting %d float(s) named: %s", m.length, name));
            else if(found.get().getParamCount() != m.length)
                throw new RuntimeException(String.format("Failed to set Uniform, Uniform %s expects %d args, was given %d", name, found.get().getParamCount(), m.length));
            throw new RuntimeException();
        }
    }

    public static void setShaderSourceDirectory(String directory)
    {
        dir = directory;
    }
}
