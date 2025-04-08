package game.world.scenes.scenes.planet.terrain;

import engine.dataItems.exceptions.FailedToCompileShaderException;
import engine.dataItems.exceptions.FailedToLinkShaderException;
import engine.dataItems.exceptions.RequiredFileNotFoundException;
import engine.engine.EngineCore;
import engine.graphics.Renderer;
import engine.graphics.ShaderManager;
import engine.graphics.objects.*;
import engine.maths.Vector3f;
import engine.maths.Vector2f;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import engine.utilities.NoiseGenerator;
import engine.utilities.resources.ResourceLocation;
import game.dataItems.constants.TerrainConstants;
import game.world.scenes.scenes.planet.Planet;
import game.world.player.Camera;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class Chunk {

    private final Logger logger;
    
    private final Planet planet;
    private final ChunkPos chunkPos;
    private NoiseGenerator noiseGenerator;
    private Mesh mesh;
    private Shader shader;
    private final String savePath;

    private final WaterTable waterTable;

    public Chunk(ChunkPos chunkPos, NoiseGenerator noiseGenerator, boolean freshChunk, String rootSaveDirectory, Planet planet){
        this.chunkPos = chunkPos;
        this.noiseGenerator = noiseGenerator;
        this.planet = planet;
        logger = EngineCore.getLogger();
        savePath = getFullSaveName(chunkPos, rootSaveDirectory);

        //create shaders
        try {
            shader = new Shader(new ResourceLocation("shaders/mainVertex.vert"), new ResourceLocation("shaders/textureFragment.frag"));

            try {
                shader.createShader();
            } catch (FailedToCompileShaderException | FailedToLinkShaderException e) {
                logger.warn("Shader warning:", e.getMessage(), new ThreadState(Thread.currentThread()));
            }

        } catch (IOException | RequiredFileNotFoundException e) {
            logger.warn("File could not be found:", e.getMessage(), new ThreadState(Thread.currentThread()));
        }

        if (freshChunk) {
            initiateFiles();
            generate();
        }

        else {
            loadExisting();
        }

        waterTable = new WaterTable(chunkPos, planet);

    }

    private void generate() {

        //generate heights
        Vertex[] vertices = new Vertex[TerrainConstants.CHUNK_VERTEX_COUNT * TerrainConstants.CHUNK_VERTEX_COUNT];
        int[] indices = new int[TerrainConstants.CHUNK_VERTEX_COUNT * TerrainConstants.CHUNK_VERTEX_COUNT * 6];

        logger.fine("Loading terrain chunk:", chunkPos.getX() + " , " + chunkPos.getZ(), new ThreadState(Thread.currentThread()));

        for (int x = 0; x < TerrainConstants.CHUNK_VERTEX_COUNT; x++){
            for (int z = 0; z < TerrainConstants.CHUNK_VERTEX_COUNT; z++) {

                float worldX = (chunkPos.getX() * TerrainConstants.CHUNK_SIZE) + x;
                float worldZ = (chunkPos.getZ() * TerrainConstants.CHUNK_SIZE) + z;

                vertices[(x * TerrainConstants.CHUNK_VERTEX_COUNT) + z] = new Vertex(new Vector3f(x, (float) noiseGenerator.get(worldX * 0.01, worldZ * 0.01), z), new ColourVector(0.0f, 0.0f, 0.0f, 0.0f), new Vector2f(x, z));
                logger.ultraFine("Loading terrain vertex:" ,x + ":" + z + " / " + TerrainConstants.CHUNK_VERTEX_COUNT + ":" + TerrainConstants.CHUNK_VERTEX_COUNT + " = " +  vertices[(x * TerrainConstants.CHUNK_VERTEX_COUNT) + z].toString(), new ThreadState(Thread.currentThread()));

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

        mesh = new Mesh(vertices, indices, new Material("textures/terrain.png"));


        try {
            mesh.generateMesh();
        } catch (RequiredFileNotFoundException e) {
            logger.warn("File not found:", e.getMessage(), new ThreadState(Thread.currentThread()));
        }

    }

    public void update() {
        waterTable.update();
    }

    public void render(Camera camera) {
        Renderer renderer = EngineCore.getGraphicsEngine().getRenderer();
        renderer.bindShader(ShaderManager.TEXTURE_SHADER());
        renderer.bindTexture(mesh.getMaterial().getTextureID());
        renderer.bindMesh(mesh);
        renderer.draw(chunkPosToWorldPos(chunkPos), TerrainConstants.CHUNK_ROTATION, TerrainConstants.CHUNK_SCALE, camera);
        renderer.cleanUp();
    }

    public static Vector3f chunkPosToWorldPos(ChunkPos chunkPos) {
        return new Vector3f(chunkPos.getX() * TerrainConstants.CHUNK_SIZE, 0.0f, chunkPos.getZ() * TerrainConstants.CHUNK_SIZE);
    }

    public static ChunkPos worldPosToChunkPos(Vector3f worldPos) {
        return new ChunkPos((int) Math.floor(worldPos.getX() / TerrainConstants.CHUNK_SIZE), (int) Math.floor(worldPos.getZ() / TerrainConstants.CHUNK_SIZE));
    }

    public static Vector3f worldPosToChunkRelativePos(Vector3f worldPos) {
        ChunkPos chunkPos = worldPosToChunkPos(worldPos);
        return new Vector3f(worldPos.getX() - (chunkPos.getX() * TerrainConstants.CHUNK_SIZE), worldPos.getY(), worldPos.getZ()  - (chunkPos.getZ() * TerrainConstants.CHUNK_SIZE));
    }

    public static String getFullSaveName(ChunkPos chunkPos, String rootSaveDirectory) {
        return rootSaveDirectory + "/terrain/chunk_" + chunkPos.getX() + "_" + chunkPos.getZ() + ".PSChunk";
    }


    public float getHeight(float chunkRelativeX, float chunkRelativeZ) {

        try {
            int vec1X, vec1Z, vec2X, vec2Z;
            float vec1Y, vec2Y;

            vec1X = (int) Math.floor(chunkRelativeX);
            vec2X = (int) Math.ceil(chunkRelativeX);

            vec1Y = (int) mesh.getVertices()[(int) ((vec1X * TerrainConstants.CHUNK_VERTEX_COUNT) + Math.floor(chunkRelativeZ))].getPosition().getY();
            vec2Y = (int) mesh.getVertices()[(int) ((vec2X * TerrainConstants.CHUNK_VERTEX_COUNT) + Math.floor(chunkRelativeZ))].getPosition().getY();
            float xAxisIntermediateLow = findIntermediate(vec1X, vec1Y, vec2X, vec2Y, chunkRelativeX);

            vec1Y = (int) mesh.getVertices()[(int) ((vec1X * TerrainConstants.CHUNK_VERTEX_COUNT) + Math.ceil(chunkRelativeZ))].getPosition().getY();
            vec2Y = (int) mesh.getVertices()[(int) ((vec2X * TerrainConstants.CHUNK_VERTEX_COUNT) + Math.ceil(chunkRelativeZ))].getPosition().getY();
            float xAxisIntermediateHigh = findIntermediate(vec1X, vec1Y, vec2X, vec2Y, chunkRelativeX);

            vec1Z = (int) Math.floor(chunkRelativeZ);
            vec2Z = (int) Math.ceil(chunkRelativeZ);

            return findIntermediate(vec1Z, xAxisIntermediateLow, vec2Z, xAxisIntermediateHigh, chunkRelativeZ);

        } catch (Exception e) {
            logger.warn("Exception getting terrain height:", e.getMessage(), new ThreadState(Thread.currentThread()));
            return 0;
        }
    }

    private float findIntermediate(int vec1X, float vec1Y, int vec2X, float vec2Y, float x){

        float gradient = (vec2Y - vec1Y) / (vec2X - vec1X);
        float yIntercept = vec1Y - gradient  * vec1X;

        return (gradient * x) + yIntercept;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public WaterTable getWaterTable() {
        return waterTable;
    }

    public void loadExisting() {

        try {
            FileReader fileReader = new FileReader(savePath);
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(fileReader);

            Vertex[] vertices = new Vertex[TerrainConstants.CHUNK_VERTEX_COUNT * TerrainConstants.CHUNK_VERTEX_COUNT];
            int[] indices = new int[TerrainConstants.CHUNK_VERTEX_COUNT * TerrainConstants.CHUNK_VERTEX_COUNT * 6];

            float worldX = (chunkPos.getX() * TerrainConstants.CHUNK_VERTEX_COUNT);
            float worldZ = (chunkPos.getZ() * TerrainConstants.CHUNK_VERTEX_COUNT);
            logger.fine("Loading terrain chunk:", chunkPos.getX() + " , " + chunkPos.getZ() + " at: " + worldX + "," + worldZ, new ThreadState(Thread.currentThread()));

            for (int x = 0; x < TerrainConstants.CHUNK_VERTEX_COUNT; x++){
                for (int z = 0; z < TerrainConstants.CHUNK_VERTEX_COUNT; z++) {

                    int vertexNumber = (x * TerrainConstants.CHUNK_VERTEX_COUNT) + z;
                    JSONObject vertex = (JSONObject) jsonObject.get("v" + vertexNumber);
                    if (vertices[(x * TerrainConstants.CHUNK_VERTEX_COUNT) + z] != null) {
                        logger.ultraFine("Loading terrain vertex " + vertexNumber + " : ", x + ":" + z + " / " + TerrainConstants.CHUNK_VERTEX_COUNT + ":" + TerrainConstants.CHUNK_VERTEX_COUNT + " = " + vertices[(x * TerrainConstants.CHUNK_VERTEX_COUNT) + z].toString(), new ThreadState(Thread.currentThread()));
                        vertices[(x * TerrainConstants.CHUNK_VERTEX_COUNT) + z] = new Vertex(new Vector3f(x, Float.parseFloat((String) vertex.get("y")), z), new ColourVector(0.0f, 0.0f, 0.0f, 0.0f), new Vector2f(x, z));
                    }
                    else {
                        logger.error("Terrain vertex is null!", "vertices[" + ((x * TerrainConstants.CHUNK_VERTEX_COUNT) + z) + "] is null, where X: " + x + ", Z: " + z + " of chunk: " + chunkPos.getX() + " , " + chunkPos.getZ() + " at: " + worldX + "," + worldZ, new ThreadState(Thread.currentThread()));
                    }
                }
            }

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

            String texPath = "textures/terrain.png";

            mesh = new Mesh(vertices, indices, new Material(texPath));


            try {
                mesh.generateMesh();
            } catch (RequiredFileNotFoundException e) {
                logger.warn("File not found:", "The file \"" + texPath + "\" could not be found: " + e.getMessage(), new ThreadState(Thread.currentThread()));
            }

        } catch (FileNotFoundException e) {
            logger.error("File not found:", "Save file for chunk[" + chunkPos.getX() + "," + chunkPos.getZ() + "] could not be found: " + e.getMessage(), new ThreadState(Thread.currentThread()));
            if (TerrainConstants.REGENERATE_CORRUPT_CHUNKS) {
                generate();
            }

        } catch (ParseException e) {
            logger.error("Exception whilst parsing file:", "Save file for chunk[" + chunkPos.getX() + "," + chunkPos.getZ() + "] could not be parsed (file may be corrupt): " + e.getLocalizedMessage(), new ThreadState(Thread.currentThread()));
            if (TerrainConstants.REGENERATE_CORRUPT_CHUNKS) {
                generate();
            }

        } catch (IOException e) {
            logger.error("File not accessible:", "Save file for chunk[" + chunkPos.getX() + "," + chunkPos.getZ() + "] could not be accessed: " + e.getMessage(), new ThreadState(Thread.currentThread()));
            generate();
        }
    }

    private void initiateFiles() {

        try {
            new File(savePath).createNewFile();

        } catch (IOException e) {
            logger.error("File error whilst saving:", "File error whilst attempting to create save file \"" + savePath + "\" for chunk[" + chunkPos.getX() + "," + chunkPos.getZ() + "]: " + e.getMessage(), new ThreadState(Thread.currentThread()));
        }
    }

    public void save() {
        try {
            FileWriter fileWriter = new FileWriter(savePath);

            String saveData = "{\n\n";

            for (int i = 0; i < mesh.getVertices().length; i++) {

                Vertex vertex = mesh.getVertices()[i];

                saveData = saveData + "\t\"v" + i + "\":{\n" +
                        "\t\t\"x\":\"" + vertex.getPosition().getX() + "\",\n" +
                        "\t\t\"y\":\"" + vertex.getPosition().getY() + "\",\n" +
                        "\t\t\"z\":\"" + vertex.getPosition().getZ() + "\"\n" +
                        "\t}";

                if (i+1 != mesh.getVertices().length) {
                    saveData = saveData + ",\n\n";
                }
            }

            saveData = saveData + "\n}";
            fileWriter.write(saveData);
            fileWriter.close();

        } catch (FileNotFoundException e) {
            logger.error("Save file not found:", "The save file \"" + savePath + "\" could not be found (is TestConstants.deleteSavesOnExit set to true?): " + e.getMessage(), new ThreadState(Thread.currentThread()));
        } catch (IOException e) {
            logger.error("Failed to save chunk:", "Could not save chunk[" + chunkPos.getX() + "," + chunkPos.getZ() + "]: " + e.getMessage(), new ThreadState(Thread.currentThread()));

        }
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "chunkPos='" + chunkPos.toString() + '\'' +
                "savePath='" + savePath + '\'' +
                '}';
    }

    public void destroy() {
        mesh.destroy();
    }
}
