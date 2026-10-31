package thejourney.entity;

import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import thejourney.Constants;
import thejourney.graphics.Animation;
import thejourney.graphics.AssetManager;
import thejourney.graphics.Sprite;
import thejourney.graphics.SpriteSheet;
import thejourney.input.PlayerInput;
import thejourney.world.Renderable;

public final class Player extends LivingEntity implements Renderable {

    private static final double IDLE_FRAME_DURATION = 0.55;
    private static final double WALK_FRAME_DURATION = 0.12;
    private static final int IDLE_FRAME_COUNT = 2;
    private static final int WALK_FIRST_COLUMN = 2;
    private static final double MIN_MOVE_LENGTH = 1e-6;

    private final Map<Facing, Animation> idleAnimations;
    private final Map<Facing, Animation> walkAnimations;

    private PlayerInput input = PlayerInput.NONE;
    private Facing facing = Facing.DOWN;
    private PlayerState state = PlayerState.IDLE;
    private Animation animation;
    private double pendingMoveX;
    private double pendingMoveY;

    public Player(AssetManager assets) {
        super(
                100,
                100,
                Constants.TILE_SIZE,
                Constants.TILE_SIZE,
                Constants.PLAYER_SPEED,
                Constants.PLAYER_MAX_HP
        );

        setCollisionBox(6, 18, 20, 14);

        SpriteSheet sheet = assets.getPlayerSheet();
        this.idleAnimations = createAnimations(sheet, true);
        this.walkAnimations = createAnimations(sheet, false);
        this.animation = idleAnimations.get(facing);
    }

    @Override
    public void update(double deltaTime) {
        syncPreviousPosition();

        pendingMoveX = 0;
        pendingMoveY = 0;

        double moveX = clamp(input.moveX(), -1, 1);
        double moveY = clamp(input.moveY(), -1, 1);
        double length = Math.hypot(moveX, moveY);

        if (length > 1) {
            moveX /= length;
            moveY /= length;
            length = 1;
        }

        if (length <= MIN_MOVE_LENGTH) {
            setState(PlayerState.IDLE);
        } else {
            setState(PlayerState.WALK);
            setFacing(resolveFacing(moveX, moveY));

            pendingMoveX = moveX * getSpeed() * deltaTime;
            pendingMoveY = moveY * getSpeed() * deltaTime;
        }

        animation.update(deltaTime);
    }

    @Override
    public void render(Graphics2D g2, double alpha) {
        int drawX = (int) Math.round(getInterpolatedX(alpha));
        int drawY = (int) Math.round(getInterpolatedY(alpha));

        animation.getCurrentFrame().draw(g2, drawX, drawY);
    }

    public void setInput(PlayerInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }

        this.input = input;
    }

    public PlayerState getState() {
        return state;
    }

    public Facing getFacing() {
        return facing;
    }

    public Sprite getCurrentSprite() {
        return animation.getCurrentFrame();
    }

    public int getAnimationFrameIndex() {
        return animation.getFrameIndex();
    }

    public void face(Facing next) {
        setFacing(next);
    }

    public double consumeMoveX() {
        double value = pendingMoveX;
        pendingMoveX = 0;
        return value;
    }

    public double consumeMoveY() {
        double value = pendingMoveY;
        pendingMoveY = 0;
        return value;
    }

    @Override
    public double getSortY() {
        return getY() + getHeight();
    }

    private void setState(PlayerState next) {
        if (state == next) {
            return;
        }

        state = next;
        selectAnimation();
    }

    private void setFacing(Facing next) {
        if (facing == next) {
            return;
        }

        facing = next;
        selectAnimation();
    }

    private void selectAnimation() {
        Map<Facing, Animation> source = state == PlayerState.WALK
                ? walkAnimations
                : idleAnimations;
        Animation next = source.get(facing);

        if (animation == next) {
            return;
        }

        animation = next;
        animation.reset();
    }

    private static Facing resolveFacing(double moveX, double moveY) {
        if (moveX != 0 && Math.abs(moveX) >= Math.abs(moveY)) {
            return moveX < 0 ? Facing.LEFT : Facing.RIGHT;
        }

        return moveY < 0 ? Facing.UP : Facing.DOWN;
    }

    private static Map<Facing, Animation> createAnimations(SpriteSheet sheet, boolean idle) {
        Map<Facing, Animation> animations = new EnumMap<>(Facing.class);

        for (Facing facing : Facing.values()) {
            int row = facing.ordinal();

            if (idle) {
                Sprite[] frames = new Sprite[IDLE_FRAME_COUNT];
                for (int column = 0; column < IDLE_FRAME_COUNT; column++) {
                    frames[column] = sheet.getSprite(column, row);
                }

                animations.put(facing, new Animation(frames, IDLE_FRAME_DURATION, true));
            } else {
                int frameCount = sheet.getColumns() - WALK_FIRST_COLUMN;
                Sprite[] frames = new Sprite[frameCount];
                for (int column = 0; column < frameCount; column++) {
                    frames[column] = sheet.getSprite(WALK_FIRST_COLUMN + column, row);
                }

                animations.put(facing, new Animation(frames, WALK_FRAME_DURATION, true));
            }
        }

        return animations;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
