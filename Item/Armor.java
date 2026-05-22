package item;

import entity.Player;

import java.awt.Color;

public abstract class Armor {
    public static final int BONUS_PER_PICKUP = 50;

    public abstract String getName();

    public abstract Color getColor();

    public int getArmorBonus() {
        return BONUS_PER_PICKUP;
    }

    public void onPickup(Player player) {
        player.addArmor(getArmorBonus());
    }
}
