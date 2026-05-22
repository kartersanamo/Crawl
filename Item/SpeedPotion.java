package item;

import entity.Player;

import java.awt.Color;

public final class SpeedPotion extends Potion {
    public static final double DURATION_SECONDS = 8.0;
    public static final double SPEED_MULTIPLIER = 2.0;

    @Override
    public String getName() {
        return "Speed Potion";
    }

    @Override
    public Color getColor() {
        return new Color(100, 200, 255);
    }

    @Override
    public void apply(Player player) {
        player.applySpeedBoost(SPEED_MULTIPLIER, DURATION_SECONDS);
    }
}
