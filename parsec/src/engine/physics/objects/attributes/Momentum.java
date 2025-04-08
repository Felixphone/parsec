package engine.physics.objects.attributes;

import engine.maths.Vector3f;

public class Momentum {

    private float x;
    private float y;
    private float z;

    public Momentum(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getMagnitude() {
        float magnitude = (float) Math.sqrt((x*x) + (y*y) + (z*z));
        return magnitude;
    }

    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
