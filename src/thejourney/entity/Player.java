package thejourney.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import thejourney.Constants;
import thejourney.input.InputState;

public class Player extends Entity {

    private final InputState input;

    public Player(InputState input) {
        super(100, 100, Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.PLAYER_SPEED);
        this.input = input;
    }

    @Override
    public void update(double deltaTime) {
        double directionX = 0;
        double directionY = 0;

        if (input.isDown(KeyEvent.VK_W)) {
            directionY -= 1;
        }
        if (input.isDown(KeyEvent.VK_S)) {
            directionY += 1;
        }
        if (input.isDown(KeyEvent.VK_A)) {
            directionX -= 1;
        }
        if (input.isDown(KeyEvent.VK_D)) {
            directionX += 1;
        }

        if (directionX == 0 && directionY == 0) {
            return;
        }

        double length = Math.sqrt(directionX * directionX + directionY * directionY);
        directionX /= length;
        directionY /= length;

        double nextX = getX() + directionX * getSpeed() * deltaTime;
        double nextY = getY() + directionY * getSpeed() * deltaTime;

        double maxX = Constants.SCREEN_WIDTH - getWidth();
        double maxY = Constants.SCREEN_HEIGHT - getHeight();

        setPosition(clamp(nextX, 0, maxX), clamp(nextY, 0, maxY));
    }

    @Override
    public void render(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect((int) Math.round(getX()), (int) Math.round(getY()), getWidth(), getHeight());
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
