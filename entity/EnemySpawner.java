package entity;

import dungeon.Dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class EnemySpawner {
    public static final int BASE_COUNT = 2;
    public static final int PER_ROUND = 2;

    private EnemySpawner() {
    }

    public static List<Enemy> spawn(Dungeon dungeon, Random rng, int round, int spawnX, int spawnY, int exitX, int exitY) {
        int count = BASE_COUNT + round * PER_ROUND;
        List<Enemy> enemies = new ArrayList<>();
        int attempts = 0;
        while (enemies.size() < count && attempts < count * 200) {
            attempts++;
            int x = rng.nextInt(dungeon.floor[0].length);
            int y = rng.nextInt(dungeon.floor.length);
            if (!dungeon.walkable(x, y)) {
                continue;
            }
            if (tooClose(x, y, spawnX, spawnY, 6) || tooClose(x, y, exitX, exitY, 4)) {
                continue;
            }
            boolean crowded = false;
            for (Enemy e : enemies) {
                if (tooClose(x, y, (int) Math.floor(e.getX()), (int) Math.floor(e.getY()), 3)) {
                    crowded = true;
                    break;
                }
            }
            if (crowded) {
                continue;
            }
            enemies.add(createRandom(rng, round, x + 0.5, y + 0.5));
        }
        return enemies;
    }

    private static boolean tooClose(int x, int y, int tx, int ty, int minDist) {
        int dx = x - tx;
        int dy = y - ty;
        return dx * dx + dy * dy < minDist * minDist;
    }

    private static Enemy createRandom(Random rng, int round, double x, double y) {
        int roll = rng.nextInt(100);
        if (round >= 3 && roll < 25) {
            return new Goblin(x, y);
        }
        if (roll < 55) {
            return new Slime(x, y);
        }
        return new Rat(x, y);
    }
}
