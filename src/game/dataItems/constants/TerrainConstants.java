package game.dataItems.constants;

import engine.maths.Vector3f;

public class TerrainConstants {

    public static final int CHUNK_SIZE = 20;
    public static final int CHUNK_VERTEX_COUNT = CHUNK_SIZE + 1;

    public static final int MAX_GENERATION_DISTANCE = 100010;
    public static final int MIN_GENERATION_DISTANCE = 0;

    public static final float NOISE_PERSISTENCE = 0.4f;
    public static final float NOISE_FREQUENCY = 3.25f;
    public static final int NOISE_AMPLITUDE = 42;
    public static final int NOISE_OCTAVES = 12;

    public static final Vector3f CHUNK_ROTATION = new Vector3f(0.0f, 0.0f, 0.0f);
    public static final Vector3f CHUNK_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);
    public static final boolean REGENERATE_CORRUPT_CHUNKS = true;

    public static final Vector3f WATER_ROTATION = new Vector3f(0.0f, 0.0f, 0.0f);
    public static final Vector3f WATER_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);
    public static final float WATER_HEIGHT = -10.0f;

    public static final float GRAVITY = 0.2f;

}
