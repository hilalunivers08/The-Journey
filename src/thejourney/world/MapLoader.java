package thejourney.world;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import thejourney.Constants;
import thejourney.entity.Facing;

public final class MapLoader {

    private static final String MAP_DIRECTORY = "/assets/maps/";
    private static final String DEFAULT_MAP_NAME = "briarwatch";

    private static final String DEFAULT_MAP = """
            ; Briarwatch Village - default overworld map
            name=Briarwatch Village
            width=32
            height=24
            spawn=100,100

            [ground]
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggggggggggggwwbwwgg
            gggggggggggggggggggggggggwwbwwgg
            gggggggggggggggggggggggggwwbwwgg
            gggggggggggggggggggggggggwwbwwgg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggssssssggggggggggggg
            pppppppppppppppppppppppppppppppp
            pppppppppppppppppppppppppppppppp
            gggggggggggggssssssggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg
            gggggggggggggggppggggggggggggggg

            [terrain]
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ....f....f...........f.......f..
            .....r......f...................
            .....................=====......
            .....................=.f.=......
            .....................=f.f=......
            ................................
            ................................
            .....................=====......
            .....................=.f.=......
            .....................=====......
            ................................
            .......f...................r....
            ................................
            ..............r.................
            ........f...f...................
            ................................
            ................................
            ................................

            [foreground]
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................
            ................................

            [objects]
            house 21 7
            house 5 15
            house 26 16
            well 11 9
            sign 6 8
            chest 20 12
            tree 2 5
            tree 9 0
            tree 12 4
            tree 18 6
            tree 28 8
            tree 2 13
            tree 30 14
            tree 3 20
            tree 10 19
            tree 17 21
            tree 23 21
            rock 8 10
            rock 27 14
            rock 14 20
            """;

    private enum Section {
        NONE,
        GROUND,
        TERRAIN,
        FOREGROUND,
        OBJECTS,
        PORTALS
    }

    private MapLoader() {
    }

    public static TileMap loadDefault() {
        return load(DEFAULT_MAP_NAME);
    }

    public static TileMap load(String mapName) {
        if (mapName == null || mapName.isBlank()) {
            throw new IllegalArgumentException("map name must not be blank");
        }

        String text = readResource(MAP_DIRECTORY + mapName + ".map");
        if (text == null) {
            text = DEFAULT_MAP;
        }

        return parse(text);
    }

    public static TileMap parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("map text must not be blank");
        }

        if (!text.isEmpty() && text.charAt(0) == '\uFEFF') {
            text = text.substring(1);
        }

        String name = "Unnamed Map";
        int width = -1;
        int height = -1;
        SpawnPoint spawn = null;

        List<String> groundRows = new ArrayList<>();
        List<String> terrainRows = new ArrayList<>();
        List<String> foregroundRows = new ArrayList<>();
        List<MapObject> objects = new ArrayList<>();
        List<Portal> portals = new ArrayList<>();

        Section section = Section.NONE;
        String[] lines = text.split("\\R", -1);

        for (int lineNumber = 1; lineNumber <= lines.length; lineNumber++) {
            String line = lines[lineNumber - 1].stripTrailing();

            if (line.isBlank() || line.startsWith(";")) {
                continue;
            }

            if (line.startsWith("[")) {
                section = parseSection(line, lineNumber);
                continue;
            }

            switch (section) {
                case NONE -> {
                    int separator = line.indexOf('=');
                    if (separator < 0) {
                        throw error(lineNumber, "expected metadata key=value");
                    }

                    String key = line.substring(0, separator).trim().toLowerCase(Locale.ROOT);
                    String value = line.substring(separator + 1).trim();

                    switch (key) {
                        case "name" -> name = value;
                        case "width" -> width = parseInt(value, lineNumber, "width");
                        case "height" -> height = parseInt(value, lineNumber, "height");
                        case "spawn" -> spawn = parseSpawn(value, lineNumber);
                        default -> throw error(lineNumber, "unknown metadata key: " + key);
                    }
                }
                case GROUND -> groundRows.add(line);
                case TERRAIN -> terrainRows.add(line);
                case FOREGROUND -> foregroundRows.add(line);
                case OBJECTS -> objects.add(parseObject(line, lineNumber));
                case PORTALS -> portals.add(parsePortal(line, lineNumber));
            }
        }

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("map width and height must be positive");
        }
        if (spawn == null) {
            spawn = new SpawnPoint(Constants.TILE_SIZE, Constants.TILE_SIZE, Facing.DOWN);
        }

        TileDefinition[][] ground = parseLayer(groundRows, width, height, TileLayer.GROUND, "ground");
        TileDefinition[][] terrain = parseLayer(terrainRows, width, height, TileLayer.TERRAIN, "terrain");
        TileDefinition[][] foreground = parseLayer(
                foregroundRows,
                width,
                height,
                TileLayer.FOREGROUND,
                "foreground"
        );

        validateObjects(objects, width, height);

        return new TileMap(name, width, height, ground, terrain, foreground, objects, portals, spawn);
    }

    private static String readResource(String path) {
        try (InputStream stream = MapLoader.class.getResourceAsStream(path)) {
            if (stream == null) {
                return null;
            }

            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    private static Section parseSection(String line, int lineNumber) {
        int end = line.indexOf(']');
        if (end < 0) {
            throw error(lineNumber, "section header must end with ]");
        }

        String name = line.substring(1, end).trim().toLowerCase(Locale.ROOT);

        return switch (name) {
            case "ground" -> Section.GROUND;
            case "terrain" -> Section.TERRAIN;
            case "foreground" -> Section.FOREGROUND;
            case "objects" -> Section.OBJECTS;
            case "portals" -> Section.PORTALS;
            default -> throw error(lineNumber, "unknown section: " + name);
        };
    }

    private static TileDefinition[][] parseLayer(
            List<String> rows,
            int width,
            int height,
            TileLayer layer,
            String label
    ) {
        if (rows.isEmpty()) {
            if (layer == TileLayer.GROUND) {
                throw new IllegalArgumentException("ground layer is required");
            }

            return new TileDefinition[height][width];
        }

        if (rows.size() != height) {
            throw new IllegalArgumentException(
                    label + " layer has " + rows.size() + " rows, expected " + height
            );
        }

        TileDefinition[][] result = new TileDefinition[height][width];

        for (int y = 0; y < height; y++) {
            String row = rows.get(y);

            if (row.length() != width) {
                throw new IllegalArgumentException(
                        label + " row " + y + " has length " + row.length() + ", expected " + width
                );
            }

            for (int x = 0; x < width; x++) {
                char symbol = row.charAt(x);

                if (symbol == '.' || symbol == ' ') {
                    continue;
                }

                TileDefinition definition = TileRegistry.bySymbol(symbol);
                if (definition == null) {
                    throw new IllegalArgumentException(
                            "unknown tile symbol '" + symbol + "' in " + label + " at " + x + "," + y
                    );
                }
                if (definition.layer() != layer) {
                    throw new IllegalArgumentException(
                            "tile '" + symbol + "' belongs to " + definition.layer()
                                    + ", not " + layer
                    );
                }

                result[y][x] = definition;
            }
        }

        return result;
    }

    private static MapObject parseObject(String line, int lineNumber) {
        String[] parts = line.trim().split("\\s+");

        if (parts.length != 3) {
            throw error(lineNumber, "object line must be: type tileX tileY");
        }

        MapObjectType type;
        try {
            type = MapObjectType.valueOf(parts[0].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw error(lineNumber, "unknown object type: " + parts[0]);
        }

        int tileX = parseInt(parts[1], lineNumber, "object tileX");
        int tileY = parseInt(parts[2], lineNumber, "object tileY");

        return new MapObject(type, tileX * Constants.TILE_SIZE, tileY * Constants.TILE_SIZE);
    }

    private static Portal parsePortal(String line, int lineNumber) {
        String[] parts = line.trim().split("\\s+");

        if (parts.length != 7) {
            throw error(
                    lineNumber,
                    "portal line must be: tileX tileY tileWidth tileHeight target spawnX spawnY"
            );
        }

        int tileX = parseInt(parts[0], lineNumber, "portal tileX");
        int tileY = parseInt(parts[1], lineNumber, "portal tileY");
        int tileWidth = parseInt(parts[2], lineNumber, "portal tileWidth");
        int tileHeight = parseInt(parts[3], lineNumber, "portal tileHeight");
        double spawnX = parseDouble(parts[5], lineNumber, "portal spawnX");
        double spawnY = parseDouble(parts[6], lineNumber, "portal spawnY");

        CollisionBox trigger = new CollisionBox(
                tileX * Constants.TILE_SIZE,
                tileY * Constants.TILE_SIZE,
                tileWidth * Constants.TILE_SIZE,
                tileHeight * Constants.TILE_SIZE
        );

        return new Portal(trigger, parts[4], spawnX, spawnY);
    }

    private static SpawnPoint parseSpawn(String value, int lineNumber) {
        String[] parts = value.split(",");

        if (parts.length < 2 || parts.length > 3) {
            throw error(lineNumber, "spawn must be: x,y or x,y,facing");
        }

        double x = parseDouble(parts[0], lineNumber, "spawn x");
        double y = parseDouble(parts[1], lineNumber, "spawn y");

        Facing facing = Facing.DOWN;
        if (parts.length == 3) {
            try {
                facing = Facing.valueOf(parts[2].trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw error(lineNumber, "unknown spawn facing: " + parts[2]);
            }
        }

        return new SpawnPoint(x, y, facing);
    }

    private static void validateObjects(List<MapObject> objects, int width, int height) {
        int pixelWidth = width * Constants.TILE_SIZE;
        int pixelHeight = height * Constants.TILE_SIZE;

        for (MapObject object : objects) {
            if (object.getX() < 0
                    || object.getY() < 0
                    || object.getX() + object.getWidth() > pixelWidth
                    || object.getY() + object.getHeight() > pixelHeight) {
                throw new IllegalArgumentException(
                        "object " + object.getType() + " outside map bounds at "
                                + object.getX() + "," + object.getY()
                );
            }
        }
    }

    private static int parseInt(String value, int lineNumber, String label) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw error(lineNumber, "invalid " + label + ": " + value);
        }
    }

    private static double parseDouble(String value, int lineNumber, String label) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw error(lineNumber, "invalid " + label + ": " + value);
        }
    }

    private static IllegalArgumentException error(int lineNumber, String message) {
        return new IllegalArgumentException("map line " + lineNumber + ": " + message);
    }
}
