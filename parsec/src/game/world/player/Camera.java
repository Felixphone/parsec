package game.world.player;

import engine.maths.Vector3f;

public class Camera {

    private Vector3f position, rotation;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;

    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void move(Vector3f movementVector) {
        this.position = Vector3f.add(position, movementVector);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void rotate(Vector3f rotationVector) {
        this.rotation = Vector3f.add(rotation, rotationVector);
    }

    public void destroy() {

    }
}
