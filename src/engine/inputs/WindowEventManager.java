package engine.inputs;

import engine.engine.EngineCore;
import engine.graphics.GameWindow;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

public class WindowEventManager {

    private final Logger logger;

    private final GameWindow gameWindow;
    private static GLFWWindowSizeCallback windowResizeCallback;
    private static GLFWWindowPosCallback windowPosCallback;

    public WindowEventManager(GameWindow window) {
        this.gameWindow = window;
        logger = EngineCore.getLogger();

        windowResizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {

                gameWindow.updateSize(width, height);
                logger.success("Updated window size!", "", new ThreadState(Thread.currentThread()));
            }
        };

        windowPosCallback = new GLFWWindowPosCallback() {
            @Override
            public void invoke(long window, int xpos, int ypos) {

                gameWindow.updatePos(xpos, ypos);
                logger.success("Updated window position!", "", new ThreadState(Thread.currentThread()));
            }
        };

        logger.success("WindowEventManager successfully initialised!", "", new ThreadState(Thread.currentThread()));

    }

    public static GLFWWindowSizeCallback getWindowResizeCallback() {
        return windowResizeCallback;
    }

    public static GLFWWindowPosCallback getWindowPosCallback() {
        return windowPosCallback;
    }

    //=========================

    public void destroy() {
        logger.attempt("Freeing windowEvent callbacks...", "", new ThreadState(Thread.currentThread()));
        windowResizeCallback.free();
        windowPosCallback.free();
        logger.success("Input callbacks freed!", "", new ThreadState(Thread.currentThread()));
        logger.success("windowEventManager destroyed!", "", new ThreadState(Thread.currentThread()));

    }
}
