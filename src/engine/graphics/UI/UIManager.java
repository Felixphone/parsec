package engine.graphics.UI;

import engine.engine.EngineCore;
import engine.graphics.UI.screens.Screen;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;

public class UIManager {

    private final Logger logger;
    
    private static Screen screen = null;

    public UIManager() {
        logger = EngineCore.getLogger();
        logger.success("Successfully initialised renderer", "", new ThreadState(Thread.currentThread()));
    }

    public static void update() {
        screen.update();
    }

    public static void render(){
        screen.render();
    }

    public static void setScreen(Screen screen) {
        UIManager.screen = screen;
    }

    public static Screen getScreen() {
        return screen;
    }
}
