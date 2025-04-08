package engine.graphics;

import engine.dataItems.constants.GameWindowConstants;
import engine.dataItems.exceptions.FailedToInitGLFWException;
import engine.dataItems.exceptions.FailedToInitWindowException;
import engine.engine.EngineCore;
import engine.graphics.objects.ColourVector;
import engine.inputs.WindowEventManager;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import tracerUtils.exitReport.QuitReason;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class GameWindow {

    private final Logger logger;
    
    private int width;
    private int height;
    private String title;
    private int XPos, YPos;


    private long window;

    private ColourVector backgroundColour = GameWindowConstants.INITIAL_BACKGROUND_COLOUR;
    private boolean isFullscreen = GameWindowConstants.SHOULD_START_FULLSCREEN;
    private boolean isMoseLocked = GameWindowConstants.SHOULD_START_MOUSE_LOCKED;
    private int preFullscreenXPos;
    private int preFullscreenYPos;
    private int preFullscreenWidth;
    private int preFullscreenHeight;

    //constructor
    public GameWindow(int width, int height, String title) {
        logger = EngineCore.getLogger();
        
        this.width = width;
        this.height = height;
        this.title = title;

        initWindow();

    }

    //set up window (to be called by constructor ONLY)
    private void initWindow() {

        logger.attempt("Initialising window...", "", new ThreadState(Thread.currentThread()));

        if (GLFW.glfwInit()) {
            logger.success("Initialised GLFW!", "", new ThreadState(Thread.currentThread()));
        } else {
            throw new FailedToInitGLFWException("GFLW was not initialised successfully!", "GFLW was not initialised successfully!", "Expression ( GLFW.glfwInit() ) evaluated FALSE", new ThreadState(Thread.currentThread()));
        }


        window = GLFW.glfwCreateWindow(width, height, title, isFullscreen ? GLFW.glfwGetPrimaryMonitor() : 0, 0);

        if (window == 0) {
                throw new FailedToInitWindowException("Window was not created successfully!", "Window was not created successfully! Window is 0.", "Expression ( window == 0 ) evaluated TRUE", new ThreadState(Thread.currentThread()));
        }

        setMouseLocked(isMoseLocked);

        logger.success("Window was successfully initialised!", "", new ThreadState(Thread.currentThread()));

        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        assert videoMode != null;
        XPos = ((videoMode.width() - width) / 2);
        YPos = ((videoMode.height() - height) / 2);
        preFullscreenXPos = ((videoMode.width() - width) / 2);
        preFullscreenYPos = ((videoMode.width() - height) / 2);

        GLFW.glfwSetWindowPos(window, XPos , YPos);
        GLFW.glfwMakeContextCurrent(window);

        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        registerInputCallbacks();

        GLFW.glfwShowWindow(window);
        GLFW.glfwSwapInterval(1);

    }

    private void registerInputCallbacks() {
        logger.attempt("Registering GLFW callbacks...", "", new ThreadState(Thread.currentThread()));
        GLFW.glfwSetKeyCallback(window, EngineCore.getKeyManager().getKeyCallback());
        GLFW.glfwSetCursorPosCallback(window, EngineCore.getMouseManager().getCursorPosCallback());
        GLFW.glfwSetMouseButtonCallback(window, EngineCore.getMouseManager().getMouseButtonCallback());
        GLFW.glfwSetScrollCallback(window, EngineCore.getMouseManager().getScrollWheelCallback());
        GLFW.glfwSetWindowSizeCallback(window, WindowEventManager.getWindowResizeCallback());
        GLFW.glfwSetWindowPosCallback(window, WindowEventManager.getWindowPosCallback());
        logger.success("Successfully registered GLFW callbacks!", "", new ThreadState(Thread.currentThread()));
    }

    public long getWindow() {
        return window;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    //============= UPDATE AND RENDER ==============


    public void update() {

        GL11.glClearColor(backgroundColour.getRed(), backgroundColour.getGreen(), backgroundColour.getBlue(), backgroundColour.getAlpha());
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GLFW.glfwPollEvents();

        if (EngineCore.getGameCore().getWorld() != null) {

            GLFW.glfwSetWindowTitle(window, "X: " + EngineCore.getGameCore().getWorld().getPlayer().getPosition().getX() +
                    " | Y: " + EngineCore.getGameCore().getWorld().getPlayer().getCamera().getPosition().getY() +
                    " | Z: " + EngineCore.getGameCore().getWorld().getPlayer().getCamera().getPosition().getZ() +
                    " || RotationX: " + EngineCore.getGameCore().getWorld().getPlayer().getCamera().getRotation().getX() +
                    " | RotationY: " + EngineCore.getGameCore().getWorld().getPlayer().getCamera().getRotation().getY() +
                    " | RotationZ: " + EngineCore.getGameCore().getWorld().getPlayer().getCamera().getRotation().getZ() +
                    "  |  Seed: " + EngineCore.getGameCore().getWorld().getSeed() +
                    "  |  FPS: " + EngineCore.getCurrentFPS() +
                    "  |  UPS: " + EngineCore.getCurrentUPS());

        } else {
            GLFW.glfwSetWindowTitle(window, "MouseX: " + EngineCore.getMouseManager().getMousePos().getX() +
                    " | MouseY: " + EngineCore.getMouseManager().getMousePos().getY());
        }

        if (shouldClose()) {

            logger.attempt("Requesting quit of program (WINDOW_CLOSURE)...", "", new ThreadState(Thread.currentThread()));
            EngineCore.requestQuit(QuitReason.WINDOW_CLOSURE);
        }
    }

    public void render() {

        GLFW.glfwSwapBuffers(window);
    }

    //============== Getters and Setters ==============


    public void setBackgroundColour(ColourVector color) {
        backgroundColour = color;
    }

    public ColourVector getBackgroundColour() {
        return backgroundColour;
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        isFullscreen = fullscreen;

        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (isFullscreen) {

            preFullscreenWidth = width;
            preFullscreenHeight = height;
            preFullscreenXPos = XPos;
            preFullscreenYPos = YPos;

            assert videoMode != null;
            width = videoMode.width();
            height = videoMode.height();
            XPos = preFullscreenXPos;
            YPos = preFullscreenYPos;

            GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), XPos, YPos, width, height, 0); //enter fullscreen

        } else {

            width = preFullscreenWidth;
            height = preFullscreenHeight;
            XPos = preFullscreenXPos;
            YPos = preFullscreenYPos;

            GLFW.glfwSetWindowMonitor(window, 0, XPos, YPos, width, height, 0); //exit fullscreen

        }
    }

    public void setMouseLocked(boolean mouseLocked) {

        if (mouseLocked) {
            logger.attempt("Locking mouse...", "", new ThreadState(Thread.currentThread()));
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        } else {
            logger.attempt("Unlocking mouse...", "", new ThreadState(Thread.currentThread()));
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }

        isMoseLocked = mouseLocked;

    }

    public boolean isMouseLocked() {
        return isMoseLocked;
    }

    //======== update variable methods <! TO BE CALLED BY WINDOW_EVENT_MANAGER ONLY! !> ======

    public void updateSize(int width, int height) {
        this.width = width;
        this.height = height;

        GL11.glViewport(0, 0, this.width, this.height); //update the veiwport size
        EngineCore.getGraphicsEngine().updateProjectionMatrixAspect(width, height); // updates aspect ratio for projection matrix so game does not gt stretched when window resized
    }


    public void updatePos(int XPos, int YPos) {
        this.XPos = XPos;
        this.YPos = YPos;

    }


    //============== closure and destroy methods =============

    private boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void destroy() {
        logger.attempt("Destroying window...", "", new ThreadState(Thread.currentThread()));
        GLFW.glfwDestroyWindow(window);
        logger.success("Window destroyed!", "", new ThreadState(Thread.currentThread()));
        logger.success("gameWindow destroyed!", "", new ThreadState(Thread.currentThread()));
    }

}
