package item;

import entity.Arrow;
import entity.Enemy;
import entity.Player;

import java.awt.Color;
import java.util.List;

public abstract class Weapon {
    public abstract String getName();

    public abstract Color getColor();

    public abstract boolean isRanged();

    public abstract int getMeleeDamage();

    public void meleeAttack(Player player, List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            double dist = Math.hypot(enemy.getX() - player.getX(), enemy.getY() - player.getY());
            if (dist <= Player.ATTACK_RANGE + Enemy.RADIUS) {
                enemy.takeDamage(getMeleeDamage());
            }
        }
    }

    public void rangedAttack(Player player, List<Enemy> enemies, List<Arrow> arrows, double targetWorldX, double targetWorldY) {
    }
}
