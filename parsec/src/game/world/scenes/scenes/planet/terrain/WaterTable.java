package game.world.scenes.scenes.planet.terrain;

import engine.dataItems.exceptions.RequiredFileNotFoundException;
import engine.engine.EngineCore;
import engine.graphics.Renderer;
import engine.graphics.ShaderManager;
import engine.graphics.objects.*;
import engine.maths.Vector3f;
import engine.maths.Vector2f;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import game.dataItems.constants.TerrainConstants;
import game.world.scenes.scenes.planet.Planet;
import game.world.player.Camera;

public class WaterTable {

    private final Logger logger;

    public final Planet planet;
    private final ChunkPos chunkPos;
    private Mesh mesh = new Mesh(new Vertex[] {
            new Vertex(new Vector3f(-1.5f, 1.5f, 0.0f), new ColourVector(1.0f, 0.0f, 0.0f), new Vector2f(0.0f,0.0f)),
            new Vertex(new Vector3f(1.5f, 1.5f, 0.0f), new ColourVector(0.0f, 1.0f, 0.0f), new Vector2f(1.0f,0.0f)),
            new Vertex(new Vector3f(1.5f, -1.5f, 0.0f), new ColourVector(0.0f, 0.0f, 1.0f), new Vector2f(1.0f,1.0f)),
            new Vertex(new Vector3f(-1.5f, -1.5f, 0.0f), new ColourVector(1.0f, 1.0f, 0.0f), new Vector2f(0.0f,1.0f)),

    }, new int[] {

            0, 1, 2,
            0, 3, 2
    }, new Material("textures/texture.png"));

    public WaterTable(ChunkPos chunkPos, Planet planet) {
        this.chunkPos = chunkPos;
        this.planet = planet;
        logger = EngineCore.getLogger();

        generate();

    }

    public void generate() {

        Vertex[] vertices = new Vertex[TerrainConstants.CHUNK_VERTEX_COUNT * TerrainConstants.CHUNK_VERTEX_COUNT];
        int[] indices = new int[TerrainConstants.CHUNK_VERTEX_COUNT * TerrainConstants.CHUNK_VERTEX_COUNT * 6];

        float worldX = (chunkPos.getX() * TerrainConstants.CHUNK_VERTEX_COUNT);
        float worldZ = (chunkPos.getZ() * TerrainConstants.CHUNK_VERTEX_COUNT);
        logger.fine("Loading water chunk:", chunkPos.getX() + " , " + chunkPos.getZ() + " at: " + worldX + "," + worldZ, new ThreadState(Thread.currentThread()));

        for (int x = 0; x < TerrainConstants.CHUNK_VERTEX_COUNT; x++){
            for (int z = 0; z < TerrainConstants.CHUNK_VERTEX_COUNT; z++) {
                vertices[(x * TerrainConstants.CHUNK_VERTEX_COUNT) + z] = new Vertex(new Vector3f(x, TerrainConstants.WATER_HEIGHT, z), new ColourVector(0.0f, 0.0f, 0.0f, 0.0f), new Vector2f(x, z));
                logger.ultraFine("Loading water vertex:", x + ":" + z + " / " + TerrainConstants.CHUNK_VERTEX_COUNT + ":" + TerrainConstants.CHUNK_VERTEX_COUNT + " = " +  vertices[(x * TerrainConstants.CHUNK_VERTEX_COUNT) + z].toString(), new ThreadState(Thread.currentThread()));

            }
        }

        //load heights into mesh
        int i = 0;
        for (int x = 0; x < TerrainConstants.CHUNK_VERTEX_COUNT - 1; x++){
            for (int z = 0; z < TerrainConstants.CHUNK_VERTEX_COUNT - 1; z++) {

                indices[i] = (x * TerrainConstants.CHUNK_VERTEX_COUNT) + z; //triangle a
                indices[i + 1] = (x * TerrainConstants.CHUNK_VERTEX_COUNT) + z + 1;
                indices[i + 2] = ((x + 1) * TerrainConstants.CHUNK_VERTEX_COUNT) + z;

                indices[i + 3] = ((x + 1) * TerrainConstants.CHUNK_VERTEX_COUNT) + z + 1; // triangle b
                indices[i + 4] = (x * TerrainConstants.CHUNK_VERTEX_COUNT) + z + 1;
                indices[i + 5] = ((x + 1) * TerrainConstants.CHUNK_VERTEX_COUNT) + z;
                i = i + 6;
            }
        }


        mesh = new Mesh(vertices, indices, new Material("textures/water.png"));


        try {
            mesh.generateMesh();
        } catch (RequiredFileNotFoundException e) {
            logger.warn("Required file not found:", e.getMessage(), new ThreadState(Thread.currentThread()));
        }
    }

    public void update() {

    }

    public void render(Camera camera) {
        Renderer renderer = EngineCore.getGraphicsEngine().getRenderer();
        renderer.bindShader(ShaderManager.TEXTURE_SHADER());
        renderer.bindTexture(mesh.getMaterial().getTextureID());
        renderer.bindMesh(mesh);
        renderer.enableBlend();
        renderer.draw(Chunk.chunkPosToWorldPos(chunkPos), TerrainConstants.WATER_ROTATION, TerrainConstants.WATER_SCALE, camera);
        renderer.cleanUp();
    }

    public float getHeight(int x, int z) {
        return mesh.getVertices()[(x * TerrainConstants.CHUNK_VERTEX_COUNT) + z].getPosition().getY();
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void destroy() {
        mesh.destroy();
    }
}
