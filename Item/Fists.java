package item;

import java.awt.Color;

public final class Fists extends Weapon {
    @Override
    public String getName() {
        return "Fists";
    }

    @Override
    public Color getColor() {
        return new Color(200, 180, 140);
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public int getMeleeDamage() {
        return 1;
    }
}
