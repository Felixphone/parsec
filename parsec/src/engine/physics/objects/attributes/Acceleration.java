package engine.physics.objects.attributes;

import engine.maths.Vector3f;

public class Acceleration {

    private float x;
    private float y;
    private float z;

    public Acceleration(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void calculate(Force force, Mass mass) {
        this.x = force.getX() / mass.getMass();
        this.y = force.getY() / mass.getMass();
        this.z = force.getZ() / mass.getMass();
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

    @Override
    public String toString() {
        return "Acceleration{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
