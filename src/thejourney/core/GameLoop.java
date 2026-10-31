package thejourney.core;

import java.util.concurrent.locks.LockSupport;

import thejourney.Constants;

public final class GameLoop implements Runnable {

    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    private final Game game;
    private final FrameStats stats;
    private final FrameRenderer renderer;

    private volatile boolean running;
    private volatile Thread thread;

    public GameLoop(Game game, FrameStats stats, FrameRenderer renderer) {
        this.game = game;
        this.stats = stats;
        this.renderer = renderer;
    }

    public void start() {
        if (running) {
            return;
        }

        running = true;
        thread = new Thread(this, "thejourney-game-loop");
        thread.start();
    }

    public void stop() {
        if (!running) {
            return;
        }

        running = false;

        Thread current = thread;
        if (current == null) {
            return;
        }

        current.interrupt();

        try {
            current.join(1000);
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

        try {
            while (running) {
                long frameStart = System.nanoTime();
                long elapsed = frameStart - lastTime;
                lastTime = frameStart;

                if (elapsed > maxFrameTime) {
                    elapsed = maxFrameTime;
                }

                accumulator += elapsed;

                while (accumulator >= updateInterval) {
                    game.update(1.0 / Constants.TARGET_UPS);
                    stats.recordUpdate();
                    accumulator -= updateInterval;
                }

                renderer.render(game, accumulator / updateInterval);

                long frameTime = System.nanoTime() - frameStart;
                long sleepTime = (long) updateInterval - frameTime;
                if (sleepTime > 0) {
                    LockSupport.parkNanos(sleepTime);
                }
            }
        } finally {
            running = false;
        }
    }
}
