package engine.graphics.objects;

import engine.dataItems.exceptions.RequiredFileNotFoundException;
import engine.utilities.resources.ResourceLoader;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.opengl.Texture;

public class Material {

    private Texture texture;
    private String path;
    private float width, height;
    private int textureID;

    public Material(String path) {
        this.path = path;
    }

    public void createMaterial() throws RequiredFileNotFoundException {
        texture = ResourceLoader.getTexture(path);
        width = texture.getWidth();
        height = texture.getHeight();
        textureID = texture.getTextureID();
    }

    public Texture getTexture() {
        return texture;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getTextureID() {
        return textureID;
    }

    public void destroy() {
        GL13.glDeleteTextures(textureID);
    }
}
