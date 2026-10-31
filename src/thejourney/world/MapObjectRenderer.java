package thejourney.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public final class MapObjectRenderer {

    private static final Color SHADOW = new Color(0, 0, 0, 70);

    private static final Color TRUNK_DARK = new Color(58, 38, 26);
    private static final Color TRUNK = new Color(96, 64, 40);
    private static final Color LEAF_DARK = new Color(30, 74, 42);
    private static final Color LEAF = new Color(52, 112, 58);
    private static final Color LEAF_LIGHT = new Color(82, 142, 74);

    private static final Color STONE_DARK = new Color(66, 68, 76);
    private static final Color STONE = new Color(112, 116, 124);
    private static final Color STONE_LIGHT = new Color(156, 160, 166);

    private static final Color WOOD_DARK = new Color(72, 46, 28);
    private static final Color WOOD = new Color(132, 88, 50);
    private static final Color WOOD_LIGHT = new Color(170, 120, 72);

    private static final Color WALL_DARK = new Color(112, 92, 70);
    private static final Color WALL = new Color(204, 186, 148);
    private static final Color WALL_LIGHT = new Color(230, 214, 180);
    private static final Color ROOF_DARK = new Color(92, 42, 40);
    private static final Color ROOF = new Color(148, 62, 54);
    private static final Color ROOF_LIGHT = new Color(184, 88, 70);
    private static final Color WINDOW = new Color(96, 144, 170);
    private static final Color DOOR = new Color(88, 56, 34);

    private MapObjectRenderer() {
    }

    public static void render(Graphics2D g2, MapObject object) {
        int x = (int) Math.round(object.getX());
        int y = (int) Math.round(object.getY());

        switch (object.getType()) {
            case TREE -> drawTree(g2, x, y);
            case ROCK -> drawRock(g2, x, y);
            case SIGN -> drawSign(g2, x, y);
            case CHEST -> drawChest(g2, x, y);
            case HOUSE -> drawHouse(g2, x, y);
            case WELL -> drawWell(g2, x, y);
            default -> throw new IllegalStateException("Unhandled object type: " + object.getType());
        }
    }

    private static void drawTree(Graphics2D g2, int x, int y) {
        int base = y + 64;

        g2.setColor(SHADOW);
        g2.fillOval(x + 5, base - 8, 22, 7);

        g2.setColor(TRUNK_DARK);
        g2.fillRect(x + 12, base - 30, 8, 28);
        g2.setColor(TRUNK);
        g2.fillRect(x + 14, base - 28, 4, 26);
        g2.fillRect(x + 9, base - 22, 5, 3);
        g2.fillRect(x + 18, base - 18, 5, 3);

        g2.setColor(LEAF_DARK);
        g2.fillRect(x + 3, y + 19, 26, 21);
        g2.fillRect(x + 7, y + 9, 18, 34);
        g2.fillRect(x + 1, y + 26, 30, 10);

        g2.setColor(LEAF);
        g2.fillRect(x + 5, y + 21, 22, 17);
        g2.fillRect(x + 9, y + 11, 14, 30);
        g2.fillRect(x + 3, y + 27, 26, 7);

        g2.setColor(LEAF_LIGHT);
        g2.fillRect(x + 8, y + 14, 6, 5);
        g2.fillRect(x + 16, y + 20, 5, 4);
        g2.fillRect(x + 11, y + 30, 7, 4);
    }

    private static void drawRock(Graphics2D g2, int x, int y) {
        g2.setColor(SHADOW);
        g2.fillOval(x + 4, y + 24, 24, 6);

        g2.setColor(STONE_DARK);
        g2.fillRect(x + 5, y + 10, 22, 20);
        g2.fillRect(x + 8, y + 6, 16, 26);

        g2.setColor(STONE);
        g2.fillRect(x + 7, y + 11, 18, 17);
        g2.fillRect(x + 10, y + 8, 12, 22);

        g2.setColor(STONE_LIGHT);
        g2.fillRect(x + 9, y + 9, 6, 5);
        g2.fillRect(x + 18, y + 14, 4, 4);
    }

    private static void drawSign(Graphics2D g2, int x, int y) {
        g2.setColor(SHADOW);
        g2.fillOval(x + 8, y + 26, 16, 5);

        g2.setColor(WOOD_DARK);
        g2.fillRect(x + 14, y + 14, 4, 16);

        g2.setColor(WOOD_DARK);
        g2.fillRect(x + 5, y + 5, 22, 12);
        g2.setColor(WOOD);
        g2.fillRect(x + 7, y + 7, 18, 8);

        g2.setColor(WOOD_LIGHT);
        g2.fillRect(x + 9, y + 9, 12, 1);
        g2.fillRect(x + 9, y + 12, 8, 1);
    }

    private static void drawChest(Graphics2D g2, int x, int y) {
        g2.setColor(SHADOW);
        g2.fillOval(x + 4, y + 26, 24, 5);

        g2.setColor(WOOD_DARK);
        g2.fillRect(x + 6, y + 12, 20, 18);
        g2.setColor(WOOD);
        g2.fillRect(x + 8, y + 14, 16, 14);

        g2.setColor(WOOD_DARK);
        g2.fillRect(x + 6, y + 6, 20, 10);
        g2.setColor(WOOD_LIGHT);
        g2.fillRect(x + 8, y + 8, 16, 6);

        g2.setColor(new Color(202, 166, 72));
        g2.fillRect(x + 14, y + 12, 4, 18);
        g2.fillRect(x + 6, y + 15, 20, 2);
        g2.fillRect(x + 14, y + 16, 4, 4);
    }

    private static void drawHouse(Graphics2D g2, int x, int y) {
        g2.setColor(SHADOW);
        g2.fillOval(x + 4, y + 84, 88, 11);

        g2.setColor(WALL_DARK);
        g2.fillRect(x + 4, y + 34, 88, 58);
        g2.setColor(WALL);
        g2.fillRect(x + 6, y + 36, 84, 54);
        g2.setColor(WALL_LIGHT);
        g2.fillRect(x + 8, y + 38, 80, 8);

        g2.setColor(WOOD_DARK);
        g2.fillRect(x + 6, y + 58, 84, 3);
        g2.fillRect(x + 28, y + 36, 3, 54);
        g2.fillRect(x + 65, y + 36, 3, 54);

        g2.setColor(DOOR);
        g2.fillRect(x + 40, y + 62, 16, 28);
        g2.setColor(WOOD_DARK);
        g2.fillRect(x + 39, y + 61, 18, 2);
        g2.fillRect(x + 39, y + 88, 18, 2);
        g2.fillRect(x + 39, y + 61, 2, 29);
        g2.fillRect(x + 55, y + 61, 2, 29);
        g2.setColor(new Color(212, 178, 86));
        g2.fillRect(x + 51, y + 75, 2, 2);

        drawWindow(g2, x + 14, y + 66);
        drawWindow(g2, x + 68, y + 66);

        Polygon roofOutline = new Polygon(
                new int[]{x, x + 18, x + 78, x + 96},
                new int[]{y + 42, y + 6, y + 6, y + 42},
                4
        );
        g2.setColor(ROOF_DARK);
        g2.fillPolygon(roofOutline);

        Polygon roof = new Polygon(
                new int[]{x + 5, x + 21, x + 75, x + 91},
                new int[]{y + 37, y + 10, y + 10, y + 37},
                4
        );
        g2.setColor(ROOF);
        g2.fillPolygon(roof);

        g2.setColor(ROOF_LIGHT);
        for (int row = 0; row < 3; row++) {
            int shingleY = y + 15 + row * 7;
            g2.fillRect(x + 19 - row * 2, shingleY, 58 + row * 4, 2);
        }

        g2.setColor(new Color(96, 66, 48));
        g2.fillRect(x + 68, y + 2, 10, 16);
        g2.setColor(new Color(120, 86, 62));
        g2.fillRect(x + 70, y + 4, 6, 12);
    }

    private static void drawWindow(Graphics2D g2, int x, int y) {
        g2.setColor(WOOD_DARK);
        g2.fillRect(x, y, 14, 14);
        g2.setColor(WINDOW);
        g2.fillRect(x + 2, y + 2, 10, 10);
        g2.setColor(WOOD);
        g2.fillRect(x + 6, y + 2, 2, 10);
        g2.fillRect(x + 2, y + 6, 10, 2);
    }

    private static void drawWell(Graphics2D g2, int x, int y) {
        g2.setColor(SHADOW);
        g2.fillOval(x + 4, y + 38, 40, 8);

        g2.setColor(STONE_DARK);
        g2.fillRect(x + 6, y + 26, 36, 20);
        g2.setColor(STONE);
        g2.fillRect(x + 8, y + 28, 32, 16);
        g2.setColor(STONE_LIGHT);
        g2.fillRect(x + 10, y + 28, 28, 3);
        g2.setColor(new Color(48, 92, 122));
        g2.fillRect(x + 12, y + 32, 24, 9);

        g2.setColor(WOOD_DARK);
        g2.fillRect(x + 10, y + 8, 4, 22);
        g2.fillRect(x + 34, y + 8, 4, 22);
        g2.fillRect(x + 12, y + 12, 24, 3);

        Polygon roof = new Polygon(
                new int[]{x + 3, x + 24, x + 45},
                new int[]{y + 12, y + 2, y + 12},
                3
        );
        g2.setColor(ROOF_DARK);
        g2.fillPolygon(roof);
        g2.setColor(ROOF);
        g2.fillRect(x + 6, y + 10, 36, 4);
    }
}
