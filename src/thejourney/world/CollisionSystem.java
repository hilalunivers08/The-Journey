package thejourney.world;

import thejourney.Constants;
import thejourney.entity.Entity;

public final class CollisionSystem {

    private static final double TILE_EPSILON = 0.001;

    public void move(Entity entity, double deltaX, double deltaY, TileMap map) {
        if (entity == null || map == null) {
            throw new IllegalArgumentException("entity and map must not be null");
        }

        moveAlongAxis(entity, deltaX, 0, map);
        moveAlongAxis(entity, 0, deltaY, map);
    }

    private void moveAlongAxis(Entity entity, double deltaX, double deltaY, TileMap map) {
        double delta = deltaX != 0 ? deltaX : deltaY;

        if (delta == 0 || !Double.isFinite(delta)) {
            return;
        }

        int steps = Math.max(1, (int) Math.ceil(Math.abs(delta)));
        double stepX = deltaX / steps;
        double stepY = deltaY / steps;

        for (int step = 0; step < steps; step++) {
            entity.setPosition(entity.getX() + stepX, entity.getY() + stepY);

            if (collides(entity, map)) {
                entity.setPosition(entity.getX() - stepX, entity.getY() - stepY);
                return;
            }
        }
    }

    private boolean collides(Entity entity, TileMap map) {
        CollisionBox box = entity.getCollisionBox();

        if (box.x() < 0
                || box.y() < 0
                || box.right() > map.getPixelWidth()
                || box.bottom() > map.getPixelHeight()) {
            return true;
        }

        int firstColumn = (int) Math.floor(box.x() / Constants.TILE_SIZE);
        int lastColumn = (int) Math.floor(
                (box.right() - TILE_EPSILON) / Constants.TILE_SIZE
        );
        int firstRow = (int) Math.floor(box.y() / Constants.TILE_SIZE);
        int lastRow = (int) Math.floor((box.bottom() - TILE_EPSILON) / Constants.TILE_SIZE);

        for (int tileY = firstRow; tileY <= lastRow; tileY++) {
            for (int tileX = firstColumn; tileX <= lastColumn; tileX++) {
                if (map.isSolid(tileX, tileY)) {
                    return true;
                }
            }
        }

        for (MapObject object : map.getObjects()) {
            if (box.intersects(object.getCollisionBox())) {
                return true;
            }
        }

        return false;
    }
}
