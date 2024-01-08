package engine.physics.objects.boundings;

import engine.maths.Vector3f;

import java.util.ArrayList;

public class Hitbox {

    private Vector3f position, rotation;
    private Vector3f size;

    private ArrayList<Bound> bounds = new ArrayList<>();

    public Hitbox(Vector3f position, Vector3f rotation, Vector3f size) {
        this.position = position;
        this.rotation = rotation;
        this.size = size;
    }

    public void move(Vector3f movementVector) {
        position = Vector3f.add(position, movementVector);
    }

    public void setPosition(Vector3f localOrigin) {
        this.position = localOrigin;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void rotate(Vector3f rotationVector) {
        rotation = Vector3f.add(rotation, rotationVector);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getSize() {
        return size;
    }

    public void setSize(Vector3f size) {
        this.size = size;
    }

    public boolean inBounds(Vector3f queryVector) {
        if ((position.getX() - (size.getX() / 2)) < queryVector.getX() && queryVector.getX() < (position.getX() + (size.getX() / 2))) { //within x bounds (width)
            if ((position.getY() - (size.getY() / 2)) < queryVector.getY() && queryVector.getY() < (position.getY() + (size.getY() / 2))) { // within y bounds (height)
                if ((position.getZ() - (size.getZ() / 2)) < queryVector.getZ() && queryVector.getZ() < (position.getZ() + (size.getZ() / 2))) { //within z bounds (depth)
                    return true;
                }
            }
        }

        return false;
    }

    public void render() {
        //TODO: make renderable for debugging
    }

    public void destroy() {

    }
}
