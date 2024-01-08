package game.world.scenes;

import engine.engine.EngineCore;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import game.world.scenes.scenes.Scene;
import game.world.scenes.scenes.planet.Planet;

public class SceneManager {

    private final Logger logger;
    private Scene currentScene;

    public SceneManager() {
        logger = EngineCore.getLogger();
        logger.success("Files generated!", "", new ThreadState(Thread.currentThread()));
    }

    public void loadScene(SceneType sceneToLoad) {
        logger.attempt("Loading scene:", sceneToLoad.name() + "...", new ThreadState(Thread.currentThread()));
        switch (sceneToLoad) {
            case PLANET:
                currentScene = new Planet();
                break;
        }
    }

    public void update() {
        if (currentScene != null) {
            currentScene.update();
        }
    }

    public void render() {
        if (currentScene != null) {
            currentScene.render(EngineCore.getGameCore().getWorld().getPlayer().getCamera());
        }
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
