package thejourney.world;

import java.awt.Graphics2D;

public final class MapObject implements Renderable {

    private final MapObjectType type;
    private final double x;
    private final double y;
    private final CollisionBox collisionBox;

    public MapObject(MapObjectType type, double x, double y) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("object position must be finite");
        }

        this.type = type;
        this.x = x;
        this.y = y;
        this.collisionBox = new CollisionBox(
                x + type.getCollisionX(),
                y + type.getCollisionY(),
                type.getCollisionWidth(),
                type.getCollisionHeight()
        );
    }

    public MapObjectType getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return type.getWidth();
    }

    public int getHeight() {
        return type.getHeight();
    }

    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    @Override
    public double getSortY() {
        return y + type.getHeight();
    }

    @Override
    public void render(Graphics2D g2, double alpha) {
        MapObjectRenderer.render(g2, this);
    }
}
