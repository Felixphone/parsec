package game.world;

import engine.engine.EngineCore;
import tracerUtils.data.ExceptionContext;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import game.dataItems.constants.PlayerConstants;
import game.dataItems.constants.TestConstants;
import game.utilities.FileUtils;
import game.world.player.Player;
import game.world.scenes.SceneManager;
import game.world.scenes.SceneType;
import tracerUtils.traceableException.TraceableException;

import java.io.IOException;
import java.util.Random;

public class World {

    private final Logger logger;
    
    private String name;
    private boolean freshWorld;

    private boolean hasLoaded = false;

    private int seed;
    private String savePath;

    private SceneManager sceneManager;
    private Player player;

    public World(String name, boolean freshWorld) {
        this.name = name;
        this.freshWorld = freshWorld;
        logger = EngineCore.getLogger();

        try {
            if (freshWorld) {
                Random randomGenerator = new Random();
                logger.attempt("Generating seed...", "", new ThreadState(Thread.currentThread()));
                seed = randomGenerator.nextInt(10000);
                logger.success("Generated seed: ", "" + seed, new ThreadState(Thread.currentThread()));

                savePath = "saves/" + name;
                logger.attempt("Generating saves directory:" ,"\"" + savePath + "\"...", new ThreadState(Thread.currentThread()));
                initialiseFiles();
                logger.success("Files generated!", "", new ThreadState(Thread.currentThread()));

                logger.attempt("Initialising scene manager...", "", new ThreadState(Thread.currentThread()));
                sceneManager = new SceneManager();

                logger.attempt("Initialising player...", "", new ThreadState(Thread.currentThread()));
                player = new Player(PlayerConstants.STARTING_POS, PlayerConstants.STARTING_ROTATION, PlayerConstants.STARTING_SIZE);
            } else {

                logger.attempt("Loading world seed from save file...", "", new ThreadState(Thread.currentThread()));
                seed = 1;
                logger.success("Generated seed: ", "" + seed, new ThreadState(Thread.currentThread()));

                savePath = "saves/" + name;
                logger.attempt("Checking directory:", "\"" + savePath + "\"...", new ThreadState(Thread.currentThread()));
                //initialiseFiles();
                logger.success("No directory elements missing!", "", new ThreadState(Thread.currentThread()));

                logger.attempt("Initialising scene manager from save file...", "", new ThreadState(Thread.currentThread()));
                sceneManager = new SceneManager();

                logger.attempt("Initialising player from save file...", "", new ThreadState(Thread.currentThread()));
                player = new Player(PlayerConstants.STARTING_POS, PlayerConstants.STARTING_ROTATION, PlayerConstants.STARTING_SIZE);
            }

            logger.success("World successfully initialised!", "", new ThreadState(Thread.currentThread()));
        } catch (TraceableException e) {
            e.contextualiseAndRethrow(new ExceptionContext("Attempted to initialise new world: " + name, new ThreadState(Thread.currentThread())));
        }
    }

    public void update(){
        try {
            if (!hasLoaded) {
                hasLoaded = true;
                sceneManager.loadScene(SceneType.PLANET);
            }

            sceneManager.update();
        } catch (TraceableException e) {
            e.contextualiseAndRethrow(new ExceptionContext("Attempted to update world: " + name, new ThreadState(Thread.currentThread())));
        }
    }

    public void render() {
        try {
            sceneManager.render();
        } catch (TraceableException e) {
            e.contextualiseAndRethrow(new ExceptionContext("Attempted to render world: " + name, new ThreadState(Thread.currentThread())));
        }
    }

    private void initialiseFiles() {
        //make directories
        FileUtils.createDirectory(savePath);
        FileUtils.createDirectory(savePath + "/data");
        FileUtils.createDirectory(savePath + "/data/planets");
        FileUtils.createDirectory(savePath + "/data/dock");
        FileUtils.createDirectory(savePath + "/data/space");

        //make files
        try {
            FileUtils.createFile(savePath + "/config.json");
            FileUtils.writeToFile(savePath + "/config.json", "{ \"__comment__\": \"===== Currently disused =====\"}");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void save() {
        logger.attempt("Saving world...", "", new ThreadState(Thread.currentThread()));

    }

    public void stop() {

    }

    public String getName() {
        return name;
    }

    public int getSeed() {
        return seed;
    }

    public Player getPlayer() {
        return player;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public String getSavePath() {
        return savePath;
    }

    public void destroy(){

        logger.attempt("Destroying world...", "", new ThreadState(Thread.currentThread()));

        // THE FOLLOWING CODE IN THE IF STATEMENT IS FOR TESTING PURPOSES ONLY  TODO: REMOVE THIS ON DEPLOYMENT
        if (TestConstants.deleteSavesOnExit) {
            try {
                FileUtils.deleteDirectory(savePath);
            } catch (IOException e) {
                logger.error("Unable to delete directory:", e.getMessage() , new ThreadState(Thread.currentThread()));
            }
        }
        // ends here

        logger.attempt("Destroying player...", "", new ThreadState(Thread.currentThread()));
        player.destroy();

        logger.success("Successfully destroyed world!", "", new ThreadState(Thread.currentThread()));
    }
}
