package thejourney.graphics;

import java.awt.image.BufferedImage;

public final class SpriteSheet {

    private final BufferedImage image;
    private final int frameWidth;
    private final int frameHeight;

    public SpriteSheet(BufferedImage image, int frameWidth, int frameHeight) {
        if (image == null) {
            throw new IllegalArgumentException("image must not be null");
        }
        if (frameWidth <= 0 || frameHeight <= 0) {
            throw new IllegalArgumentException("frame size must be positive");
        }
        if (image.getWidth() % frameWidth != 0 || image.getHeight() % frameHeight != 0) {
            throw new IllegalArgumentException("image size must be a multiple of the frame size");
        }

        this.image = image;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    public int getColumns() {
        return image.getWidth() / frameWidth;
    }

    public int getRows() {
        return image.getHeight() / frameHeight;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public Sprite getSprite(int column, int row) {
        if (column < 0 || column >= getColumns() || row < 0 || row >= getRows()) {
            throw new IllegalArgumentException("sprite coordinates out of bounds");
        }

        return new Sprite(image.getSubimage(
                column * frameWidth,
                row * frameHeight,
                frameWidth,
                frameHeight
        ));
    }
}
