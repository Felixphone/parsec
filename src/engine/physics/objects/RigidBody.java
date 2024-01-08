package engine.physics.objects;

import engine.engine.EngineCore;
import engine.maths.Vector3f;
import engine.physics.objects.attributes.Acceleration;
import engine.physics.objects.attributes.Force;
import engine.physics.objects.attributes.Mass;
import engine.physics.objects.attributes.Velocity;
import engine.physics.objects.boundings.Bound;

import java.util.ArrayList;

public abstract class RigidBody {

    private Vector3f position;
    private Vector3f rotation;
    private Vector3f size;
    private ArrayList<Bound> bounds = new ArrayList<>();
    private ArrayList<Force> forces = new ArrayList<>();
    private Velocity velocity = new Velocity(0.0f, 0.0f, 0.0f);
    private Acceleration acceleration = new Acceleration(0.0f, 0.0f, 0.0f);
    private Mass mass = new Mass(1.0f);

    public RigidBody(Vector3f position, Vector3f rotation, Vector3f size, Mass mass) {
        this.position = position;
        this.rotation = rotation;
        this.size = size;
        this.mass = mass;
    }

    public void update() {
        Force resultantForce = new Force(0.0f, 0.0f, 0.0f);

        for (Force force : forces) {
            resultantForce.add(force);
        }

        acceleration.calculate(resultantForce, mass);
        Vector3f velocityVec = velocity.toVector3f();
        Vector3f accelerationVec = acceleration.toVector3f();

        float elapsedTime = 1.0f / EngineCore.getCurrentUPS();

        // distance = initial_velocity x time + 1/2 x acceleration x time^2  |  s = ut + 1/2 at^2

        Vector3f movementVector = new Vector3f(0.0f, 0.0f, 0.0f);

        movementVector.setX((velocityVec.getX() * elapsedTime) + (0.5f * accelerationVec.getX() * (elapsedTime * elapsedTime)));
        movementVector.setY((velocityVec.getY() * elapsedTime) + (0.5f * accelerationVec.getY() * (elapsedTime * elapsedTime)));
        movementVector.setZ((velocityVec.getZ() * elapsedTime) + (0.5f * accelerationVec.getZ() * (elapsedTime * elapsedTime)));

        velocity.setX(velocity.getX() + acceleration.getX() * elapsedTime);
        velocity.setY(velocity.getY() + acceleration.getY() * elapsedTime);
        velocity.setZ(velocity.getZ() + acceleration.getZ() * elapsedTime);

        position = Vector3f.add(position, movementVector);
    }
}
