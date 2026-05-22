package item;

import entity.Player;

import java.awt.Color;

public abstract class Potion {
    public abstract String getName();

    public abstract Color getColor();

    public abstract void apply(Player player);
}
