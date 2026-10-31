package thejourney.world;

import thejourney.entity.Facing;

public record SpawnPoint(double x, double y, Facing facing) {

    public SpawnPoint {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("spawn position must be finite");
        }
        if (facing == null) {
            facing = Facing.DOWN;
        }
    }
}
