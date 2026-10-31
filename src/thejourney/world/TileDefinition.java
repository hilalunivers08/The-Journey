package thejourney.world;

public record TileDefinition(char symbol, TileType type, TileLayer layer, boolean solid) {

    public TileDefinition {
        if (symbol == ' ' || symbol == '.') {
            throw new IllegalArgumentException("symbol must identify a tile");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (layer == null) {
            throw new IllegalArgumentException("layer must not be null");
        }
    }
}
