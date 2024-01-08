package game.dataItems.constants;

import engine.maths.Vector3f;

public class PlayerConstants {

    public static final Vector3f BASE_CAMERA_POSITION_OFFSET = new Vector3f(0.0f, 4.0f, 0.0f);
    public static final Vector3f BASE_CAMERA_ROTATION_OFFSET = new Vector3f(0.0f, 0.0f, 0.0f);
    public static final float BASE_MOVEMENT_SPEED = 0.2f;
    public static final float CTRL_MOVEMENT_SPEED_INCREASE = 0.05f;
    public static final float BASE_ROTATE_SPEED = 0.1f;

    public static final float MAX_POS_VALUE = 100000f; // past ~60,000 rendering gets glitchy and at 3.4028234E8f it is realy glitchy and terrain no longer loads propperly
    public static final float MIN_POS_VALUE = 0f;
    public static final float MAX_Y_VALUE = 100000f;
    public static final float MIN_Y_VALUE = -100000f;

    public static final Vector3f STARTING_POS = new Vector3f(1000.1f, 100f, 1000.1f);  // .1 to ensure player does not start at the intersection between chunks, resulting in NaN being returned as terrain height
    public static final Vector3f STARTING_ROTATION = new Vector3f(0.5f, 0.0f, 0.5f);
    public static final Vector3f STARTING_SIZE = new Vector3f(0.5f, 1.0f, 0.5f);

    public static final int SIMULATION_DISTANCE = 20; //crashes if over 5500 chunks are loaded idk why

}
