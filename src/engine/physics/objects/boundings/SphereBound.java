package engine.physics.objects.boundings;

import engine.maths.Vector3f;

public class SphereBound extends Bound{
    public SphereBound(Vector3f position, Vector3f rotation, Vector3f size) {
        super(position, rotation, size);
    }

    @Override
    public boolean inBounds(Vector3f queryVector) {
        if (Math.sqrt((queryVector.getX()) * queryVector.getX()) + (queryVector.getY() * queryVector.getY()) + (queryVector.getZ() * queryVector.getZ()) > super.getSize().getX()) {
            return true;
        }

        return false;
    }
}
