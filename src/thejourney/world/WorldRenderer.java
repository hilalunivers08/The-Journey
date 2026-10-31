package thejourney.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import thejourney.Constants;
import thejourney.entity.Player;

public final class WorldRenderer {

    private static final Color BACKGROUND = new Color(18, 22, 24);

    private static final Color GRASS = new Color(76, 139, 63);
    private static final Color GRASS_DARK = new Color(54, 110, 48);
    private static final Color GRASS_LIGHT = new Color(102, 164, 76);

    private static final Color PATH = new Color(176, 138, 90);
    private static final Color PATH_DARK = new Color(140, 106, 66);
    private static final Color PATH_LIGHT = new Color(202, 170, 120);

    private static final Color WATER = new Color(58, 110, 154);
    private static final Color WATER_DARK = new Color(42, 86, 128);
    private static final Color WATER_LIGHT = new Color(104, 162, 198);

    private static final Color STONE = new Color(122, 124, 132);
    private static final Color STONE_DARK = new Color(82, 84, 92);
    private static final Color STONE_LIGHT = new Color(158, 160, 166);

    private static final Color BRIDGE = new Color(142, 96, 56);
    private static final Color BRIDGE_DARK = new Color(100, 64, 36);
    private static final Color BRIDGE_LIGHT = new Color(174, 126, 78);

    private static final Color FLOWER_STEM = new Color(46, 104, 48);
    private static final Color[] FLOWER_PETALS = {
        new Color(236, 214, 102),
        new Color(224, 112, 104),
        new Color(226, 226, 232),
        new Color(176, 120, 208)
    };

    private static final Color LEAF = new Color(42, 96, 52);
    private static final Color LEAF_LIGHT = new Color(74, 132, 64);
    private static final Color ROOF = new Color(142, 60, 52);
    private static final Color ROOF_DARK = new Color(98, 42, 38);
    private static final Color ROOF_LIGHT = new Color(178, 84, 66);

    private final TileMap map;
    private final List<Renderable> renderables = new ArrayList<>();

    public WorldRenderer(TileMap map) {
        if (map == null) {
            throw new IllegalArgumentException("map must not be null");
        }

        this.map = map;
    }

    public void render(Graphics2D g2, Camera camera, Player player, double alpha) {
        g2.setColor(BACKGROUND);
        g2.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        Graphics2D g = (Graphics2D) g2.create();
        try {
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF
            );
            g.translate(-camera.getX(), -camera.getY());

            drawLayer(g, camera, TileLayer.GROUND);
            drawLayer(g, camera, TileLayer.TERRAIN);

            renderables.clear();
            renderables.addAll(map.getObjects());
            renderables.add(player);
            renderables.sort(Comparator.comparingDouble(Renderable::getSortY));

            for (Renderable renderable : renderables) {
                renderable.render(g, alpha);
            }

            drawLayer(g, camera, TileLayer.FOREGROUND);
        } finally {
            g.dispose();
        }
    }

    private void drawLayer(Graphics2D g2, Camera camera, TileLayer layer) {
        int firstColumn = Math.max(0, camera.getX() / Constants.TILE_SIZE);
        int lastColumn = Math.min(
                map.getWidth() - 1,
                (camera.getX() + Constants.SCREEN_WIDTH - 1) / Constants.TILE_SIZE
        );
        int firstRow = Math.max(0, camera.getY() / Constants.TILE_SIZE);
        int lastRow = Math.min(
                map.getHeight() - 1,
                (camera.getY() + Constants.SCREEN_HEIGHT - 1) / Constants.TILE_SIZE
        );

        for (int tileY = firstRow; tileY <= lastRow; tileY++) {
            for (int tileX = firstColumn; tileX <= lastColumn; tileX++) {
                TileDefinition tile = map.getTile(layer, tileX, tileY);
                if (tile == null) {
                    continue;
                }

                drawTile(
                        g2,
                        tile.type(),
                        tileX * Constants.TILE_SIZE,
                        tileY * Constants.TILE_SIZE,
                        tileX,
                        tileY
                );
            }
        }
    }

    private static void drawTile(
            Graphics2D g2,
            TileType type,
            int pixelX,
            int pixelY,
            int tileX,
            int tileY
    ) {
        int hash = hash(tileX, tileY);

        switch (type) {
            case GRASS -> drawGrass(g2, pixelX, pixelY, hash);
            case PATH -> drawPath(g2, pixelX, pixelY, hash);
            case WATER -> drawWater(g2, pixelX, pixelY, hash);
            case STONE_FLOOR -> drawStoneFloor(g2, pixelX, pixelY, tileX, tileY);
            case BRIDGE -> drawBridge(g2, pixelX, pixelY, hash);
            case FLOWERS -> drawFlowers(g2, pixelX, pixelY, hash);
            case PEBBLES -> drawPebbles(g2, pixelX, pixelY, hash);
            case ROCK -> drawRockTile(g2, pixelX, pixelY);
            case WALL -> drawWall(g2, pixelX, pixelY, tileX, tileY);
            case FENCE -> drawFence(g2, pixelX, pixelY);
            case CANOPY -> drawCanopy(g2, pixelX, pixelY, hash);
            case ROOF -> drawRoof(g2, pixelX, pixelY, tileX, tileY);
        }
    }

    private static void drawGrass(Graphics2D g2, int x, int y, int hash) {
        g2.setColor(GRASS);
        g2.fillRect(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);

        for (int i = 0; i < 2; i++) {
            int bladeX = x + 3 + Math.floorMod(hash >> (i * 7), 26);
            int bladeY = y + 3 + Math.floorMod(hash >> (i * 7 + 3), 26);

            g2.setColor(GRASS_DARK);
            g2.fillRect(bladeX, bladeY, 1, 3);
            g2.setColor(GRASS_LIGHT);
            g2.fillRect(bladeX + 2, bladeY + 1, 1, 2);
        }
    }

    private static void drawPath(Graphics2D g2, int x, int y, int hash) {
        g2.setColor(PATH);
        g2.fillRect(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);

        for (int i = 0; i < 3; i++) {
            int pebbleX = x + 3 + Math.floorMod(hash >> (i * 6), 26);
            int pebbleY = y + 4 + Math.floorMod(hash >> (i * 6 + 2), 24);

            g2.setColor(i % 2 == 0 ? PATH_DARK : PATH_LIGHT);
            g2.fillRect(pebbleX, pebbleY, 2, 1);
        }
    }

    private static void drawWater(Graphics2D g2, int x, int y, int hash) {
        g2.setColor(WATER);
        g2.fillRect(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);

        for (int row = 5; row < Constants.TILE_SIZE; row += 10) {
            int waveX = x + Math.floorMod(hash + row, 18);

            g2.setColor(WATER_DARK);
            g2.fillRect(waveX, y + row, 10, 1);
            g2.setColor(WATER_LIGHT);
            g2.fillRect(waveX + 3, y + row + 2, 8, 1);
        }
    }

    private static void drawStoneFloor(Graphics2D g2, int x, int y, int tileX, int tileY) {
        g2.setColor(STONE);
        g2.fillRect(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);

        g2.setColor(STONE_DARK);
        for (int row = 0; row < 2; row++) {
            int lineY = y + row * 16;
            g2.fillRect(x, lineY, Constants.TILE_SIZE, 1);

            int seamX = x + ((row + tileY) % 2 == 0 ? 15 : 7);
            g2.fillRect(seamX, lineY + 1, 1, 15);
        }

        g2.setColor(STONE_LIGHT);
        g2.fillRect(x + 2, y + 2, 5, 1);
        g2.fillRect(x + 19, y + 19, 6, 1);

        if ((tileX + tileY) % 3 == 0) {
            g2.setColor(STONE_DARK);
            g2.fillRect(x + 9, y + 25, 8, 3);
        }
    }

    private static void drawBridge(Graphics2D g2, int x, int y, int hash) {
        g2.setColor(BRIDGE);
        g2.fillRect(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);

        for (int row = 0; row < Constants.TILE_SIZE; row += 8) {
            g2.setColor(BRIDGE_DARK);
            g2.fillRect(x, y + row, Constants.TILE_SIZE, 1);
            g2.setColor(BRIDGE_LIGHT);
            g2.fillRect(x, y + row + 1, Constants.TILE_SIZE, 1);
        }

        g2.setColor(BRIDGE_DARK);
        int nailX = x + 6 + Math.floorMod(hash, 16);
        g2.fillRect(nailX, y + 6, 1, 1);
        g2.fillRect(nailX, y + 22, 1, 1);
    }

    private static void drawFlowers(Graphics2D g2, int x, int y, int hash) {
        for (int i = 0; i < 3; i++) {
            int flowerX = x + 5 + Math.floorMod(hash >> (i * 5), 20);
            int flowerY = y + 7 + Math.floorMod(hash >> (i * 5 + 2), 18);

            g2.setColor(FLOWER_STEM);
            g2.fillRect(flowerX, flowerY + 3, 1, 4);

            g2.setColor(FLOWER_PETALS[Math.floorMod(hash >> (i * 3), FLOWER_PETALS.length)]);
            g2.fillRect(flowerX - 1, flowerY, 3, 3);
            g2.setColor(new Color(250, 232, 160));
            g2.fillRect(flowerX + 1, flowerY + 1, 1, 1);
        }
    }

    private static void drawPebbles(Graphics2D g2, int x, int y, int hash) {
        g2.setColor(STONE_DARK);
        for (int i = 0; i < 4; i++) {
            int pebbleX = x + 4 + Math.floorMod(hash >> (i * 4), 22);
            int pebbleY = y + 5 + Math.floorMod(hash >> (i * 4 + 2), 20);
            g2.fillRect(pebbleX, pebbleY, 3, 2);
        }

        g2.setColor(STONE_LIGHT);
        g2.fillRect(x + 10, y + 12, 2, 1);
    }

    private static void drawRockTile(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(0, 0, 0, 55));
        g2.fillOval(x + 5, y + 25, 22, 6);

        g2.setColor(STONE_DARK);
        g2.fillRect(x + 6, y + 10, 20, 18);
        g2.fillRect(x + 9, y + 7, 14, 24);

        g2.setColor(STONE);
        g2.fillRect(x + 8, y + 12, 16, 14);
        g2.fillRect(x + 11, y + 9, 10, 20);

        g2.setColor(STONE_LIGHT);
        g2.fillRect(x + 10, y + 10, 5, 4);
        g2.fillRect(x + 19, y + 15, 3, 3);
    }

    private static void drawWall(Graphics2D g2, int x, int y, int tileX, int tileY) {
        g2.setColor(STONE_DARK);
        g2.fillRect(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);

        g2.setColor(STONE);
        for (int row = 0; row < 4; row++) {
            int rowY = y + row * 8;
            g2.fillRect(x + 1, rowY + 1, Constants.TILE_SIZE - 2, 6);

            int offset = (row + tileX + tileY) % 2 == 0 ? 0 : 8;
            for (int column = offset; column < Constants.TILE_SIZE; column += 16) {
                g2.setColor(STONE_DARK);
                g2.fillRect(x + column, rowY, 1, 8);
                g2.setColor(STONE);
            }
        }

        g2.setColor(STONE_LIGHT);
        g2.fillRect(x + 2, y + 1, 6, 1);
    }

    private static void drawFence(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(84, 54, 32));
        g2.fillRect(x + 3, y + 7, 4, 22);
        g2.fillRect(x + 25, y + 7, 4, 22);
        g2.fillRect(x + 2, y + 10, 28, 4);
        g2.fillRect(x + 2, y + 20, 28, 4);

        g2.setColor(new Color(138, 92, 52));
        g2.fillRect(x + 4, y + 8, 2, 19);
        g2.fillRect(x + 26, y + 8, 2, 19);
        g2.fillRect(x + 3, y + 11, 26, 2);
        g2.fillRect(x + 3, y + 21, 26, 2);
    }

    private static void drawCanopy(Graphics2D g2, int x, int y, int hash) {
        g2.setColor(LEAF);
        g2.fillRect(x + 2, y + 4, 28, 24);
        g2.fillRect(x + 7, y, 18, 32);

        g2.setColor(LEAF_LIGHT);
        g2.fillRect(x + 6 + Math.floorMod(hash, 6), y + 6, 7, 5);
        g2.fillRect(x + 17 + Math.floorMod(hash >> 3, 5), y + 15, 6, 5);
    }

    private static void drawRoof(Graphics2D g2, int x, int y, int tileX, int tileY) {
        g2.setColor(ROOF);
        g2.fillRect(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);
        g2.setColor(ROOF_DARK);
        g2.fillRect(x, y, Constants.TILE_SIZE, 1);

        for (int row = 4; row < Constants.TILE_SIZE; row += 6) {
            g2.setColor(ROOF_DARK);
            g2.fillRect(x, y + row, Constants.TILE_SIZE, 1);
            g2.setColor(ROOF_LIGHT);
            g2.fillRect(x + ((row + tileX + tileY) % 2 == 0 ? 0 : 6), y + row + 1, 10, 1);
        }
    }

    private static int hash(int x, int y) {
        int value = x * 374761393 + y * 668265263;
        value = (value ^ (value >>> 13)) * 1274126177;
        return value ^ (value >>> 16);
    }
}
