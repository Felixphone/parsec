package game.world.scenes.scenes.planet;

import engine.dataItems.exceptions.InvalidInputException;
import engine.engine.EngineCore;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import game.dataItems.constants.PlayerConstants;
import game.dataItems.constants.TerrainConstants;
import game.dataItems.exeptions.PlayerPosOutOfBoundsException;
import game.dataItems.interfaces.PlayerLogic;
import game.world.player.Player;
import game.world.scenes.SceneType;
import game.world.scenes.scenes.planet.terrain.Chunk;
import game.world.scenes.scenes.planet.terrain.ChunkPos;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class PlanetPlayerLogic implements PlayerLogic {

    private final Logger logger;

    private Vector3f previousPosition;
    private Player player;
    private Planet planet;

    private boolean isFlying = true;

    public PlanetPlayerLogic(Planet planet) {
        logger = EngineCore.getLogger();
        player = EngineCore.getGameCore().getWorld().getPlayer();
        this.planet = planet;
    }

    @Override
    public void update() {

        if (!Float.isNaN(player.getPosition().getY())) {
            previousPosition = player.getPosition();
        }

        checkMovementInputs();
        assertWithinValidBounds();

        player.getCamera().setPosition(Vector3f.add(player.getPosition(), player.getCameraPositionOffset()));

        loadUnloadChunks();

    }

    private void loadUnloadChunks() {

        ChunkPos containingChunkPos = Chunk.worldPosToChunkPos(player.getPosition());

        //load chunks within simulation diastance in a spiral pattern around player
        int x = 0;
        int y = 0;
        int dx = 0;
        int dy = -1;

        int simDist = PlayerConstants.SIMULATION_DISTANCE;

        outLoop:
        for (int i = 0; i < simDist * simDist * 4; i++) {

            ChunkPos chunkPos =  ChunkPos.add(new ChunkPos(x, y), containingChunkPos);

            if (!planet.getTerrain().isChunkLoaded(chunkPos)
                    & chunkPos.getX() <= TerrainConstants.MAX_GENERATION_DISTANCE
                    & chunkPos.getX() >= TerrainConstants.MIN_GENERATION_DISTANCE
                    & chunkPos.getZ() <= TerrainConstants.MAX_GENERATION_DISTANCE
                    & chunkPos.getZ() >= TerrainConstants.MIN_GENERATION_DISTANCE) {
                planet.getTerrain().loadChunk(chunkPos);
                break outLoop;
            }

            if (x == y || (x < 0 & x == -y) || (x > 0 & x == 1 - y)) {

                int dxTemp = dx;
                int dyTemp = dy;

                dx = -dyTemp;
                dy = dxTemp;
            }
            x += dx;
            y += dy;
        }

        //unload all chunks more than the sim distance away
        ArrayList<Chunk> chunksToUnload = new ArrayList<>();

        for (Chunk chunk : planet.getTerrain().getLoadedChunks()) {
            if (chunk.getChunkPos().getX() < containingChunkPos.getX() - PlayerConstants.SIMULATION_DISTANCE
                    || chunk.getChunkPos().getX() > containingChunkPos.getX() + PlayerConstants.SIMULATION_DISTANCE
                    || chunk.getChunkPos().getZ() < containingChunkPos.getZ() - PlayerConstants.SIMULATION_DISTANCE
                    || chunk.getChunkPos().getZ() > containingChunkPos.getZ() + PlayerConstants.SIMULATION_DISTANCE) {
                chunksToUnload.add(chunk);
            }
        }

        for (Chunk chunk : chunksToUnload) {
            planet.getTerrain().unloadChunk(chunk.getChunkPos());
        }
    }

    private void checkMovementInputs() {

        Vector3f resultPos = player.getPosition();

        float x = (float) Math.sin(Math.toRadians(player.getRotation().getY())) * player.getMovementSpeed();
        float z = (float) Math.cos(Math.toRadians(player.getRotation().getY())) * player.getMovementSpeed();

        try {
            if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_A)) {
                resultPos = Vector3f.add(resultPos, new Vector3f(-z, 0.0f, x));
            }

            if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_D)) {
                resultPos = Vector3f.add(resultPos, new Vector3f(z, 0.0f, -x));
            }

            if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_W)) {
                resultPos = Vector3f.add(resultPos, new Vector3f(-x, 0.0f, -z));
            }

            if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_S)) {
                resultPos = Vector3f.add(resultPos, new Vector3f(x, 0.0f, z));
            }

            if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_SPACE)) {
                isFlying = true;
                resultPos = Vector3f.add(resultPos, new Vector3f(0.0f, player.getMovementSpeed(), 0.0f));
            }

            if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                resultPos = Vector3f.add(resultPos, new Vector3f(0.0f, -player.getMovementSpeed(), 0.0f));
            }

            if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
                player.setMovementSpeed(player.getMovementSpeed() + PlayerConstants.CTRL_MOVEMENT_SPEED_INCREASE);
            } else {
                player.resetMovementSpeed();
            }

            if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_R)) {
                player.setPosition(PlayerConstants.STARTING_POS);
                player.setRotation(PlayerConstants.STARTING_ROTATION);
            }

            if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_M)) {
                EngineCore.getGameCore().getWorld().getSceneManager().loadScene(SceneType.SPACE_DOCK);
            }
        } catch (InvalidInputException invalidInputException) {
            logger.warn("Invalid input:", invalidInputException.getMessage(), new ThreadState(Thread.currentThread()));
        }

        if (EngineCore.getGraphicsEngine().getGameWindow().isMouseLocked()) { // only rotate if mouse is captured
            Vector2f mousePosDifferance = Vector2f.subtract(EngineCore.getMouseManager().getMousePos(), player.getPreviousMousePos());
            player.setPreviousMousePos(EngineCore.getMouseManager().getMousePos());

            player.rotate(new Vector3f(-mousePosDifferance.getY() * PlayerConstants.BASE_ROTATE_SPEED, -mousePosDifferance.getX() * PlayerConstants.BASE_ROTATE_SPEED, 0.0f));
        }

        player.setPosition(validateMovement(resultPos));
    }

    private Vector3f validateMovement(Vector3f resultPos) {

        ChunkPos chunkPos = Chunk.worldPosToChunkPos(resultPos);
        Vector3f chunkRelativePosition = Chunk.worldPosToChunkRelativePos(resultPos);

        if (!isFlying) { // if player is not flying, set y value to terrain height
            try {
                float terrainHeight = planet.getTerrain().getChunk(chunkPos).getHeight(chunkRelativePosition.getX(), chunkRelativePosition.getZ());

                if (!Float.isNaN(terrainHeight)) {
                    resultPos.setY(terrainHeight);
                }

            } catch (NullPointerException e) {
                logger.warn("Player out of chunk bounds", "(or current chunk is not loaded), terrain y pos not available whilst attempting to set y value of player", new ThreadState(Thread.currentThread()));
            }
        }

        else {
            try {

                float terrainHeight = planet.getTerrain().getChunk(chunkPos).getHeight(chunkRelativePosition.getX(), chunkRelativePosition.getZ());

                if (resultPos.getY() < terrainHeight) {
                    isFlying = false;

                    if (!Float.isNaN(terrainHeight)) {
                        resultPos.setY(terrainHeight);
                    }
                }
            } catch (NullPointerException e) {
                logger.warn("Player out of chunk bounds", "(or current chunk is not loaded), terrain y pos not available whilst attempting to check if player is below terrain", new ThreadState(Thread.currentThread()));
            }
        }

        if (Float.isNaN(resultPos.getY())) {
            resultPos.setY(previousPosition.getY());
        }

        if (resultPos.getX() > PlayerConstants.MAX_POS_VALUE) {
            resultPos.setX(PlayerConstants.MAX_POS_VALUE);
        }
        if (resultPos.getX() < PlayerConstants.MIN_POS_VALUE) {
            resultPos.setX(PlayerConstants.MIN_POS_VALUE);
        }
        if (resultPos.getY() > PlayerConstants.MAX_Y_VALUE) {
            resultPos.setY(PlayerConstants.MAX_Y_VALUE);
        }
        if (resultPos.getY() < PlayerConstants.MIN_Y_VALUE) {
            resultPos.setY(PlayerConstants.MIN_Y_VALUE);
        }
        if (resultPos.getZ() > PlayerConstants.MAX_POS_VALUE) {
            resultPos.setZ(PlayerConstants.MAX_POS_VALUE);
        }
        if (resultPos.getZ() < PlayerConstants.MIN_POS_VALUE) {
            resultPos.setZ(PlayerConstants.MIN_POS_VALUE);
        }

        return resultPos;

    }

    public void assertWithinValidBounds() {
        if (player.getPosition().getX() > PlayerConstants.MAX_POS_VALUE
                || player.getPosition().getZ() > PlayerConstants.MAX_POS_VALUE) { // crash if player pos exceeds max bound
            throw new PlayerPosOutOfBoundsException("Player pos out of bounds!", "Player X/Z position (" + player.getPosition().toString() + ") exceeds max value of " + PlayerConstants.MAX_POS_VALUE, "Expression ( player.getPosition().getX() > PlayerConstants.MAX_POS_VALUE || player.getPosition().getZ() > PlayerConstants.MAX_POS_VALUE ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }

        if (player.getPosition().getX() < PlayerConstants.MIN_POS_VALUE
                || player.getPosition().getZ() < PlayerConstants.MIN_POS_VALUE) { // crash if player pos exceeds min bound
            throw new PlayerPosOutOfBoundsException("Player pos out of bounds!", "Player X/Z position (" + player.getPosition().toString() + ") exceeds min value of " + PlayerConstants.MIN_POS_VALUE, "Expression (player.getPosition().getX() < PlayerConstants.MIN_POS_VALUE || player.getPosition().getZ() < PlayerConstants.MIN_POS_VALUE ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }

        if (player.getPosition().getY() > PlayerConstants.MAX_Y_VALUE) { // crash if player pos exceeds max bound
            throw new PlayerPosOutOfBoundsException("Player pos out of bounds!", "Player Y position (" + player.getPosition().toString() + ") exceeds max value of " + PlayerConstants.MAX_Y_VALUE, "Expression ( player.getPosition().getY() > PlayerConstants.MAX_Y_VALUE ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }

        if (player.getPosition().getY() < PlayerConstants.MIN_Y_VALUE) { // crash if player pos exceeds min bound
            throw new PlayerPosOutOfBoundsException("Player pos out of bounds!", "Player Y position (" + player.getPosition().toString() + ") exceeds min value of " + PlayerConstants.MAX_Y_VALUE, "Expression ( player.getPosition().getY() < PlayerConstants.MIN_Y_VALUE ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }
    }

    @Override
    public void destroy() {
        logger.success("Successfully destroyed playerLogic!", "", new ThreadState(Thread.currentThread()));

    }
}
