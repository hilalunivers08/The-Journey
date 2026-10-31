package thejourney;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import thejourney.core.FrameStats;
import thejourney.core.Game;
import thejourney.entity.Facing;
import thejourney.entity.LivingEntity;
import thejourney.entity.Player;
import thejourney.entity.PlayerState;
import thejourney.graphics.Animation;
import thejourney.graphics.AssetManager;
import thejourney.graphics.Sprite;
import thejourney.graphics.SpriteSheet;
import thejourney.input.InputState;
import thejourney.world.Camera;
import thejourney.world.CollisionSystem;
import thejourney.world.MapLoader;
import thejourney.world.MapObjectType;
import thejourney.world.TileLayer;
import thejourney.world.TileMap;
import thejourney.world.TileType;

public final class SmokeTest {

    private static final double DELTA_TIME = 1.0 / 60.0;
    private static final double EPSILON = 1e-6;

    private SmokeTest() {
    }

    public static void main(String[] args) {
        testIdle();
        testHorizontalMovement();
        testDiagonalMovement();
        testInterpolation();
        testPause();
        testFocusLoss();
        testPlayerStateAndFacing();
        testPlayerAnimation();
        testAnimationLooping();
        testAnimationNonLooping();
        testLivingEntityHealth();
        testAssetManager();
        testWorldLoading();
        testMapParsing();
        testTileCollision();
        testCamera();
        testCameraFollowsPlayer();
        testWorldRendering();

        System.out.println("SmokeTest OK");
    }

    private static void testIdle() {
        Game game = newGame(new InputState());

        updateFor(game, 60);

        assertNear(100.0, game.getPlayer().getX(), "idle X");
        assertNear(100.0, game.getPlayer().getY(), "idle Y");
        assertPlayerState(PlayerState.IDLE, game, "idle state");
        assertFacing(Facing.DOWN, game, "idle facing");
    }

    private static void testHorizontalMovement() {
        InputState input = new InputState();
        Game game = newGame(input);

        input.press(KeyEvent.VK_D);
        updateFor(game, 60);

        assertNear(220.0, game.getPlayer().getX(), "horizontal X");
        assertNear(100.0, game.getPlayer().getY(), "horizontal Y");
        assertPlayerState(PlayerState.WALK, game, "walk state");
        assertFacing(Facing.RIGHT, game, "walk facing");
    }

    private static void testDiagonalMovement() {
        InputState input = new InputState();
        Game game = newGame(input);

        input.press(KeyEvent.VK_W);
        input.press(KeyEvent.VK_D);
        updateFor(game, 60);

        double diagonalDistance = 120.0 / Math.sqrt(2.0);
        assertNear(100.0 + diagonalDistance, game.getPlayer().getX(), "diagonal X");
        assertNear(100.0 - diagonalDistance, game.getPlayer().getY(), "diagonal Y");
    }

    private static void testInterpolation() {
        InputState input = new InputState();
        Game game = newGame(input);

        input.press(KeyEvent.VK_D);
        game.update(DELTA_TIME);

        assertNear(101.0, game.getPlayer().getInterpolatedX(0.5), "interpolated X");
        assertNear(100.0, game.getPlayer().getInterpolatedY(0.5), "interpolated Y");
    }

    private static void testPause() {
        InputState input = new InputState();
        Game game = newGame(input);

        input.press(KeyEvent.VK_ESCAPE);
        game.update(DELTA_TIME);
        assertState(GameState.PAUSED, game, "pause");

        double pausedX = game.getPlayer().getX();
        input.press(KeyEvent.VK_D);
        updateFor(game, 30);
        assertNear(pausedX, game.getPlayer().getX(), "paused movement");
        assertPlayerState(PlayerState.IDLE, game, "paused player state");

        input.release(KeyEvent.VK_ESCAPE);
        input.press(KeyEvent.VK_ESCAPE);
        game.update(DELTA_TIME);
        assertState(GameState.PLAYING, game, "resume");
    }

    private static void testFocusLoss() {
        InputState input = new InputState();
        Game game = newGame(input);

        input.press(KeyEvent.VK_D);
        game.onFocusLost();

        assertState(GameState.PAUSED, game, "focus loss pause");
        assertPlayerState(PlayerState.IDLE, game, "focus loss player state");
        assertTrue(!input.isDown(KeyEvent.VK_D), "focus loss clears input");
    }

    private static void testPlayerStateAndFacing() {
        InputState input = new InputState();
        Game game = newGame(input);

        input.press(KeyEvent.VK_A);
        game.update(DELTA_TIME);
        assertPlayerState(PlayerState.WALK, game, "left walk state");
        assertFacing(Facing.LEFT, game, "left facing");

        input.release(KeyEvent.VK_A);
        input.press(KeyEvent.VK_W);
        game.update(DELTA_TIME);
        assertFacing(Facing.UP, game, "up facing");

        input.release(KeyEvent.VK_W);
        input.press(KeyEvent.VK_D);
        game.update(DELTA_TIME);
        assertFacing(Facing.RIGHT, game, "right facing");

        input.release(KeyEvent.VK_D);
        input.press(KeyEvent.VK_S);
        game.update(DELTA_TIME);
        assertFacing(Facing.DOWN, game, "down facing");

        input.release(KeyEvent.VK_S);
        game.update(DELTA_TIME);
        assertPlayerState(PlayerState.IDLE, game, "idle after release");
    }

    private static void testPlayerAnimation() {
        InputState input = new InputState();
        Game game = newGame(input);

        input.press(KeyEvent.VK_D);
        game.update(DELTA_TIME);
        assertEqualsInt(0, game.getPlayer().getAnimationFrameIndex(), "walk first frame");

        updateFor(game, 10);
        assertEqualsInt(1, game.getPlayer().getAnimationFrameIndex(), "walk second frame");

        input.release(KeyEvent.VK_D);
        game.update(DELTA_TIME);
        assertEqualsInt(0, game.getPlayer().getAnimationFrameIndex(), "idle resets frame");
    }

    private static void testAnimationLooping() {
        Animation animation = newAnimation(true);

        animation.update(0.1);
        assertEqualsInt(1, animation.getFrameIndex(), "loop frame 1");
        animation.update(0.1);
        assertEqualsInt(2, animation.getFrameIndex(), "loop frame 2");
        animation.update(0.1);
        assertEqualsInt(3, animation.getFrameIndex(), "loop frame 3");
        animation.update(0.1);
        assertEqualsInt(0, animation.getFrameIndex(), "loop wraps to 0");
    }

    private static void testAnimationNonLooping() {
        Animation animation = newAnimation(false);

        animation.update(1.0);
        assertEqualsInt(3, animation.getFrameIndex(), "non-loop clamps to last frame");

        animation.update(1.0);
        assertEqualsInt(3, animation.getFrameIndex(), "non-loop stays at last frame");
    }

    private static void testLivingEntityHealth() {
        DummyLiving entity = new DummyLiving();

        assertEqualsInt(50, entity.getMaxHp(), "max hp");
        assertEqualsInt(50, entity.getHp(), "starting hp");
        assertTrue(entity.isAlive(), "alive at start");

        assertEqualsInt(20, entity.takeDamage(20), "damage applied");
        assertEqualsInt(30, entity.getHp(), "hp after damage");

        assertEqualsInt(30, entity.takeDamage(100), "overkill damage applied");
        assertEqualsInt(0, entity.getHp(), "hp after overkill");
        assertTrue(!entity.isAlive(), "dead after overkill");

        entity.heal(10);
        assertEqualsInt(10, entity.getHp(), "hp after heal");
        entity.heal(1000);
        assertEqualsInt(50, entity.getHp(), "heal clamped to max");
    }

    private static void testAssetManager() {
        AssetManager assets = new AssetManager();
        SpriteSheet sheet = assets.getPlayerSheet();

        assertEqualsInt(6, sheet.getColumns(), "player sheet columns");
        assertEqualsInt(4, sheet.getRows(), "player sheet rows");
        assertEqualsInt(32, sheet.getFrameWidth(), "player frame width");
        assertEqualsInt(32, sheet.getFrameHeight(), "player frame height");
    }

    private static void testWorldLoading() {
        TileMap map = MapLoader.loadDefault();

        assertEqualsInt(32, map.getWidth(), "map width");
        assertEqualsInt(24, map.getHeight(), "map height");
        assertTrue(map.getName().contains("Briarwatch"), "map name");
        assertNear(100.0, map.getSpawn().x(), "spawn x");
        assertNear(100.0, map.getSpawn().y(), "spawn y");
        assertTrue(map.getSpawn().facing() == Facing.DOWN, "spawn facing");

        assertTrue(map.getTile(TileLayer.GROUND, 3, 3) != null, "spawn tile exists");
        assertTrue(
                map.getTile(TileLayer.GROUND, 3, 3).type() == TileType.GRASS,
                "spawn tile is grass"
        );
        assertTrue(map.isSolid(25, 2), "water is solid");
        assertTrue(!map.isSolid(27, 2), "bridge is walkable");
        assertTrue(!map.getObjects().isEmpty(), "map objects loaded");

        TileMap fallback = MapLoader.load("missing-map-file");
        assertEqualsInt(32, fallback.getWidth(), "fallback map width");
        assertEqualsInt(24, fallback.getHeight(), "fallback map height");
    }

    private static void testMapParsing() {
        String text = """
                name=Tiny
                width=4
                height=3
                spawn=32,32

                [ground]
                gggg
                gppg
                gggg

                [objects]
                rock 1 1

                [portals]
                0 0 1 1 forest 64 64
                """;

        TileMap map = MapLoader.parse(text);

        assertEqualsInt(4, map.getWidth(), "parsed map width");
        assertEqualsInt(3, map.getHeight(), "parsed map height");
        assertEqualsInt(4, map.getPixelWidth() / Constants.TILE_SIZE, "parsed pixel width");
        assertTrue(
                map.getTile(TileLayer.GROUND, 1, 1).type() == TileType.PATH,
                "parsed path tile"
        );
        assertEqualsInt(1, map.getObjects().size(), "parsed object count");
        assertTrue(
                map.getObjects().get(0).getType() == MapObjectType.ROCK,
                "parsed object type"
        );
        assertEqualsInt(1, map.getPortals().size(), "parsed portal count");
        assertTrue(
                "forest".equals(map.getPortals().get(0).targetMap()),
                "parsed portal target"
        );
    }

    private static void testTileCollision() {
        TileMap map = MapLoader.loadDefault();
        CollisionSystem collision = new CollisionSystem();
        Player player = new Player(new AssetManager());

        player.setPosition(773, 80);
        collision.move(player, 10, 0, map);

        assertTrue(player.getX() > 773.0, "player advances to water edge");
        assertTrue(player.getX() < 775.0, "water blocks movement");
        assertTrue(player.getX() + 6 + 20 <= 800.0 + EPSILON, "player stays outside water");

        player.setPosition(868, 170);
        collision.move(player, 0, -32, map);

        assertNear(138.0, player.getY(), "bridge allows vertical movement");
    }

    private static void testCamera() {
        Camera camera = new Camera();

        camera.follow(100, 100, 1024, 768);
        assertEqualsInt(0, camera.getX(), "camera clamp left");
        assertEqualsInt(0, camera.getY(), "camera clamp top");

        camera.follow(700, 500, 1024, 768);
        assertEqualsInt(256, camera.getX(), "camera clamp right");
        assertEqualsInt(192, camera.getY(), "camera clamp bottom");
    }

    private static void testCameraFollowsPlayer() {
        InputState input = new InputState();
        Game game = newGame(input);

        input.press(KeyEvent.VK_D);
        updateFor(game, 200);

        assertNear(500.0, game.getPlayer().getX(), "camera follow player X");
        assertEqualsInt(132, game.getWorld().getCamera().getX(), "camera follows player");
    }

    private static void testWorldRendering() {
        Game game = newGame(new InputState());
        BufferedImage image = new BufferedImage(
                Constants.SCREEN_WIDTH,
                Constants.SCREEN_HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2 = image.createGraphics();
        try {
            game.render(g2, 0.5);
        } finally {
            g2.dispose();
        }

        int rgb = image.getRGB(40, 200);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        assertTrue(green > red && green > blue, "world ground rendered");
    }

    private static Animation newAnimation(boolean looping) {
        SpriteSheet sheet = new AssetManager().getPlayerSheet();
        Sprite[] frames = new Sprite[4];

        for (int i = 0; i < frames.length; i++) {
            frames[i] = sheet.getSprite(i, 0);
        }

        return new Animation(frames, 0.1, looping);
    }

    private static Game newGame(InputState input) {
        return new Game(input, new FrameStats());
    }

    private static void updateFor(Game game, int ticks) {
        for (int i = 0; i < ticks; i++) {
            game.update(DELTA_TIME);
        }
    }

    private static void assertState(GameState expected, Game game, String message) {
        if (game.getGameState() != expected) {
            throw new AssertionError(
                    message + ": expected " + expected + " but was " + game.getGameState()
            );
        }
    }

    private static void assertPlayerState(PlayerState expected, Game game, String message) {
        PlayerState actual = game.getPlayer().getState();
        if (actual != expected) {
            throw new AssertionError(
                    message + ": expected " + expected + " but was " + actual
            );
        }
    }

    private static void assertFacing(Facing expected, Game game, String message) {
        Facing actual = game.getPlayer().getFacing();
        if (actual != expected) {
            throw new AssertionError(
                    message + ": expected " + expected + " but was " + actual
            );
        }
    }

    private static void assertNear(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > EPSILON) {
            throw new AssertionError(
                    message + ": expected " + expected + " but was " + actual
            );
        }
    }

    private static void assertEqualsInt(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(
                    message + ": expected " + expected + " but was " + actual
            );
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class DummyLiving extends LivingEntity {

        private DummyLiving() {
            super(0, 0, 16, 16, 0, 50);
        }

        @Override
        public void update(double deltaTime) {
        }

        @Override
        public void render(Graphics2D g2, double alpha) {
        }
    }
}
