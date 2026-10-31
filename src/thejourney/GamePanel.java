package thejourney;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import thejourney.core.FrameStats;
import thejourney.core.Game;
import thejourney.core.GameLoop;
import thejourney.input.InputState;
import thejourney.input.KeyHandler;

@SuppressWarnings({"serial", "this-escape"})
public class GamePanel extends JPanel {

    private final InputState input = new InputState();
    private final FrameStats stats = new FrameStats();
    private final Game game = new Game(input, stats);
    private final GameLoop gameLoop;
    private final Object bufferLock = new Object();

    private BufferedImage frontBuffer;
    private BufferedImage backBuffer;

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        frontBuffer = createBlackBuffer();
        backBuffer = createBlackBuffer();

        addKeyListener(new KeyHandler(input));
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                handleFocusLost();
            }
        });

        gameLoop = new GameLoop(game, stats, this::renderFrame);
    }

    public void handleFocusLost() {
        game.onFocusLost();
    }

    public void startGameThread() {
        gameLoop.start();
    }

    public void stopGameThread() {
        gameLoop.stop();
    }

    private void renderFrame(Game activeGame, double alpha) {
        BufferedImage target;

        synchronized (bufferLock) {
            target = backBuffer;
        }

        Graphics2D g2 = target.createGraphics();
        try {
            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            );
            activeGame.render(g2, alpha);
        } finally {
            g2.dispose();
        }

        synchronized (bufferLock) {
            BufferedImage previousFront = frontBuffer;
            frontBuffer = target;
            backBuffer = previousFront;
        }

        repaint();
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

            synchronized (bufferLock) {
                g2.drawImage(frontBuffer, 0, 0, getWidth(), getHeight(), null);
            }
        } finally {
            g2.dispose();
        }

        stats.recordFrame();
    }

    private static BufferedImage createBlackBuffer() {
        BufferedImage image = new BufferedImage(
                Constants.SCREEN_WIDTH,
                Constants.SCREEN_HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2 = image.createGraphics();
        try {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, image.getWidth(), image.getHeight());
        } finally {
            g2.dispose();
        }

        return image;
    }
}
