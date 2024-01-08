package game.world.scenes.scenes.planet.terrain;

import java.util.Objects;

public class ChunkPos {

    private int x, z;

    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public void set(int x, int y) {
        this.x = x;
        this.z = y;
    }

    public static ChunkPos add(ChunkPos vector1, ChunkPos vector2) {
        return new ChunkPos(vector1.getX() + vector2.getX(), vector1.getZ() + vector2.getZ());
    }

    public static ChunkPos subtract(ChunkPos vector1, ChunkPos vector2) {
        return new ChunkPos(vector1.getX() - vector2.getX(), vector1.getZ() - vector2.getZ());
    }

    public static ChunkPos multiply(ChunkPos vector1, ChunkPos vector2) {
        return new ChunkPos(vector1.getX() * vector2.getX(), vector1.getZ() * vector2.getZ());
    }

    public static ChunkPos divide(ChunkPos vector1, ChunkPos vector2) {
        return new ChunkPos(vector1.getX() / vector2.getX(), vector1.getZ() / vector2.getZ());
    }

    public static float length(ChunkPos vector) {
        return (float) Math.sqrt(vector.getX() * vector.getX() + vector.getZ() * vector.getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPos chunkPos = (ChunkPos) o;
        return x == chunkPos.x && z == chunkPos.z;
    }

    @Override
    public String toString() {
        return "ChunkPos{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
