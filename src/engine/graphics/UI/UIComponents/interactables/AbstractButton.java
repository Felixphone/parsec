package engine.graphics.UI.UIComponents.interactables;

import engine.dataItems.exceptions.RequiredFileNotFoundException;
import engine.engine.EngineCore;
import engine.graphics.objects.ColourVector;
import engine.graphics.objects.Material;
import engine.graphics.objects.Mesh;
import engine.graphics.objects.Vertex;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import tracerUtils.data.ThreadState;

public abstract class AbstractButton extends InteractiveUIComponent {

    private static Mesh mesh = new Mesh(new Vertex[] {
            new Vertex(new Vector3f(-1.5f, 1.5f, 0.0f), new ColourVector(1.0f, 0.0f, 0.0f), new Vector2f(0.0f,0.0f)),
            new Vertex(new Vector3f(1.5f, 1.5f, 0.0f), new ColourVector(0.0f, 1.0f, 0.0f), new Vector2f(1.0f,0.0f)),
            new Vertex(new Vector3f(1.5f, -1.5f, 0.0f), new ColourVector(0.0f, 0.0f, 1.0f), new Vector2f(1.0f,1.0f)),
            new Vertex(new Vector3f(-1.5f, -1.5f, 0.0f), new ColourVector(1.0f, 1.0f, 0.0f), new Vector2f(0.0f,1.0f)),

    }, new int[] {

            0, 1, 2,
            0, 3, 2
    }, new Material("textures/terrain.png"));

    static {
        try {
            mesh.generateMesh();
            EngineCore.getLogger().warn("Generated mesh", "", new ThreadState(Thread.currentThread()));
        } catch (RequiredFileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public AbstractButton(Vector3f position, Vector3f rotation, Vector3f scale) throws RequiredFileNotFoundException {
        super(position, rotation, scale, mesh);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void whilstBeingClicked() {

    }

    @Override
    public void onClick() {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void whilstBeingHovered() {

    }

    @Override
    public void onMouseEnter() {

    }

    @Override
    public void onMouseExit() {

    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
