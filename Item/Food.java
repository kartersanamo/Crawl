package item;

import entity.Player;

import java.awt.Color;

public abstract class Food {
    public abstract String getName();

    public abstract Color getColor();

    public void onPickup(Player player) {
        player.healToFull();
    }
}
