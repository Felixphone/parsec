package game;

import engine.dataItems.constants.GameWindowConstants;
import engine.dataItems.exceptions.InvalidInputException;
import launcher.watchdog.EngineWatchdog;
import tracerUtils.data.ExceptionContext;
import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;
import tracerUtils.traceableException.defaultExceptions.fatal.SimulatedException;
import engine.engine.EngineCore;
import engine.graphics.UI.UIManager;
import engine.graphics.UI.screens.TitleScreen;
import tracerUtils.logger.Logger;
import game.dataItems.exeptions.ConcurrentWorldLoadException;
import game.world.World;
import tracerUtils.exitReport.QuitReason;
import org.lwjgl.glfw.GLFW;

public class GameCore {

    private final Logger logger;

    private World world = null;

    private boolean isF11PressRegistered = false;
    private boolean isEscapePressRegistered = false;

    public GameCore() {
        logger = EngineCore.getLogger();
        UIManager.setScreen(new TitleScreen());

        try {
            createNewWorld("World");
            createNewWorld("World");
            createNewWorld("World");
            createNewWorld("World");
        }
        catch (ConcurrentWorldLoadException e) {
            //EngineWatchdog.getLogger().error();
        }

        EngineCore.getLogger().success("Core successfully initialised!", "", new ThreadState(Thread.currentThread()));
    }

    public void update() { // <! TO BE CALLED BY GAME_ENGINE ONLY !> called by engine.engine.EngineCore every update ( prior to GameWindow's update method being called )
        try {
            checkIfShouldForceCrash();
            checkIfShouldQuit();
            checkIfShouldToggleMouseLocked();
            checkIfShouldToggleFullscreen();

            if (world != null) {
                world.update();
            }
        } catch (TraceableException e) {
            e.contextualiseAndRethrow(new ExceptionContext("Attempted to update", new ThreadState(Thread.currentThread())));
        }
    }

    public void render() { // <! TO BE CALLED BY GAME_ENGINE ONLY !> called by engine.engine.EngineCore every render
        try {
            if (world != null) {
                world.render();
            }
        } catch (TraceableException e) {
            e.contextualiseAndRethrow(new ExceptionContext("Attempted to render", new ThreadState(Thread.currentThread())));
        }
    }

    public void createNewWorld(String name) throws ConcurrentWorldLoadException {
        logger.attempt("Creating new world...", "", new ThreadState(Thread.currentThread()));

        if (world != null) {
            throw new ConcurrentWorldLoadException("Concurrent world loading was attempted!", "A world: \"" + name + "\" was attempted to be created and loaded whilst an existing world: \"" + world.getName() + "\" was loaded", "Expression ( world != null ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }

        logger.attempt("Initialising world...", "", new ThreadState(Thread.currentThread()));
        world = new World(name, true);
    }

    public void loadWorld(String name) {
        try {
            if (world != null) {
                throw new ConcurrentWorldLoadException("Concurrent world loading was attempted!", "A world: \"" + name + "\" was attempted to be loaded whilst an existing world: \"" + world.getName() + "\" was loaded", "Expression ( world != null ) evaluated TRUE", new ThreadState(Thread.currentThread()));
            }

            logger.attempt("Loading world:", " \"" + name + "\" form save file", new ThreadState(Thread.currentThread()));
            logger.attempt("Initialising world...", "", new ThreadState(Thread.currentThread()));
            world = new World(name, false);
        }
        catch (ConcurrentWorldLoadException e) {
            logger.error("Concurrent world loading attempted:", e.asString(), new ThreadState(Thread.currentThread()));
        }
    }

    public void closeWorld() {
        if (world != null) {
            logger.attempt("Closing world...", "", new ThreadState(Thread.currentThread()));
            world.save();
            world.stop();
            world.destroy();
            world = null;
        }
    }

    public World getWorld() {
        return world;
    }

    private void checkIfShouldToggleFullscreen() {
        try {

            if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_F11)) {

                if (!isF11PressRegistered) { // check if f11 is pressed and if so, toggle fullscreen
                    logger.attempt("Toggling fullscreen (F11 was pressed)...", "", new ThreadState(Thread.currentThread()));
                    EngineCore.getGraphicsEngine().getGameWindow().setFullscreen(!EngineCore.getGraphicsEngine().getGameWindow().isFullscreen());
                    isF11PressRegistered = true; // prevent method being called multiple times per press
                    logger.success("Set fullscreen to " + EngineCore.getGraphicsEngine().getGameWindow().isFullscreen() + "!", "", new ThreadState(Thread.currentThread()));
                }

            } else {

                isF11PressRegistered = false;
            }
        } catch (InvalidInputException invalidInputException) {
            logger.warn("Invalid input:", invalidInputException.getMessage(), new ThreadState(Thread.currentThread()));
        }
    }

    private void checkIfShouldToggleMouseLocked() {

        if (world != null) {
            try {

                if (EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {

                    if (!isEscapePressRegistered) { // check if ESCAPE is pressed and if so, toggle mouseLocked
                        logger.attempt("Toggling mouseLocked (ESCAPE was pressed)...", "", new ThreadState(Thread.currentThread()));
                        EngineCore.getGraphicsEngine().getGameWindow().setMouseLocked(!EngineCore.getGraphicsEngine().getGameWindow().isMouseLocked());
                        isEscapePressRegistered = true; // prevent method being called multiple times per press
                        logger.success("Set mouseLocked to " + EngineCore.getGraphicsEngine().getGameWindow().isMouseLocked() + "!", "", new ThreadState(Thread.currentThread()));
                    }

                } else if (EngineCore.getMouseManager().isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT) & !EngineCore.getGraphicsEngine().getGameWindow().isMouseLocked() & GameWindowConstants.SHOULD_LOCK_MOUSE_ON_LEFT_CLICK) { // relock mouse when window is clicked on
                    logger.attempt("Enabling mouseLocked (MOUSE_LEFT was pressed)...", "", new ThreadState(Thread.currentThread()));
                    EngineCore.getGraphicsEngine().getGameWindow().setMouseLocked(true);
                    logger.success("Set mouseLocked to " + EngineCore.getGraphicsEngine().getGameWindow().isMouseLocked() + "!", "", new ThreadState(Thread.currentThread()));

                } else {
                    isEscapePressRegistered = false;
                }
            } catch (InvalidInputException invalidInputException) {
                logger.warn("Invalid input:", invalidInputException.getMessage(), new ThreadState(Thread.currentThread()));
            }
        } else {
            if (EngineCore.getGraphicsEngine().getGameWindow().isMouseLocked()) {
                EngineCore.getGraphicsEngine().getGameWindow().setMouseLocked(false);
            }
        }
    }

    //=================== Quit methods ===================

    private void checkIfShouldQuit() {
        try {
            if(EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_0)) {
                logger.attempt("Requesting quit of program (PRESSED_0)...", "", new ThreadState(Thread.currentThread()));
                EngineCore.requestQuit(QuitReason.PRESSED_0);
            }
        } catch (InvalidInputException invalidInputException) {
            logger.warn("Invalid input:", invalidInputException.getMessage(), new ThreadState(Thread.currentThread()));
        }
    }

    private void checkIfShouldForceCrash() {
        try {
            if(EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_0) & EngineCore.getKeyManager().isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                logger.attempt("Crashing program (PRESSED_0_AND_L_SHIFT)...", "", new ThreadState(Thread.currentThread()));
                throw new SimulatedException("Process was forcefully terminated", "Process was forcefully terminated (L-SHIFT and 0 pressed simultaneously)", "Expression ( KeyManager.isKeyDown(GLFW.GLFW_KEY_0) & KeyManager.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) ) evaluated TRUE", new ThreadState(Thread.currentThread()));
            }
        } catch (InvalidInputException invalidInputException) {
            logger.warn("Invalid input:", invalidInputException.getMessage(), new ThreadState(Thread.currentThread()));
        }
    }

    public void terminate() { // <! TO BE CALLED BY GAME_ENGINE ONLY !> called when requested to quit

        logger.attempt("Disposing of objects...", "", new ThreadState(Thread.currentThread()));

        // dispose of objects
        closeWorld();

        logger.attempt("Saving state...", "", new ThreadState(Thread.currentThread()));
        //save
        //TODO: save game

        logger.success("Core terminated!", "", new ThreadState(Thread.currentThread()));

    }

    public void emergencySave() { // <! TO BE CALLED BY GAME_ENGINE ONLY !> called when forceQuit() is called
        logger.attempt("Performing emergency save...", "", new ThreadState(Thread.currentThread()));
        closeWorld();
        //try to save data
        //TODO: emergency save game

    }
}
