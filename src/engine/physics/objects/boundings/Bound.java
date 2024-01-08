package engine.physics.objects.boundings;

import engine.maths.Vector3f;

public abstract class Bound {

    private Vector3f position;
    private Vector3f rotation;
    private Vector3f size;

    public Bound(Vector3f position, Vector3f rotation, Vector3f size) {
        this.position = position;
        this.rotation = rotation;
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

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getSize() {
        return size;
    }

    public void setSize(Vector3f size) {
        this.size = size;
    }
}
