package engine.graphics;

import engine.dataItems.constants.GameWindowConstants;
import engine.engine.EngineCore;
import engine.graphics.UI.UIManager;
import engine.maths.Matrix4f;
import engine.inputs.WindowEventManager;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;

public class GraphicsEngine {

    private final Logger logger;
    
    private WindowEventManager windowEventManager;
    private GameWindow gameWindow;
    private Renderer renderer;
    private UIManager uiManager;
    private long window;

    private Matrix4f projection;
    private float currentFOV = GameWindowConstants.INITIAL_FOV;

    public GraphicsEngine() {
        logger = EngineCore.getLogger();

        logger.engineAttempt("Initialising game window...", "", new ThreadState(Thread.currentThread()));
        gameWindow = new GameWindow(GameWindowConstants.INITIAL_WIDTH, GameWindowConstants.INITIAL_HEIGHT, GameWindowConstants.WINDOW_TITLE);
        window = gameWindow.getWindow();

        logger.engineAttempt("Initialising windowEventManager...", "", new ThreadState(Thread.currentThread()));
        windowEventManager = new WindowEventManager(gameWindow);

        logger.engineAttempt("Initialising shaders...", "", new ThreadState(Thread.currentThread()));
        ShaderManager.initialiseShaders();

        logger.engineAttempt("Initialising renderer...", "", new ThreadState(Thread.currentThread()));
        renderer = new Renderer();

        logger.engineAttempt("Initialising uiManager...", "", new ThreadState(Thread.currentThread()));
        uiManager = new UIManager();

        projection = Matrix4f.projection(currentFOV, (float) gameWindow.getWidth() / (float) gameWindow.getHeight(), GameWindowConstants.NEAR_CLIPPING_PLANE, GameWindowConstants.FAR_CLIPPING_PLANE);

        logger.success("GraphicsEngine successfully initialised!", "", new ThreadState(Thread.currentThread()));
    }

    public void update() {
        gameWindow.update();
        UIManager.update();
    }

    public void render() {
        UIManager.render(); //needs to be rendered before window to show up
        gameWindow.render();
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public UIManager getUiManager() {
        return uiManager;
    }

    public WindowEventManager getWindowEventManager() {
        return windowEventManager;
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }

    public void updateProjectionMatrixAspect(int width, int height) {
        projection = Matrix4f.projection(GameWindowConstants.INITIAL_FOV, (float) width / (float) height, GameWindowConstants.NEAR_CLIPPING_PLANE, GameWindowConstants.FAR_CLIPPING_PLANE);
    }

    public void updateFOV(float FOV) {
        currentFOV = FOV;
        projection = Matrix4f.projection(FOV, (float) gameWindow.getWidth() / (float) gameWindow.getHeight(), GameWindowConstants.NEAR_CLIPPING_PLANE, GameWindowConstants.FAR_CLIPPING_PLANE);
    }

    public float getFOV() {
        return currentFOV;
    }

    public void resetFOV() {
        projection = Matrix4f.projection(GameWindowConstants.INITIAL_FOV, (float) gameWindow.getWidth() / (float) gameWindow.getHeight(), GameWindowConstants.NEAR_CLIPPING_PLANE, GameWindowConstants.FAR_CLIPPING_PLANE);
    }

    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    //============= destroy methods ============

    public void destroy() {

        logger.engineAttempt("Destroying renderer...", "", new ThreadState(Thread.currentThread()));
        if (renderer != null) {
            renderer.destroy();
        } else {
            logger.warn("Unable to destroy renderer", "as it has not yet been initialised!", new ThreadState(Thread.currentThread()));
        }

        logger.engineAttempt("Destroying windowEventManager...", "", new ThreadState(Thread.currentThread()));
        if (windowEventManager != null) {
            windowEventManager.destroy();
        } else {
            logger.warn("Unable to destroy windowEventManager", "as it has not yet been initialised!", new ThreadState(Thread.currentThread()));
        }

        logger.engineAttempt("Destroying gameWindow...", "", new ThreadState(Thread.currentThread()));
        if (gameWindow != null) {
            gameWindow.destroy();
        } else {
            logger.warn("Unable to destroy gameWindow", "as it has not yet been initialised!", new ThreadState(Thread.currentThread()));
        }

        logger.success("graphicsEngine destroyed!", "", new ThreadState(Thread.currentThread()));
    }
}
