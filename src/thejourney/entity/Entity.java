package thejourney.entity;

import java.awt.Graphics2D;

public abstract class Entity {

    private double x;
    private double y;
    private final int width;
    private final int height;
    private double speed;

    protected Entity(double x, double y, int width, int height, double speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    public abstract void update(double deltaTime);

    public abstract void render(Graphics2D g2);

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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
