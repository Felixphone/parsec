package engine.inputs;

import engine.dataItems.exceptions.InvalidInputException;
import engine.engine.EngineCore;
import engine.maths.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;

public class MouseManager {

    private final Logger logger;

    private GLFWCursorPosCallback cursorPosCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWScrollCallback scrollWheelCallback;

    private boolean[] pressedMouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private Vector2f mousePos = new Vector2f(0.0f, 0.0f);
    private double scrollXOffset, scrollYOffset;

    public MouseManager() {
        logger = EngineCore.getLogger();

        cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mousePos = new Vector2f( (float) xpos, (float) ypos);
            }
        };

        mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                pressedMouseButtons[button] = (action != GLFW.GLFW_RELEASE);
            }
        };

        scrollWheelCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scrollXOffset = xoffset;
                scrollYOffset = yoffset;
            }
        };

        logger.success("Mouse manager successfully initialised!", "", new ThreadState(Thread.currentThread()));
    }

    public GLFWCursorPosCallback getCursorPosCallback() {
        return cursorPosCallback;
    }

    public GLFWMouseButtonCallback getMouseButtonCallback() {
        return mouseButtonCallback;
    }

    public GLFWScrollCallback getScrollWheelCallback() {
        return scrollWheelCallback;
    }

    //=======================

    public boolean[] getPressedMouseButtons() {
        return pressedMouseButtons;
    }

    public Vector2f getMousePos() {
        return mousePos;
    }

    public double getScrollXOffset() {
        return scrollXOffset;
    }

    public double getScrollYOffset() {
        return scrollYOffset;
    }

    public boolean isMouseButtonPressed(int button) throws InvalidInputException {
        if (button <= (pressedMouseButtons.length - 1)) {
            return pressedMouseButtons[button];
        } else {
            throw new InvalidInputException("Query for invalid mouse button attempted!", "Query for invalid mouse button (" + button + ") was attempted! Max valid mouse button is " + (pressedMouseButtons.length - 1), "Expression ( button <= (pressedMouseButtons.length - 1) ) evaluated FALSE", new ThreadState(Thread.currentThread()));
        }
    }

    public void destroy() {
        logger.attempt("Freeing input callbacks...", "", new ThreadState(Thread.currentThread()));
        cursorPosCallback.free();
        mouseButtonCallback.free();
        scrollWheelCallback.free();
        logger.success("Input callbacks freed!", "", new ThreadState(Thread.currentThread()));
        logger.success("inputManager destroyed!", "", new ThreadState(Thread.currentThread()));

    }
}
