package thejourney.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import thejourney.entity.Facing;

public final class AssetManager {

    public static final int SPRITE_SIZE = 32;
    public static final int PLAYER_SHEET_COLUMNS = 6;
    public static final int PLAYER_SHEET_ROWS = 4;

    private static final String PLAYER_SHEET_PATH = "/assets/sprites/player.png";

    private static final Color OUTLINE = new Color(24, 20, 32);
    private static final Color SKIN = new Color(232, 190, 154);
    private static final Color HAIR = new Color(96, 58, 34);
    private static final Color TUNIC = new Color(58, 110, 165);
    private static final Color TUNIC_DARK = new Color(40, 78, 122);
    private static final Color BELT = new Color(74, 52, 32);
    private static final Color PANTS = new Color(62, 66, 82);
    private static final Color BOOTS = new Color(52, 38, 28);
    private static final Color SHADOW = new Color(0, 0, 0, 64);

    private final SpriteSheet playerSheet;

    public AssetManager() {
        this.playerSheet = loadPlayerSheet();
    }

    public SpriteSheet getPlayerSheet() {
        return playerSheet;
    }

    private static SpriteSheet loadPlayerSheet() {
        BufferedImage image = readImage(PLAYER_SHEET_PATH);

        if (isExpectedPlayerSheet(image)) {
            return new SpriteSheet(image, SPRITE_SIZE, SPRITE_SIZE);
        }

        return new SpriteSheet(createPlaceholderPlayerSheet(), SPRITE_SIZE, SPRITE_SIZE);
    }

    private static BufferedImage readImage(String path) {
        try (InputStream stream = AssetManager.class.getResourceAsStream(path)) {
            if (stream == null) {
                return null;
            }

            return ImageIO.read(stream);
        } catch (IOException e) {
            return null;
        }
    }

    private static boolean isExpectedPlayerSheet(BufferedImage image) {
        return image != null
                && image.getWidth() == SPRITE_SIZE * PLAYER_SHEET_COLUMNS
                && image.getHeight() == SPRITE_SIZE * PLAYER_SHEET_ROWS;
    }

    private static BufferedImage createPlaceholderPlayerSheet() {
        BufferedImage sheet = new BufferedImage(
                SPRITE_SIZE * PLAYER_SHEET_COLUMNS,
                SPRITE_SIZE * PLAYER_SHEET_ROWS,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = sheet.createGraphics();
        try {
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF
            );

            Facing[] facings = Facing.values();
            for (int row = 0; row < facings.length && row < PLAYER_SHEET_ROWS; row++) {
                for (int column = 0; column < PLAYER_SHEET_COLUMNS; column++) {
                    drawPlaceholderFrame(g2, column, row, facings[row]);
                }
            }
        } finally {
            g2.dispose();
        }

        return sheet;
    }

    private static void drawPlaceholderFrame(Graphics2D g2, int column, int row, Facing facing) {
        int originX = column * SPRITE_SIZE;
        int originY = row * SPRITE_SIZE;

        boolean walking = column >= 2;
        int walkFrame = walking ? column - 2 : 0;

        int bob;
        if (column == 1) {
            bob = 1;
        } else if (walking) {
            bob = walkFrame == 1 || walkFrame == 3 ? 1 : 0;
        } else {
            bob = 0;
        }

        int stride = 0;
        if (walking) {
            stride = walkFrame == 0 || walkFrame == 2 ? 1 : 0;
        }

        g2.setColor(SHADOW);
        g2.fillOval(originX + 8, originY + 27, 16, 5);

        int headX = originX + 10;
        int headY = originY + 4 + bob;
        int bodyY = headY + 12;
        int legY = bodyY + 10;

        drawLegs(g2, headX, legY, stride);
        drawArms(g2, headX, bodyY);

        g2.setColor(OUTLINE);
        g2.fillRect(headX - 1, bodyY - 1, 14, 11);
        g2.setColor(TUNIC);
        g2.fillRect(headX, bodyY, 12, 9);
        g2.setColor(BELT);
        g2.fillRect(headX, bodyY + 7, 12, 2);

        g2.setColor(OUTLINE);
        g2.fillRect(headX - 1, headY - 1, 14, 14);
        g2.setColor(SKIN);
        g2.fillRect(headX, headY, 12, 12);

        drawHead(g2, headX, headY, facing);
    }

    private static void drawLegs(Graphics2D g2, int headX, int legY, int stride) {
        g2.setColor(OUTLINE);
        g2.fillRect(headX + 1, legY, 4, 6 + stride);
        g2.fillRect(headX + 7, legY, 4, 6 + (stride == 0 ? 1 : 0));

        g2.setColor(PANTS);
        g2.fillRect(headX + 2, legY, 2, 5 + stride);
        g2.fillRect(headX + 8, legY, 2, 5 + (stride == 0 ? 1 : 0));

        g2.setColor(BOOTS);
        g2.fillRect(headX + 1, legY + 5 + stride, 4, 2);
        g2.fillRect(headX + 7, legY + 5 + (stride == 0 ? 1 : 0), 4, 2);
    }

    private static void drawArms(Graphics2D g2, int headX, int bodyY) {
        g2.setColor(OUTLINE);
        g2.fillRect(headX - 4, bodyY + 1, 4, 9);
        g2.fillRect(headX + 12, bodyY + 1, 4, 9);

        g2.setColor(TUNIC_DARK);
        g2.fillRect(headX - 3, bodyY + 2, 2, 7);
        g2.fillRect(headX + 13, bodyY + 2, 2, 7);

        g2.setColor(SKIN);
        g2.fillRect(headX - 3, bodyY + 8, 2, 2);
        g2.fillRect(headX + 13, bodyY + 8, 2, 2);
    }

    private static void drawHead(Graphics2D g2, int headX, int headY, Facing facing) {
        g2.setColor(HAIR);

        switch (facing) {
            case DOWN -> {
                g2.fillRect(headX, headY, 12, 4);
                g2.setColor(OUTLINE);
                g2.fillRect(headX + 3, headY + 6, 2, 2);
                g2.fillRect(headX + 8, headY + 6, 2, 2);
                g2.fillRect(headX + 5, headY + 9, 3, 1);
            }
            case UP -> g2.fillRect(headX, headY, 12, 10);
            case LEFT -> {
                g2.fillRect(headX, headY, 5, 12);
                g2.setColor(OUTLINE);
                g2.fillRect(headX + 1, headY + 6, 2, 2);
            }
            case RIGHT -> {
                g2.fillRect(headX + 7, headY, 5, 12);
                g2.setColor(OUTLINE);
                g2.fillRect(headX + 9, headY + 6, 2, 2);
            }
            default -> throw new IllegalStateException("Unhandled facing: " + facing);
        }
    }
}
