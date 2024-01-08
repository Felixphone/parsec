package engine.physics.objects.attributes;

import engine.maths.Vector3f;

public class Force {

    private float x;
    private float y;
    private float z;

    public Force(float x, float y, float z) {
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

    public void add(Force force) {
        this.x += force.getX();
        this.y += force.getY();
        this.z += force.getZ();
    }

    public void subtract(Force force) {
        this.x -= force.getX();
        this.y -= force.getY();
        this.z -= force.getZ();
    }

    public void multiply(Force force) {
        this.x *= force.getX();
        this.y *= force.getY();
        this.z *= force.getZ();
    }

    public void divide(Force force) {
        this.x /= force.getX();
        this.y /= force.getY();
        this.z /= force.getZ();
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
        return "Force{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
