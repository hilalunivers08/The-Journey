package thejourney.input;

public record PlayerInput(double moveX, double moveY) {

    public static final PlayerInput NONE = new PlayerInput(0, 0);

    public PlayerInput {
        if (!Double.isFinite(moveX) || !Double.isFinite(moveY)) {
            throw new IllegalArgumentException("Player input must be finite");
        }
    }
}
