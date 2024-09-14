import org.joml.*;

import java.lang.Math;

public class Camera
{
    private Vector3f pos;   //Camera position
    private Vector3f tgt;
    private Vector3f wup;   //Up in world coordinates
    private Vector3f cup;   //Up in camera coordinates
    private Vector3f rht;   //Camera right
    private Vector3f dir;   //What way camera is pointing

    private Matrix4f rot;

    public Camera()
    {
        this(new Vector3f(0.0f, 0.0f, 6.0f),
             new Vector3f(0.0f, 0.0f, 0.0f),
             new Vector3f(0.0f, 1.0f, 0.0f)
        );
    }

    public Camera(Vector3f pos)
    {
        this(pos,
             new Vector3f(0.0f, 0.0f, 0.0f),
             new Vector3f(0.0f, 1.0f, 0.0f)
        );
    }

    public Camera(Vector3f pos, Vector3f tgt)
    {
        this(pos,
             tgt,
             new Vector3f(0.0f, 1.0f, 0.0f)
        );
    }

    public Camera(Vector3f pos, Vector3f tgt, Vector3f up)
    {
        this.pos = pos;
        this.wup = up;
        this.tgt = tgt;

        update();
    }

    private void update()
    {
        this.dir = new Vector3f(pos).sub(tgt).normalize();
        this.rht = new Vector3f(wup).cross(dir).normalize();
        this.cup = new Vector3f(dir).cross(rht);

        System.out.println(
                "\nPos: " + pos +
                "\nTgt: " + tgt +
                "\nDir: " + dir +
                "\nRht: " + rht +
                "\n Up: " + cup
        );
    }

    public void move(Vector3f dist)
    {
        pos.add(dist);
        tgt.add(dist);

        update();
    }

    public Vector3f getPos()
    {
        return new Vector3f(pos);
    }

    public void setPos(Vector3f pos)
    {
        this.pos = pos;

        update();
    }

    public void setTgt(Vector3f tgt)
    {
        this.tgt = tgt;

        update();
    }

    public void setRotation(float pitch, float yaw)
    {
        Vector3f tmp = new Vector3f(
                 (float)(Math.sin(Math.toRadians(yaw))),
                -(float)(Math.sin(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw))),
                -(float)(Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)))
        );
        
        tgt = new Vector3f(pos).add(tmp);
        update();
    }

    public Vector3f getFront()
    {
        return new Vector3f(dir).mul(-1);
    }

    public Vector3f getRight()
    {
        return new Vector3f(rht);
    }

    public Vector3f getUp()
    {
        return new Vector3f(cup);
    }

    public Matrix4f getView()
    {
        return new Matrix4f().lookAt(pos, tgt, cup);
    }

    public Vector3f getTarget()
    {
        return tgt;
    }
}
