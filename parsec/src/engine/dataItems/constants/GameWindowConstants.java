package engine.dataItems.constants;

import engine.graphics.objects.ColourVector;

public class GameWindowConstants {

    public static final int INITIAL_WIDTH = 1500;
    public static final int INITIAL_HEIGHT = 800;
    public static final String WINDOW_TITLE = "Space Game";
    public static final ColourVector INITIAL_BACKGROUND_COLOUR = new ColourVector(0.1f, 0.1f, 0.1f, 0.0f);
    public static final boolean SHOULD_START_FULLSCREEN = false; // setting to true breaks it so don't
    public static final boolean SHOULD_START_MOUSE_LOCKED = false;
    public static final boolean SHOULD_LOCK_MOUSE_ON_LEFT_CLICK = true;

    public static final float INITIAL_FOV = 70.0f;
    public static final float NEAR_CLIPPING_PLANE = 0.1f;
    public static final float FAR_CLIPPING_PLANE = 10000.0f;
}
