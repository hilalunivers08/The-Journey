package thejourney.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class Sprite {

    private final BufferedImage image;

    public Sprite(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("image must not be null");
        }

        this.image = image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public void draw(Graphics2D g2, int x, int y) {
        g2.drawImage(image, x, y, null);
    }
}
