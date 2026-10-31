package thejourney.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TileRegistry {

    private static final Map<Character, TileDefinition> BY_SYMBOL = new HashMap<>();

    static {
        register(new TileDefinition('g', TileType.GRASS, TileLayer.GROUND, false));
        register(new TileDefinition('p', TileType.PATH, TileLayer.GROUND, false));
        register(new TileDefinition('w', TileType.WATER, TileLayer.GROUND, true));
        register(new TileDefinition('s', TileType.STONE_FLOOR, TileLayer.GROUND, false));
        register(new TileDefinition('b', TileType.BRIDGE, TileLayer.GROUND, false));

        register(new TileDefinition('f', TileType.FLOWERS, TileLayer.TERRAIN, false));
        register(new TileDefinition('e', TileType.PEBBLES, TileLayer.TERRAIN, false));
        register(new TileDefinition('r', TileType.ROCK, TileLayer.TERRAIN, true));
        register(new TileDefinition('#', TileType.WALL, TileLayer.TERRAIN, true));
        register(new TileDefinition('=', TileType.FENCE, TileLayer.TERRAIN, true));

        register(new TileDefinition('c', TileType.CANOPY, TileLayer.FOREGROUND, false));
        register(new TileDefinition('^', TileType.ROOF, TileLayer.FOREGROUND, false));
    }

    private TileRegistry() {
    }

    private static void register(TileDefinition definition) {
        TileDefinition previous = BY_SYMBOL.put(definition.symbol(), definition);
        if (previous != null) {
            throw new IllegalStateException("Duplicate tile symbol: " + definition.symbol());
        }
    }

    public static TileDefinition bySymbol(char symbol) {
        return BY_SYMBOL.get(symbol);
    }

    public static Map<Character, TileDefinition> all() {
        return Collections.unmodifiableMap(BY_SYMBOL);
    }
}
