package item;

import dungeon.Dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ItemSpawner {
    public static final double WEAPON_SPAWN_CHANCE = 0.5;

    private ItemSpawner() {
    }

    public static List<MapPickup> spawn(Dungeon dungeon, Random rng, int spawnX, int spawnY, int exitX, int exitY) {
        List<MapPickup> pickups = new ArrayList<>();

        if (rng.nextDouble() < WEAPON_SPAWN_CHANCE) {
            double[] pos = randomTile(dungeon, rng, spawnX, spawnY, exitX, exitY, 5);
            if (pos != null) {
                Weapon weapon = rng.nextBoolean() ? new Sword() : new Bow();
                pickups.add(MapPickup.weapon(pos[0], pos[1], weapon));
            }
        }

        double[] armorPos = randomTile(dungeon, rng, spawnX, spawnY, exitX, exitY, 4);
        if (armorPos != null) {
            pickups.add(MapPickup.armor(armorPos[0], armorPos[1], new PlateArmor()));
        }

        double[] foodPos = randomTile(dungeon, rng, spawnX, spawnY, exitX, exitY, 4);
        if (foodPos != null) {
            pickups.add(MapPickup.food(foodPos[0], foodPos[1], new Ration()));
        }

        double[] potionPos = randomTile(dungeon, rng, spawnX, spawnY, exitX, exitY, 4);
        if (potionPos != null) {
            pickups.add(MapPickup.potion(potionPos[0], potionPos[1], new SpeedPotion()));
        }

        return pickups;
    }

    private static double[] randomTile(Dungeon dungeon, Random rng, int spawnX, int spawnY, int exitX, int exitY, int minDist) {
        for (int attempt = 0; attempt < 300; attempt++) {
            int x = rng.nextInt(dungeon.floor[0].length);
            int y = rng.nextInt(dungeon.floor.length);
            if (!dungeon.walkable(x, y)) {
                continue;
            }
            if (tooClose(x, y, spawnX, spawnY, minDist) || tooClose(x, y, exitX, exitY, 3)) {
                continue;
            }
            return new double[] {x + 0.5, y + 0.5};
        }
        return null;
    }

    private static boolean tooClose(int x, int y, int tx, int ty, int minDist) {
        int dx = x - tx;
        int dy = y - ty;
        return dx * dx + dy * dy < minDist * minDist;
    }
}
