package game.world.objects;

import engine.dataItems.exceptions.RequiredFileNotFoundException;
import engine.engine.EngineCore;
import engine.graphics.ShaderManager;
import engine.graphics.objects.*;
import engine.graphics.Renderer;
import engine.maths.Vector3f;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import game.world.player.Camera;

public class GameObject {

    private final Logger logger;

    private Vector3f position, rotation, scale;
    private Mesh mesh;
    private Shader shader;

    public GameObject(Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.mesh = mesh;
        logger = EngineCore.getLogger();

        //generate mesh
        try {
            mesh.generateMesh();
        } catch (RequiredFileNotFoundException e) {
            logger.warn("File not found:", e.getMessage(), new ThreadState(Thread.currentThread()));
        }
    }

    public void update() {

    }

    public void render(Camera camera) {
        Renderer renderer = EngineCore.getGraphicsEngine().getRenderer();
        renderer.bindShader(ShaderManager.TEXTURE_SHADER());
        renderer.bindTexture(mesh.getMaterial().getTextureID());
        renderer.bindMesh(mesh);
        renderer.draw(position, rotation, scale, EngineCore.getGameCore().getWorld().getPlayer().getCamera());
        renderer.cleanUp();
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

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public void destroy() {
        mesh.destroy();
        shader.destroy();

    }
}
