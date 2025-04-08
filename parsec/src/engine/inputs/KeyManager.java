package engine.inputs;

import engine.dataItems.exceptions.InvalidInputException;
import engine.engine.EngineCore;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;

public class KeyManager {

    private final Logger logger;

    private GLFWKeyCallback keyCallback;

    private boolean[] pressedKeys = new boolean[GLFW.GLFW_KEY_LAST];

    public KeyManager() {
        logger = EngineCore.getLogger();

        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                pressedKeys[key] = (action != GLFW.GLFW_RELEASE);
            }
        };

        logger.success("Key manager successfully initialised!", "", new ThreadState(Thread.currentThread()));
    }

    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    //=======================

    public boolean[] getPressedKeys() {
        return pressedKeys;
    }

    public boolean isKeyDown(int key) throws InvalidInputException {
        if (key <= (pressedKeys.length - 1)) {
            return pressedKeys[key];
        } else {
            throw new InvalidInputException("Query for invalid key attempted!", "Query for invalid key (" + key + ") was attempted! Max valid key is " + (pressedKeys.length - 1), "Expression ( key <= (pressedKeys.length - 1) ) evaluated FALSE", new ThreadState(Thread.currentThread()));
        }
    }

    public void destroy() {
        logger.attempt("Freeing key callbacks...", "", new ThreadState(Thread.currentThread()));
        keyCallback.free();
        logger.success("Key callbacks freed!", "", new ThreadState(Thread.currentThread()));
        logger.success("keyManager destroyed!", "", new ThreadState(Thread.currentThread()));

    }
}
