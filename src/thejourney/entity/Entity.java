package thejourney.entity;

import java.awt.Graphics2D;

import thejourney.world.CollisionBox;

public abstract class Entity {

    private double x;
    private double y;
    private double previousX;
    private double previousY;
    private final int width;
    private final int height;
    private double speed;
    private int collisionOffsetX;
    private int collisionOffsetY;
    private int collisionWidth;
    private int collisionHeight;

    protected Entity(double x, double y, int width, int height, double speed) {
        this.x = x;
        this.y = y;
        this.previousX = x;
        this.previousY = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.collisionOffsetX = 0;
        this.collisionOffsetY = 0;
        this.collisionWidth = width;
        this.collisionHeight = height;
    }

    public abstract void update(double deltaTime);

    public abstract void render(Graphics2D g2, double alpha);

    public void syncPreviousPosition() {
        previousX = x;
        previousY = y;
    }

    public double getInterpolatedX(double alpha) {
        double clampedAlpha = Math.max(0, Math.min(1, alpha));
        return previousX + (x - previousX) * clampedAlpha;
    }

    public double getInterpolatedY(double alpha) {
        double clampedAlpha = Math.max(0, Math.min(1, alpha));
        return previousY + (y - previousY) * clampedAlpha;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected void setCollisionBox(int offsetX, int offsetY, int width, int height) {
        if (offsetX < 0 || offsetY < 0
                || offsetX + width > this.width
                || offsetY + height > this.height) {
            throw new IllegalArgumentException("collision box must fit inside the entity");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("collision box size must be positive");
        }

        this.collisionOffsetX = offsetX;
        this.collisionOffsetY = offsetY;
        this.collisionWidth = width;
        this.collisionHeight = height;
    }

    public CollisionBox getCollisionBox() {
        return new CollisionBox(
                x + collisionOffsetX,
                y + collisionOffsetY,
                collisionWidth,
                collisionHeight
        );
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
