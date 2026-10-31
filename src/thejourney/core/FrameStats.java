package thejourney.core;

public final class FrameStats {

    private static final long WINDOW_NANOS = 1_000_000_000L;

    private long windowStart = System.nanoTime();
    private int frames;
    private int updates;

    private volatile int fps;
    private volatile int ups;

    public synchronized void recordFrame() {
        frames++;
        rollWindow(System.nanoTime());
    }

    public synchronized void recordUpdate() {
        updates++;
        rollWindow(System.nanoTime());
    }

    private void rollWindow(long now) {
        long elapsed = now - windowStart;
        if (elapsed < WINDOW_NANOS) {
            return;
        }

        fps = (int) Math.round(frames * (double) WINDOW_NANOS / elapsed);
        ups = (int) Math.round(updates * (double) WINDOW_NANOS / elapsed);
        frames = 0;
        updates = 0;
        windowStart = now;
    }

    public int getFps() {
        return fps;
    }

    public int getUps() {
        return ups;
    }
}
