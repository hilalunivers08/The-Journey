package thejourney.world;

public record CollisionBox(double x, double y, double width, double height) {

    public CollisionBox {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("collision box position must be finite");
        }
        if (!Double.isFinite(width) || !Double.isFinite(height) || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("collision box size must be positive");
        }
    }

    public double right() {
        return x + width;
    }

    public double bottom() {
        return y + height;
    }

    public boolean intersects(CollisionBox other) {
        return x < other.right()
                && right() > other.x()
                && y < other.bottom()
                && bottom() > other.y();
    }

    public boolean intersects(double otherX, double otherY, double otherWidth, double otherHeight) {
        return x < otherX + otherWidth
                && right() > otherX
                && y < otherY + otherHeight
                && bottom() > otherY;
    }
}
