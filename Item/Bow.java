package item;

import entity.Arrow;
import entity.Enemy;
import entity.Player;

import java.awt.Color;
import java.util.List;

public final class Bow extends Weapon {
    public static final int ARROW_DAMAGE = 2;
    public static final double ARROW_SPEED = 14.0;

    @Override
    public String getName() {
        return "Bow";
    }

    @Override
    public Color getColor() {
        return new Color(160, 110, 60);
    }

    @Override
    public boolean isRanged() {
        return true;
    }

    @Override
    public int getMeleeDamage() {
        return 1;
    }

    @Override
    public void rangedAttack(Player player, List<Enemy> enemies, List<Arrow> arrows, double targetWorldX, double targetWorldY) {
        double dx = targetWorldX - player.getX();
        double dy = targetWorldY - player.getY();
        double len = Math.hypot(dx, dy);
        if (len < 0.001) {
            return;
        }
        arrows.add(new Arrow(
                player.getX(),
                player.getY(),
                dx / len * ARROW_SPEED,
                dy / len * ARROW_SPEED,
                ARROW_DAMAGE
        ));
    }
}
