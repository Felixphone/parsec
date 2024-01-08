package game.world.player;


import engine.engine.EngineCore;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;
import game.dataItems.constants.PlayerConstants;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import engine.physics.objects.boundings.Hitbox;

public class Player {

    private final Logger logger;

    private final Camera camera;
    private final Hitbox hitbox;
    private final Vector3f cameraPositionOffset = PlayerConstants.BASE_CAMERA_POSITION_OFFSET;
    private final Vector3f cameraRotationOffset = PlayerConstants.BASE_CAMERA_ROTATION_OFFSET;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f size;
    private Vector2f previousMousePos = new Vector2f(0.0f, 0.0f);
    private float movementSpeed = PlayerConstants.BASE_MOVEMENT_SPEED;

    public Player(Vector3f position, Vector3f rotation, Vector3f size) {
        this.position = position;
        this.rotation = rotation;
        logger = EngineCore.getLogger();

        logger.attempt("Initialising camera...", "", new ThreadState(Thread.currentThread()));
        camera = new Camera(Vector3f.add(position, cameraPositionOffset), Vector3f.add(rotation, cameraRotationOffset));

        logger.attempt("Initialising hitbox...", "", new ThreadState(Thread.currentThread()));
        hitbox = new Hitbox(position, rotation, size);
        logger.success("Hitbox initialised!", "", new ThreadState(Thread.currentThread()));

        logger.success("Player successfully initialised!", "", new ThreadState(Thread.currentThread()));
    }

    public void update() {

    }

    //============= getters and setters =============

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        camera.setPosition(Vector3f.add(position, cameraPositionOffset));
    }

    public Vector3f getCameraPositionOffset() {
        return cameraPositionOffset;
    }

    public Vector3f getCameraRotationOffset() {
        return cameraRotationOffset;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public Vector2f getPreviousMousePos() {
        return previousMousePos;
    }

    public void setPreviousMousePos(Vector2f previousMousePos) {
        this.previousMousePos = previousMousePos;
    }

    public void move(Vector3f movementVector) {
        position = Vector3f.add(position, movementVector);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        camera.setRotation(Vector3f.add(rotation, cameraRotationOffset));
    }

    public void rotate(Vector3f rotationVector) {
        this.rotation = Vector3f.add(rotation, rotationVector);
        camera.setRotation(Vector3f.add(rotation, cameraRotationOffset));
    }

    public Vector3f getSize() {
        return size;
    }

    public void setSize(Vector3f size) {
        this.size = size;
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public void resetMovementSpeed() {
        movementSpeed = PlayerConstants.BASE_MOVEMENT_SPEED;
    }

    public Camera getCamera() {
        return camera;
    }

    public void destroy() {
        camera.destroy();
        hitbox.destroy();
    }
}
