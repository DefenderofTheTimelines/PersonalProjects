import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class UniformInfo
{
    private final int location;
    private final String name;
    private final int params;

    public UniformInfo(Shader shader, String name, int params)
    {
        this.name = name;
        location = glGetUniformLocation(shader.getID(), name);
        this.params = params;
    }

    public int getLocation()
    {
        return location;
    }

    public String getName()
    {
        return name;
    }

    public int getParamCount()
    {
        return params;
    }

    public boolean equals(Object o)
    {
        if(!(o instanceof UniformInfo other)) return false;
        return this.location == other.location &&
                this.name.equals(other.name) &&
                this.params == other.params;
    }
}
