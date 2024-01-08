package game.world.scenes.scenes.planet.objects;

import engine.graphics.objects.*;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import game.world.objects.GameObject;

public class Sun extends GameObject {

    private static Mesh mesh = new Mesh(new Vertex[] {
            new Vertex(new Vector3f(-1.5f, 1.5f, 0.0f), new ColourVector(1.0f, 0.0f, 0.0f), new Vector2f(0.0f,0.0f)),
            new Vertex(new Vector3f(1.5f, 1.5f, 0.0f), new ColourVector(0.0f, 1.0f, 0.0f), new Vector2f(1.0f,0.0f)),
            new Vertex(new Vector3f(1.5f, -1.5f, 0.0f), new ColourVector(0.0f, 0.0f, 1.0f), new Vector2f(1.0f,1.0f)),
            new Vertex(new Vector3f(-1.5f, -1.5f, 0.0f), new ColourVector(1.0f, 1.0f, 0.0f), new Vector2f(0.0f,1.0f)),

    }, new int[] {

            0, 1, 2,
            0, 3, 2
    }, new Material("textures/texture.png"));

    public Sun(Vector3f position, Vector3f rotation, Vector3f scale) {
        super(position, rotation, scale, mesh);
    }

    @Override
    public void update() {

    }
}
