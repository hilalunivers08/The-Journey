package thejourney.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import thejourney.Constants;
import thejourney.GameState;
import thejourney.entity.Player;
import thejourney.graphics.AssetManager;
import thejourney.input.InputState;
import thejourney.input.KeyBindings;
import thejourney.input.PlayerInput;
import thejourney.world.MapLoader;
import thejourney.world.World;

public final class Game {

    private static final Font DEBUG_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Font PAUSE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    private static final Color PAUSE_OVERLAY_COLOR = new Color(0, 0, 0, 160);

    private final InputState input;
    private final AssetManager assets;
    private final Player player;
    private final World world;
    private final FrameStats stats;

    private volatile GameState gameState = GameState.PLAYING;

    public Game(InputState input, FrameStats stats) {
        this.input = input;
        this.stats = stats;
        this.assets = new AssetManager();
        this.player = new Player(assets);
        this.world = new World(MapLoader.loadDefault(), player);
    }

    public void update(double deltaTime) {
        if (input.wasPressed(KeyBindings.PAUSE)) {
            if (gameState == GameState.PLAYING) {
                gameState = GameState.PAUSED;
                player.setInput(PlayerInput.NONE);
                player.syncPreviousPosition();
            } else if (gameState == GameState.PAUSED) {
                gameState = GameState.PLAYING;
            }
        }

        if (gameState == GameState.PLAYING) {
            player.setInput(readPlayerInput());
            world.update(deltaTime);
        } else {
            player.setInput(PlayerInput.NONE);
            player.syncPreviousPosition();
        }

        input.endFrame();
    }

    public void render(Graphics2D g2, double alpha) {
        world.render(g2, alpha);

        if (gameState == GameState.PAUSED) {
            g2.setColor(PAUSE_OVERLAY_COLOR);
            g2.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

            g2.setColor(Color.WHITE);
            g2.setFont(PAUSE_FONT);

            String text = "PAUSED";
            FontMetrics metrics = g2.getFontMetrics();
            int textX = (Constants.SCREEN_WIDTH - metrics.stringWidth(text)) / 2;
            int textY = (Constants.SCREEN_HEIGHT - metrics.getHeight()) / 2 + metrics.getAscent();

            g2.drawString(text, textX, textY);
        }

        if (Constants.DEBUG_OVERLAY) {
            g2.setFont(DEBUG_FONT);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(
                    "FPS: " + stats.getFps() + "  UPS: " + stats.getUps()
                            + "  Game: " + gameState,
                    8,
                    16
            );
            g2.drawString(
                    "Facing: " + player.getFacing() + "  Player: " + player.getState()
                            + "  HP: " + player.getHp() + "/" + player.getMaxHp(),
                    8,
                    32
            );
            g2.drawString(
                    "Map: " + world.getMap().getName()
                            + "  Pos: " + Math.round(player.getX()) + "," + Math.round(player.getY()),
                    8,
                    48
            );
        }
    }

    public void onFocusLost() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        }

        player.setInput(PlayerInput.NONE);
        player.syncPreviousPosition();
        input.clear();
    }

    private PlayerInput readPlayerInput() {
        double moveX = 0;
        double moveY = 0;

        if (input.isDown(KeyBindings.MOVE_LEFT)) {
            moveX -= 1;
        }
        if (input.isDown(KeyBindings.MOVE_RIGHT)) {
            moveX += 1;
        }
        if (input.isDown(KeyBindings.MOVE_UP)) {
            moveY -= 1;
        }
        if (input.isDown(KeyBindings.MOVE_DOWN)) {
            moveY += 1;
        }

        if (moveX == 0 && moveY == 0) {
            return PlayerInput.NONE;
        }

        return new PlayerInput(moveX, moveY);
    }

    public GameState getGameState() {
        return gameState;
    }

    public Player getPlayer() {
        return player;
    }

    public AssetManager getAssets() {
        return assets;
    }

    public World getWorld() {
        return world;
    }
}
