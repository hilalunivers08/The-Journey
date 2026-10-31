package thejourney.world;

import java.awt.Graphics2D;

import thejourney.entity.Player;

public final class World {

    private final TileMap map;
    private final Player player;
    private final Camera camera;
    private final CollisionSystem collision;
    private final WorldRenderer renderer;

    public World(TileMap map, Player player) {
        if (map == null) {
            throw new IllegalArgumentException("map must not be null");
        }
        if (player == null) {
            throw new IllegalArgumentException("player must not be null");
        }

        this.map = map;
        this.player = player;
        this.camera = new Camera();
        this.collision = new CollisionSystem();
        this.renderer = new WorldRenderer(map);

        spawnPlayer();
    }

    public void update(double deltaTime) {
        player.update(deltaTime);
        collision.move(player, player.consumeMoveX(), player.consumeMoveY(), map);
        followPlayer();
    }

    public void render(Graphics2D g2, double alpha) {
        renderer.render(g2, camera, player, alpha);
    }

    public TileMap getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public Camera getCamera() {
        return camera;
    }

    private void spawnPlayer() {
        SpawnPoint spawn = map.getSpawn();

        player.setPosition(spawn.x(), spawn.y());
        player.face(spawn.facing());
        player.syncPreviousPosition();

        followPlayer();
    }

    private void followPlayer() {
        camera.follow(
                player.getX() + player.getWidth() / 2.0,
                player.getY() + player.getHeight() / 2.0,
                map.getPixelWidth(),
                map.getPixelHeight()
        );
    }
}
