package engine.graphics.objects;

import engine.maths.Vector2f;
import engine.maths.Vector3f;

public class Vertex {

    private Vector3f position;
    private ColourVector colour;
    private Vector2f textureCoord;
    private Material material;

    public Vertex(Vector3f position, ColourVector colour, Vector2f textureCoord) {
        this.position = position;
        this.colour = colour;
        this.textureCoord = textureCoord;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public ColourVector getColour() {
        return colour;
    }

    public void setColour(ColourVector colour) {
        this.colour = colour;
    }

    public Vector2f getTextureCoord() {
        return textureCoord;
    }

    public void setTextureCoord(Vector2f textureCoord) {
        this.textureCoord = textureCoord;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "position=" + position +
                ", colour=" + colour +
                ", textureCoord=" + textureCoord +
                ", material=" + material +
                '}';
    }
}
