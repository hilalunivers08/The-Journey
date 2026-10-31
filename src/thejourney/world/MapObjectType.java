package thejourney.world;

public enum MapObjectType {
    TREE(32, 64, 7, 46, 18, 18),
    ROCK(32, 32, 5, 15, 22, 17),
    SIGN(32, 32, 10, 20, 12, 12),
    CHEST(32, 32, 6, 14, 20, 18),
    HOUSE(96, 96, 4, 56, 88, 40),
    WELL(48, 48, 6, 20, 36, 28);

    private final int width;
    private final int height;
    private final int collisionX;
    private final int collisionY;
    private final int collisionWidth;
    private final int collisionHeight;

    MapObjectType(
            int width,
            int height,
            int collisionX,
            int collisionY,
            int collisionWidth,
            int collisionHeight
    ) {
        this.width = width;
        this.height = height;
        this.collisionX = collisionX;
        this.collisionY = collisionY;
        this.collisionWidth = collisionWidth;
        this.collisionHeight = collisionHeight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCollisionX() {
        return collisionX;
    }

    public int getCollisionY() {
        return collisionY;
    }

    public int getCollisionWidth() {
        return collisionWidth;
    }

    public int getCollisionHeight() {
        return collisionHeight;
    }
}
