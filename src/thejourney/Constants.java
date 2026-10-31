package thejourney;

public final class Constants {

    private Constants() {
    }

    public static final int TILE_SIZE = 32;
    public static final int VIEWPORT_COLUMNS = 24;
    public static final int VIEWPORT_ROWS = 18;

    public static final int SCREEN_WIDTH = TILE_SIZE * VIEWPORT_COLUMNS;
    public static final int SCREEN_HEIGHT = TILE_SIZE * VIEWPORT_ROWS;

    public static final int TARGET_UPS = 60;
    public static final double MAX_FRAME_TIME_SECONDS = 0.25;

    public static final double PLAYER_SPEED = 120.0;

    public static final boolean DEBUG_OVERLAY = true;
}
