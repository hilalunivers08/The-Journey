package thejourney.entity;

public abstract class LivingEntity extends Entity {

    private final int maxHp;
    private int hp;

    protected LivingEntity(
            double x,
            double y,
            int width,
            int height,
            double speed,
            int maxHp
    ) {
        super(x, y, width, height, speed);

        if (maxHp <= 0) {
            throw new IllegalArgumentException("maxHp must be positive");
        }

        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public int takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("damage must not be negative");
        }

        int applied = Math.min(hp, amount);
        hp -= applied;
        return applied;
    }

    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("heal amount must not be negative");
        }

        hp = Math.min(maxHp, hp + amount);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }
}
