package thejourney.graphics;

public final class Animation {

    private final Sprite[] frames;
    private final double frameDuration;
    private final boolean looping;

    private double elapsed;
    private int frameIndex;

    public Animation(Sprite[] frames, double frameDuration, boolean looping) {
        if (frames == null || frames.length == 0) {
            throw new IllegalArgumentException("animation requires at least one frame");
        }
        if (!Double.isFinite(frameDuration) || frameDuration <= 0) {
            throw new IllegalArgumentException("frame duration must be positive");
        }

        this.frames = frames.clone();
        this.frameDuration = frameDuration;
        this.looping = looping;
    }

    public void update(double deltaTime) {
        if (!Double.isFinite(deltaTime) || deltaTime < 0) {
            throw new IllegalArgumentException("delta time must be finite and non-negative");
        }
        if (frames.length == 1) {
            return;
        }
        if (!looping && frameIndex == frames.length - 1) {
            return;
        }

        elapsed += deltaTime;

        while (elapsed >= frameDuration) {
            elapsed -= frameDuration;

            if (frameIndex + 1 < frames.length) {
                frameIndex++;
            } else if (looping) {
                frameIndex = 0;
            } else {
                frameIndex = frames.length - 1;
                elapsed = 0;
                break;
            }
        }
    }

    public void reset() {
        elapsed = 0;
        frameIndex = 0;
    }

    public Sprite getCurrentFrame() {
        return frames[frameIndex];
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public int getFrameCount() {
        return frames.length;
    }
}
