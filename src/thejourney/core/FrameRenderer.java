package thejourney.core;

@FunctionalInterface
public interface FrameRenderer {

    void render(Game game, double alpha);
}
