package item;

import java.awt.Color;

public final class PlateArmor extends Armor {
    @Override
    public String getName() {
        return "Plate Armor";
    }

    @Override
    public Color getColor() {
        return new Color(120, 140, 200);
    }
}
