package thejourney.world;

import thejourney.Constants;

public final class Camera {

    private int x;
    private int y;

    public void follow(double targetCenterX, double targetCenterY, int worldWidth, int worldHeight) {
        double desiredX = targetCenterX - Constants.SCREEN_WIDTH / 2.0;
        double desiredY = targetCenterY - Constants.SCREEN_HEIGHT / 2.0;

        x = (int) Math.round(clamp(desiredX, 0, Math.max(0, worldWidth - Constants.SCREEN_WIDTH)));
        y = (int) Math.round(clamp(desiredY, 0, Math.max(0, worldHeight - Constants.SCREEN_HEIGHT)));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
