package game.world.scenes.scenes.planet.terrain;

import engine.engine.EngineCore;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import engine.utilities.NoiseGenerator;
import game.dataItems.constants.TerrainConstants;
import game.world.scenes.scenes.planet.Planet;
import game.world.player.Camera;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Terrain {

    private final Logger logger;
    private final Planet planet;

    private ArrayList<Chunk> loadedChunks = new ArrayList<>();
    private int seed;
    private final String rootSaveDirectory;

    private NoiseGenerator noiseGenerator;
    private ExecutorService chunkGeneratorThreadPool;

    public Terrain(int seed, String rootSaveDirectory, Planet planet) {
        this.seed = seed;
        this.rootSaveDirectory = rootSaveDirectory;
        this.planet = planet;
        logger = EngineCore.getLogger();
        logger.info("RootSaveDirectory set", "to: \"" + rootSaveDirectory + "\"", new ThreadState(Thread.currentThread()));

        logger.attempt("Generating noise...", "", new ThreadState(Thread.currentThread()));
        generate();
        logger.success("Noise generator initialised!", "", new ThreadState(Thread.currentThread()));

        logger.attempt("Starting generator thread...", "", new ThreadState(Thread.currentThread()));
        chunkGeneratorThreadPool = Executors.newCachedThreadPool();

        logger.success("Terrain successfully initialised!", "", new ThreadState(Thread.currentThread()));
    }

    private void generate() {

        logger.info("Seed: ", "" + seed, new ThreadState(Thread.currentThread()));
        noiseGenerator = new NoiseGenerator(seed, TerrainConstants.NOISE_PERSISTENCE, TerrainConstants.NOISE_FREQUENCY, TerrainConstants.NOISE_AMPLITUDE, TerrainConstants.NOISE_OCTAVES);
    }

    public void update() {

        for (Chunk chunk : loadedChunks) {
            chunk.update();
        }
    }

    public void render(Camera camera) {

        for (Chunk chunk:loadedChunks) {
            chunk.render(camera);
        }

        for (Chunk chunk:loadedChunks) {
            chunk.getWaterTable().render(camera); // must be rendered last after all terrain or transparancy will not work
        }
    }

    public boolean isChunkLoaded(ChunkPos chunkPos) {
        for (Chunk chunk:loadedChunks) {
            if (chunk.getChunkPos().equals(chunkPos)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasChunkBeenGenerated(ChunkPos chunkPos) {
        File file = new File(Chunk.getFullSaveName(chunkPos, rootSaveDirectory));
        if (file.exists()) {
            return true;
        }

        if (isChunkLoaded(chunkPos)) {
            return true;
        }

        return false;
    }

    public Chunk getChunk(ChunkPos chunkPos) {
        for (Chunk chunk:loadedChunks) {
            if (chunk.getChunkPos().equals(chunkPos)) {
                return chunk;
            }
        }
        return null;
    }

    public ArrayList<Chunk> getLoadedChunks() {
        return loadedChunks;
    }

    public void loadChunk(ChunkPos chunkPos) {

        if (chunkPos.getX() >= TerrainConstants.MIN_GENERATION_DISTANCE
                & chunkPos.getX() <= TerrainConstants.MAX_GENERATION_DISTANCE
                & chunkPos.getZ() >= TerrainConstants.MIN_GENERATION_DISTANCE
                & chunkPos.getZ() <= TerrainConstants.MAX_GENERATION_DISTANCE) {
            if (!isChunkLoaded(chunkPos)) {
                if (hasChunkBeenGenerated(chunkPos)) {
                    //chunkGeneratorThreadPool.submit(() -> { // TODO: figure out why LWJGL doesn't like other-thread contexts
                        logger.info("Loading chunk:", "[" + chunkPos.getX() + "," + chunkPos.getZ() + "] (world pos: " + Chunk.chunkPosToWorldPos(chunkPos).getX() + "," + Chunk.chunkPosToWorldPos(chunkPos).getZ() + ") from save file... Loaded chunks: " + loadedChunks.size(), new ThreadState(Thread.currentThread()));
                        Chunk chunk = new Chunk(chunkPos, noiseGenerator, false, rootSaveDirectory, planet);
                        loadedChunks.add(chunk);
                    //});
                } else {
                    //chunkGeneratorThreadPool.submit(() -> {
                        logger.info("Generating fresh chunk:", "[" + chunkPos.getX() + "," + chunkPos.getZ() + "] (world pos: " + Chunk.chunkPosToWorldPos(chunkPos).getX() + "," + Chunk.chunkPosToWorldPos(chunkPos).getZ() + ") Loaded chunks: " + loadedChunks.size(), new ThreadState(Thread.currentThread()));
                        Chunk chunk = new Chunk(chunkPos, noiseGenerator, true, rootSaveDirectory, planet);
                        loadedChunks.add(chunk);
                    //});
                }
            }
        }
    }

    public void unloadChunk(ChunkPos chunkPos) {
        if (isChunkLoaded(chunkPos)) {
            getChunk(chunkPos).save();
            getChunk(chunkPos).destroy();
            loadedChunks.remove(getChunk(chunkPos));
        }
    }

    public void saveAllLoadedChunks() {
        for (Chunk chunk:loadedChunks) {
            chunk.save();
        }
    }

    public int getSeed() {
        return seed;
    }

    public void destroy() {

        for (Chunk chunk:loadedChunks) {
            chunk.save();
            chunk.destroy();
        }

        chunkGeneratorThreadPool.shutdown();
    }
}
