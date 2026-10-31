package thejourney.world;

public record Portal(CollisionBox trigger, String targetMap, double spawnX, double spawnY) {

    public Portal {
        if (trigger == null) {
            throw new IllegalArgumentException("trigger must not be null");
        }
        if (targetMap == null || targetMap.isBlank()) {
            throw new IllegalArgumentException("target map must not be blank");
        }
        if (!Double.isFinite(spawnX) || !Double.isFinite(spawnY)) {
            throw new IllegalArgumentException("portal spawn must be finite");
        }
    }
}
