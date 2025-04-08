package engine.graphics.UI.UIComponents;

import engine.dataItems.exceptions.RequiredFileNotFoundException;
import engine.engine.EngineCore;
import engine.graphics.GameWindow;
import engine.graphics.Renderer;
import engine.graphics.ShaderManager;
import engine.graphics.objects.ColourVector;
import engine.graphics.objects.Material;
import engine.graphics.objects.Mesh;
import engine.graphics.objects.Vertex;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import game.dataItems.constants.TestConstants;
import game.world.player.Camera;
import tracerUtils.data.ThreadState;

public abstract class UIComponent {

    protected Vector3f position;
    protected Vector3f rotation;
    protected Vector3f scale;

    protected Mesh mesh;
    protected boolean screenInteractionEnabled = true;

    public UIComponent(Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;

        this.mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f(-1.5f, 1.5f, 0.0f), new ColourVector(1.0f, 0.0f, 0.0f), new Vector2f(0.0f,0.0f)),
                new Vertex(new Vector3f(1.5f, 1.5f, 0.0f), new ColourVector(0.0f, 1.0f, 0.0f), new Vector2f(1.0f,0.0f)),
                new Vertex(new Vector3f(1.5f, -1.5f, 0.0f), new ColourVector(0.0f, 0.0f, 1.0f), new Vector2f(1.0f,1.0f)),
                new Vertex(new Vector3f(-1.5f, -1.5f, 0.0f), new ColourVector(1.0f, 1.0f, 0.0f), new Vector2f(0.0f,1.0f)),

        }, new int[] {

                0, 1, 2,
                0, 3, 2
        }, new Material("textures/terrain.png"));

        try {
            this.mesh.generateMesh();
            EngineCore.getLogger().warn("Generated mesh", "", new ThreadState(Thread.currentThread()));
        } catch (RequiredFileNotFoundException e) {
            e.printStackTrace();
        }
        //this.mesh = mesh;
    }

    public void render() {
        Renderer renderer = EngineCore.getGraphicsEngine().getRenderer();
        renderer.bindShader(ShaderManager.TEXTURE_SHADER());
        renderer.bindTexture(mesh.getMaterial().getTextureID());
        renderer.bindMesh(mesh);
        //System.out.println(" e: " + Vector3f.divide(new Vector3f(GameWindow.getWidth(), GameWindow.getHeight(), 100),position));
        try {
            position = new Vector3f(0.0f, 0.0f, 0.01f);
            rotation = new Vector3f(0, 0, 0);
            scale = new Vector3f(10, 10, 10);
            float width = EngineCore.getGraphicsEngine().getGameWindow().getWidth();
            float height = EngineCore.getGraphicsEngine().getGameWindow().getHeight();
            //renderer.draw(new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), new Vector3f(10, 10, 10), new Camera(TestConstants.ONE_VEC3F, TestConstants.ZERO_VEC3F));
            //renderer.draw(new Vector3f((width/2)*position.getX(), (height/2)*position.getY(), -5), rotation, scale, new Camera(TestConstants.ONE_VEC3F, TestConstants.ZERO_VEC3F));
        } catch (Exception e) {
            e.printStackTrace();
        }
        renderer.cleanUp();
    }

    public void update() {

    }

    public boolean isScreenInteractionEnabled() {
        return screenInteractionEnabled;
    }

    public void setScreenInteractionEnabled(boolean screenInteractionEnabled) {
        this.screenInteractionEnabled = screenInteractionEnabled;
    }
}
