package engine.engine;

import engine.dataItems.constants.GameLoopConstants;
import engine.engineFlags.DebugFlagType;
import engine.engineFlags.FlagManager;
import engine.engineFlags.FlagType;
import engine.graphics.GraphicsEngine;
import engine.inputs.KeyManager;
import engine.inputs.MouseManager;
import engine.system.SystemInfo;
import engine.system.SystemManager;
import engine.system.dataItems.enums.SystemType;
import engine.system.dataItems.exeptions.UnsupportedPlatformException;
import game.GameCore;
import launcher.launchConfig.LaunchConfig;
import launcher.watchdog.EngineWatchdog;
import tracerUtils.data.ExceptionContext;
import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.CrashReport;
import tracerUtils.exitReport.ExitReport;
import tracerUtils.exitReport.QuitReason;
import tracerUtils.logger.Logger;
import tracerUtils.traceableException.FatalTraceableException;
import tracerUtils.traceableException.TraceableException;
import tracerUtils.traceableException.defaultExceptions.fatal.OutOfMemoryException;
import tracerUtils.traceableException.defaultExceptions.fatal.UncaughtException;
import tracerUtils.traceableException.defaultExceptions.fatal.UntraceableException;


public class EngineCore  {

    /**
     * The current version of <b>PHOENIX_EL</b>
     */
    public static final String PHOENIX3D_EL_VERSION = "PHOENIX_3D_EL_v1.0.0a";
    /**
     * The current state that the engine core is in. Used for debugging.
     */
    private static String currentState = "PRE-INIT";
    /**
     * The logger which the engine should use.<br>
     * <br>
     * For now, it is the same logger as the {@link EngineWatchdog}'s, but may become separate later in development.
     */
    private static Logger logger;

    private static int currentFPS = 0;
    private static int currentUPS = 0;
    private static SystemInfo systemInfo;
    private static SystemType[] supportedSystems = {SystemType.WINDOWS, SystemType.MACOS};

    private static FlagManager flagManager;

    /**
     * Stores the reason why the program was quit when {@link #requestQuit(QuitReason)} is called. <br>
     * <b>NOT TO BE SET BY ANY OTHER METHOD!</b>
     */
    private static QuitReason quitReason;

    private static GraphicsEngine graphicsEngine;
    private static MouseManager mouseManager;
    private static KeyManager keyManager;
    private static GameCore gameCore;


    public EngineCore(LaunchConfig config, Logger logger) {
        EngineCore.logger = logger;
        try {
            EngineCore.currentState = "SYSTEM-SUPPORT-VERIFICATION";
            systemInfo = SystemManager.getSystemInfo();

            for (SystemType type : supportedSystems) {
                if (systemInfo.getSystemType() == type) {
                    logger.success("Successfully initialised gameEngine!", "", new ThreadState(Thread.currentThread()));
                    EngineCore.currentState = "AWAITING-RUN";
                    return;
                }
            }

            String supportedSystemsStr = "";
            for (SystemType type : supportedSystems) {
                supportedSystemsStr += "[" + type.name() + "] ";
            }

            throw new UnsupportedPlatformException("Unsupported platform!", "Unsupported platform! This version of Phoenix3D (EL) does not support " + systemInfo.getSystemType().name() + ". To use this program, install it on one of the following platforms: " + supportedSystemsStr, "Result of systemInfo.getSystemType() could not be found in supportedSystems", new ThreadState(Thread.currentThread()));
        } catch (UnsupportedPlatformException e) {
            forceQuit(e);
        }
    }

    /**
     * Initialises all resources and classes: <br>
     * - {@link MouseManager} <br>
     * - {@link KeyManager} <br>
     * - {@link GraphicsEngine} <br>
     * - {@link GameCore} <br>
     * <br>
     * Then starts game loop by calling {@link #startGameLoop()}
     */
    public void start() { //init game

        EngineCore.currentState = "ENGINE-INITIALISATION";
        flagManager = new FlagManager();

        currentState = "RUN-START";
        try {
            //initialising game
            EngineCore.currentState = "INITIALISING-GAME";
            logger.engineAttempt("Initialising game...", "", new ThreadState(Thread.currentThread()));

            EngineCore.currentState = "INITIALISING-MOUSE-MANAGER";
            logger.engineAttempt("Initialising mouseManager...", "", new ThreadState(Thread.currentThread()));
            mouseManager = new MouseManager(); // <MUST BE INITIALISED PRIOR TO GAME_WINDOW AND GAME_CORE>

            EngineCore.currentState = "INITIALISING-KEY-MANAGER";
            logger.engineAttempt("Initialising keyManager...", "", new ThreadState(Thread.currentThread()));
            keyManager = new KeyManager(); // <MUST BE INITIALISED PRIOR TO GAME_WINDOW AND GAME_CORE>

            EngineCore.currentState = "INITIALISING-GRAPHICS-ENGINE";
            logger.engineAttempt("Initialising graphicsEngine...", "", new ThreadState(Thread.currentThread()));
            graphicsEngine = new GraphicsEngine();

            EngineCore.currentState = "INITIALISING-GAME-CORE";
            logger.engineAttempt("Initialising game core...", "", new ThreadState(Thread.currentThread()));
            gameCore = new GameCore();

            try {
                try {
                    EngineCore.currentState = "STARTING-GAME-LOOP";
                    startGameLoop();
                    EngineWatchdog.getLogger().engineSuccess("Exited game loop cleanly!", "", new ThreadState(Thread.currentThread()));
                }
                catch (OutOfMemoryError e) {
                    throw new OutOfMemoryException("Out of memory!", "Out of memory! Could not allocate space: " + e.getMessage(), "Exception OutOfMemoryError thrown", e, new ThreadState(Thread.currentThread()));
                }
                catch (Throwable e) {
                    if (! (e instanceof TraceableException || e instanceof FatalTraceableException)) {
                        throw new TraceableException(new UntraceableException(e));
                    } else {
                        throw e;
                    }
                }
            } catch (TraceableException e) {
                throw new UncaughtException("Uncaught exception!", "Uncaught exception! A non-fatal exception was not caught and propagated up to the engine core", "Non-fatal core exception was not caught", e, new ThreadState(Thread.currentThread()));
            }
        }
        catch (FatalTraceableException e) {
            logger.error("Fatal core exception encountered by EngineCore.", "Forcefully quitting program...", new ThreadState(Thread.currentThread()));
            forceQuit(e);
        }
    }

    /**
     * Runs an infinite loop until the SHOULD_QUIT flag {@link #flagManager} is <b>true</b>. <br>
     * <br>
     * Each loop, the code checks delta time to ensure the time between updates or frames is as constant as possible. <br>
     * <br>
     * If <b>deltaUpdateTime</b> is greater or equal to the <b>timePerUpdate</b> <i>(derived by converting constant <b>{@link GameLoopConstants}.UPS</b> to milliseconds)</i> the {@link #update()} function is called. <br>
     * <br>
     * If <b>deltaFrameTime</b> is greater or equal to the <b>timePerFrame</b> <i>(derived by converting constant <b>{@link GameLoopConstants}.FPS</b> to milliseconds)</i> the {@link #render()} function is called. <br>
     * <br>
     * When SHOULD_QUIT becomes <b>true</b>, the game loop will exit and call the {@link #quit()} method.
     */
    private void startGameLoop() { //initialises loop and calls update() methods of both GameCore and GameWindow respectively every update, and render() method of GameWindow (not synced with updates)

        logger.engineSuccess("Successfully initialised game!", "", new ThreadState(Thread.currentThread()));

        //Debugger debugger = new Debugger();
        double timePerFrame = 1000000000.0 / GameLoopConstants.FPS;
        double timePerUpdate = 1000000000.0 / GameLoopConstants.UPS;

        long previousTime = System.nanoTime();
        long lastCheck = System.currentTimeMillis();

        int updates = 0;
        int frames = 0;

        double deltaUpdateTime = 0;
        double deltaFrameTime = 0;


        while (!flagManager.getFlag(FlagType.SHOULD_QUIT).isUp()) {
            try {

                if (!flagManager.getDebugFlag(DebugFlagType.DEBUG_PAUSE_ALL).isUp()) {

                    EngineCore.currentState = "RUNNING";
                    long timeNow = System.nanoTime();

                    deltaUpdateTime += (timeNow - previousTime) / timePerUpdate;
                    deltaFrameTime += (timeNow - previousTime) / timePerFrame;
                    previousTime = timeNow;

                    if (deltaUpdateTime >= 1) {
                        EngineCore.currentState = "RUNNING,UPDATE-TASK";
                        update();

                        updates++;
                        deltaUpdateTime--;
                    }

                    if (deltaFrameTime >= 1) {
                        EngineCore.currentState = "RUNNING,RENDER-TASK";
                        render();

                        frames++;
                        deltaFrameTime--;
                    }

                    if (System.currentTimeMillis() - lastCheck >= 1000) {
                        lastCheck = System.currentTimeMillis();
                        currentFPS = frames;
                        currentUPS = updates;
                        frames = 0;
                        updates = 0;

                    }

                    //System.out.println("UPS: " + currentUPS + " | FPS: " + currentFPS);
                }
            } catch (TraceableException e) {
                e.contextualiseAndRethrow(new ExceptionContext("Attempted to do game loop", new ThreadState(Thread.currentThread())));
            }
        }

        quit();
    }

    /**
     * Called by game loop inside {@link #startGameLoop()} <br>
     * <b>NOT TO BE CALLED BY ANY OTHER METHOD!</b> <br>
     * <br>
     * Calls <b>update()</b> function in {@link GameCore}. <br>
     * <br>
     * Then calls <b>update()</b> function in {@link GraphicsEngine}.
     */
    private static void update() { //called by startGameLoop()  <DO NOT CALL FROM ELSEWHERE>
        try {
            gameCore.update();
            graphicsEngine.update();
        } catch (TraceableException e) {
            e.contextualiseAndRethrow(new ExceptionContext("Attempted to run update task", new ThreadState(Thread.currentThread())));
        }
    }

    /**
     * Called by game loop inside {@link #startGameLoop()} <br>
     * <b>NOT TO BE CALLED BY ANY OTHER METHOD!</b> <br>
     * <br>
     * Calls <b>render()</b> function in {@link GameCore}. <br>
     * <br>
     * Then calls <b>render()</b> function in {@link GraphicsEngine}.
     */
    private static void render() { //called by startGameLoop()  <DO NOT CALL FROM ELSEWHERE>
        try {
            gameCore.render();
            graphicsEngine.render();
        } catch (TraceableException e) {
            e.contextualiseAndRethrow(new ExceptionContext("Attempted to run render task", new ThreadState(Thread.currentThread())));
        }
    }

    //############# GETTERS AND SETTERS: ################################

    public static Logger getLogger() {
        return logger;
    }

    public static int getCurrentFPS() {
        return currentFPS;
    }

    public static int getCurrentUPS() {
        return currentUPS;
    }

    public static String getStateNow() {
        return new String(EngineCore.currentState);
    }

    public static FlagManager getFlagManager() {
        return flagManager;
    }

    public static SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public static GraphicsEngine getGraphicsEngine() {
        return graphicsEngine;
    }

    public static MouseManager getMouseManager() {
        return mouseManager;
    }

    public static KeyManager getKeyManager() {
        return keyManager;
    }

    public static GameCore getGameCore() {
        return gameCore;
    }

    //############## QUITING METHODS: ################################

    /**
     * Sets variable #####  to <b>true</b>, causing the game loop to stop looping. <br>
     * When the game loop exits it then calls the {@link #quit()} method to destroy objects and save the game. <br>
     * <br>
     *
     * @param quitReason The reason why the game should be quit <i>(It is saved in the variable {@link #quitReason}, and is printed out and recorded in log when method {@link #quit()} is called by the termination of the game loop)</i>.
     */
    public static void requestQuit(QuitReason quitReason) { //sets shouldQuit to true, stopping the while loop in startGameLoop()
        EngineCore.currentState = "QUIT-REQUESTED";
        flagManager.getFlag(FlagType.SHOULD_QUIT).raise();
        EngineCore.quitReason = quitReason;
    }

    /**
     * Directly calls all <b>destroy()</b> methods and attempts to save the game. <br>
     * <b>SHOULD ONLY BE CALLED IN THE EVENT OF A FATAL ERROR!</b> <br>
     * <br>
     * The program is then forcefully terminated without exiting the game loop with <b>System.exit(1)</b>. <br>
     * <br>
     * Exits the program with a code of <b>1</b> to show that there was an error. <br>
     * <br>
     *
     * @param fatalCoreException The #############################.
     */

    public static void forceQuit(FatalTraceableException fatalCoreException) { //forcefully terminates the program without exiting the game-loop
        int warnings = 0;
        String logPath = null;

        try {
            logger.log(fatalCoreException.toLogEntry(new ThreadState(Thread.currentThread())));

            logger.warn(" ########## ENDING PROCESS: FORCEQUIT WAS CALLED! ##########", "", new ThreadState(Thread.currentThread()));

            logger.engineAttempt("Attempting to save game...", "", new ThreadState(Thread.currentThread()));
            try {
                if (gameCore != null) { //attempt to save game
                    logger.engineAttempt("Destroying gameCore...", "", new ThreadState(Thread.currentThread()));
                    gameCore.emergencySave();
                    gameCore = null;
                } else {
                    warnings ++;
                    logger.warn("Unable to perform emergency save", " as gameCore not yet initialised!", new ThreadState(Thread.currentThread()));
                }
            } catch (Throwable e) {
                warnings ++;
                logger.warn("Unable to perform emergency save:", e.getMessage(), new ThreadState(Thread.currentThread()));
            }

            warnings += destroyObjects();

            logger.engineSuccess("[THREAD/ENGINE] Process finished uncleanly:", fatalCoreException.getQuitReason().name(), new ThreadState(Thread.currentThread()));

        } catch (Throwable e) {
            warnings++;
            e.printStackTrace();
        }

        CrashReport exitReport = new CrashReport(fatalCoreException, PHOENIX3D_EL_VERSION, getStateNow(), warnings);
        EngineWatchdog.submitExitReport(exitReport);
    }

    /**
     * Called when game loop in  {@link #startGameLoop()} stops due to variable #### being <b>true</b>. <br>
     * <b>NOT TO BE CALLED BY ANY OTHER METHOD!</b> <br>
     * <br>
     * Destroys all objects and classes and attempts to save the game. <br>
     * <br>
     * The program is then terminated cleanly using <b>System.exit(0)</b>. <br>
     * <br>
     * Exits the program with a code of <b>0</b> to show that it finished without errors.
     */
    private static void quit() {
        String logPath = null;
        int warnings = 0;

        logger.engineAttempt("############## Quitting game... ################", "", new ThreadState(Thread.currentThread()));

        logger.engineAttempt("Destroying gameCore...", "", new ThreadState(Thread.currentThread()));
        try{
            if (gameCore != null) { //save game
                gameCore.terminate();
                gameCore = null;
            } else {
                logger.warn("Unable to destroy gameCore", "as it has not yet initialised!", new ThreadState(Thread.currentThread()));
                warnings ++;
            }
        } catch (Throwable e) {
            logger.warn("Unable to destroy gameCore:", e.getMessage(), new ThreadState(Thread.currentThread()));
            warnings ++;
        }

        warnings += destroyObjects();

        logger.engineSuccess("[THREAD/ENGINE] Process finished cleanly:", quitReason.name(), new ThreadState(Thread.currentThread()));

        ExitReport exitReport = new ExitReport(quitReason, PHOENIX3D_EL_VERSION, getStateNow(), warnings);
        EngineWatchdog.submitExitReport(exitReport);
    }

    /**
     * Destroys the engine core's objects, ready for the program to be terminated or relaunched.
     * @return {@link Integer} warnings - The number of warnings encountered when attempting to destroy objects.
     */
    private static int destroyObjects() {
        int warnings = 0;
        logger.engineAttempt("Destroying mouseManager...", "", new ThreadState(Thread.currentThread()));
        try {
            if (mouseManager != null) { //free mouseManager
                mouseManager.destroy();
                mouseManager = null;
            } else {
                logger.warn("Unable to destroy mouseManager", "as it has not yet been initialised!", new ThreadState(Thread.currentThread()));
                warnings ++;
            }
        } catch(Throwable e) {
            logger.warn("Unable to destroy mouseManager:", e.getMessage(), new ThreadState(Thread.currentThread()));
            warnings ++;
        }

        logger.engineAttempt("Destroying keyManager...", "", new ThreadState(Thread.currentThread()));
        try {
            if (keyManager != null) { //free keyManager
                keyManager.destroy();
                keyManager = null;
            } else {
                logger.warn("Unable to destroy keyManager", "as it has not yet been initialised!", new ThreadState(Thread.currentThread()));
                warnings ++;
            }
        } catch(Throwable e) {
            logger.warn("Unable to destroy keyManager:", e.getMessage(), new ThreadState(Thread.currentThread()));
            warnings ++;
        }

        logger.engineAttempt("Destroying graphicsEngine...", "", new ThreadState(Thread.currentThread()));
        try {
            if (graphicsEngine != null) { //destroy graphicsEngine
                graphicsEngine.destroy();
                graphicsEngine = null;
            } else {
                logger.warn("Unable to destroy graphicsEngine", "as it has not yet been initialised!", new ThreadState(Thread.currentThread()));
                warnings ++;
            }
        } catch (Throwable e) {
            logger.warn("Unable to destroy graphicsEngine:", e.getMessage(), new ThreadState(Thread.currentThread()));
            warnings ++;
        }
        return warnings;
    }

    public void close() {

    }
}
