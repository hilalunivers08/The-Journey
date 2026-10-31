package thejourney;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.locks.LockSupport;

import javax.swing.JPanel;

import thejourney.entity.Player;
import thejourney.input.InputState;
import thejourney.input.KeyHandler;

@SuppressWarnings({"serial", "this-escape"})
public class GamePanel extends JPanel implements Runnable {

    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final Font DEBUG_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Font PAUSE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    private static final Color PAUSE_OVERLAY_COLOR = new Color(0, 0, 0, 160);

    private final InputState input = new InputState();
    private final KeyHandler keyHandler = new KeyHandler(input);
    private final Player player = new Player(input);
    private final Thread gameThread;

    private volatile boolean running;
    private volatile GameState gameState = GameState.PLAYING;
    private volatile int fps;
    private volatile int ups;

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        addKeyListener(keyHandler);
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                input.clear();
            }
        });

        gameThread = new Thread(this, "thejourney-game-loop");
    }

    public void startGameThread() {
        if (running) {
            return;
        }

        running = true;
        gameThread.start();
    }

    public void stopGameThread() {
        if (!running) {
            return;
        }

        running = false;
        gameThread.interrupt();

        try {
            gameThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        final double updateInterval = (double) NANOS_PER_SECOND / Constants.TARGET_UPS;
        final long maxFrameTime = (long) (Constants.MAX_FRAME_TIME_SECONDS * NANOS_PER_SECOND);

        long lastTime = System.nanoTime();
        double accumulator = 0;
        long secondTimer = 0;
        int frameCount = 0;
        int updateCount = 0;

        while (running) {
            long frameStart = System.nanoTime();
            long elapsed = frameStart - lastTime;
            lastTime = frameStart;

            if (elapsed > maxFrameTime) {
                elapsed = maxFrameTime;
            }

            accumulator += elapsed;
            secondTimer += elapsed;

            while (accumulator >= updateInterval) {
                update(1.0 / Constants.TARGET_UPS);
                accumulator -= updateInterval;
                updateCount++;
            }

            repaint();
            frameCount++;

            if (secondTimer >= NANOS_PER_SECOND) {
                fps = frameCount;
                ups = updateCount;
                frameCount = 0;
                updateCount = 0;
                secondTimer = 0;
            }

            long frameTime = System.nanoTime() - frameStart;
            long sleepTime = (long) updateInterval - frameTime;
            if (sleepTime > 0) {
                LockSupport.parkNanos(sleepTime);
            }
        }
    }

    private void update(double deltaTime) {
        if (input.wasPressed(KeyEvent.VK_ESCAPE)) {
            gameState = gameState == GameState.PLAYING
                    ? GameState.PAUSED
                    : GameState.PLAYING;
        }

        if (gameState == GameState.PLAYING) {
            player.update(deltaTime);
        }

        input.endFrame();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            );

            player.render(g2);

            if (gameState == GameState.PAUSED) {
                g2.setColor(PAUSE_OVERLAY_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(Color.WHITE);
                g2.setFont(PAUSE_FONT);

                String text = "PAUSED";
                FontMetrics metrics = g2.getFontMetrics();
                int textX = (getWidth() - metrics.stringWidth(text)) / 2;
                int textY = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

                g2.drawString(text, textX, textY);
            }

            if (Constants.DEBUG_OVERLAY) {
                g2.setFont(DEBUG_FONT);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString(
                        "FPS: " + fps + "  UPS: " + ups + "  State: " + gameState,
                        8,
                        16
                );
            }
        } finally {
            g2.dispose();
        }
    }
}
