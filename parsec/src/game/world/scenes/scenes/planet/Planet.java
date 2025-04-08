package game.world.scenes.scenes.planet;

import engine.engine.EngineCore;
import engine.maths.Vector3f;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import game.dataItems.constants.TestConstants;
import game.dataItems.interfaces.PlayerLogic;
import game.utilities.FileUtils;
import game.world.player.Camera;
import game.world.scenes.scenes.Scene;
import game.world.scenes.scenes.planet.terrain.Terrain;
import game.world.scenes.scenes.planet.objects.Sun;

import java.io.IOException;
import java.util.Random;

public class Planet implements Scene {

    private final Logger logger;
    
    private final String savePath;

    private final Terrain terrain;
    private final PlanetPlayerLogic playerLogic;
    private final Sun sun;

    public Planet() {
        logger = EngineCore.getLogger();        
        int seed = EngineCore.getGameCore().getWorld().getSeed();

        savePath = EngineCore.getGameCore().getWorld().getSavePath() + "/planet";
        logger.attempt("Generating directory:", "\"" + savePath + "\"...", new ThreadState(Thread.currentThread()));
        initialiseFiles();
        logger.success("Files generated!", "", new ThreadState(Thread.currentThread()));

        logger.attempt("Initialising terrain...", "", new ThreadState(Thread.currentThread()));
        terrain = new Terrain(seed, savePath, this);

        logger.attempt("Initialising PlanetPlayerLogic...", "", new ThreadState(Thread.currentThread()));
        playerLogic = new PlanetPlayerLogic(this);

        sun = new Sun(new Vector3f(102, 5, 90), TestConstants.ONE_VEC3F, TestConstants.ONE_VEC3F);

        logger.success("Planet successfully initialised!", "", new ThreadState(Thread.currentThread()));
    }

    @Override
    public void update(){
        terrain.update();
        playerLogic.update();
        sun.update();
    }

    @Override
    public void render(Camera camera) {
        terrain.render(camera);
        //sun.render(camera);
    }

    @Override
    public PlayerLogic getPlayerLogic() {
        return playerLogic;
    }

    private void initialiseFiles() {
        //make directories
        FileUtils.createDirectory(savePath);
        FileUtils.createDirectory(savePath + "/terrain");
        FileUtils.createDirectory(savePath + "/entities");

        //make files
        try {
            FileUtils.createFile(savePath + "/config.json");
            FileUtils.writeToFile(savePath + "/config.json", "{ \"__comment__\": \"===== Currently disused =====\"}");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getSavePath() {
        return savePath;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void destroy(){

        logger.attempt("Destroying terrain...", "", new ThreadState(Thread.currentThread()));
        terrain.destroy();

        logger.attempt("Destroying SpacePlayerLogic...", "", new ThreadState(Thread.currentThread()));
        playerLogic.destroy();

        logger.success("Successfully destroyed world!", "", new ThreadState(Thread.currentThread()));
    }
}