package thejourney.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import thejourney.Constants;

public final class TileMap {

    private final String name;
    private final int width;
    private final int height;
    private final TileDefinition[][] ground;
    private final TileDefinition[][] terrain;
    private final TileDefinition[][] foreground;
    private final List<MapObject> objects;
    private final List<Portal> portals;
    private final SpawnPoint spawn;

    public TileMap(
            String name,
            int width,
            int height,
            TileDefinition[][] ground,
            TileDefinition[][] terrain,
            TileDefinition[][] foreground,
            List<MapObject> objects,
            List<Portal> portals,
            SpawnPoint spawn
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("map name must not be blank");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("map size must be positive");
        }
        if (spawn == null) {
            throw new IllegalArgumentException("spawn must not be null");
        }

        this.name = name;
        this.width = width;
        this.height = height;
        this.ground = requireLayer(ground, width, height, "ground");
        this.terrain = requireLayer(terrain, width, height, "terrain");
        this.foreground = requireLayer(foreground, width, height, "foreground");
        this.objects = Collections.unmodifiableList(new ArrayList<>(objects));
        this.portals = Collections.unmodifiableList(new ArrayList<>(portals));
        this.spawn = spawn;

        validateSpawn(spawn);
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixelWidth() {
        return width * Constants.TILE_SIZE;
    }

    public int getPixelHeight() {
        return height * Constants.TILE_SIZE;
    }

    public SpawnPoint getSpawn() {
        return spawn;
    }

    public List<MapObject> getObjects() {
        return objects;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public TileDefinition getTile(TileLayer layer, int tileX, int tileY) {
        if (tileX < 0 || tileX >= width || tileY < 0 || tileY >= height) {
            return null;
        }

        return layerArray(layer)[tileY][tileX];
    }

    public boolean isSolid(int tileX, int tileY) {
        if (tileX < 0 || tileX >= width || tileY < 0 || tileY >= height) {
            return true;
        }

        return isSolid(ground[tileY][tileX])
                || isSolid(terrain[tileY][tileX])
                || isSolid(foreground[tileY][tileX]);
    }

    private static boolean isSolid(TileDefinition definition) {
        return definition != null && definition.solid();
    }

    private TileDefinition[][] layerArray(TileLayer layer) {
        if (layer == null) {
            throw new IllegalArgumentException("layer must not be null");
        }

        return switch (layer) {
            case GROUND -> ground;
            case TERRAIN -> terrain;
            case FOREGROUND -> foreground;
        };
    }

    private void validateSpawn(SpawnPoint point) {
        if (point.x() < 0 || point.x() >= getPixelWidth()
                || point.y() < 0 || point.y() >= getPixelHeight()) {
            throw new IllegalArgumentException("spawn outside map bounds");
        }
    }

    private static TileDefinition[][] requireLayer(
            TileDefinition[][] layer,
            int width,
            int height,
            String label
    ) {
        if (layer == null) {
            throw new IllegalArgumentException(label + " layer must not be null");
        }
        if (layer.length != height) {
            throw new IllegalArgumentException(label + " layer height mismatch");
        }

        for (int y = 0; y < height; y++) {
            if (layer[y] == null || layer[y].length != width) {
                throw new IllegalArgumentException(label + " layer width mismatch");
            }
        }

        return layer;
    }
}
