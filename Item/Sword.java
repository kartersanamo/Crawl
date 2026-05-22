package item;

import java.awt.Color;

public final class Sword extends Weapon {
    @Override
    public String getName() {
        return "Sword";
    }

    @Override
    public Color getColor() {
        return new Color(200, 200, 220);
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public int getMeleeDamage() {
        return 2;
    }
}
