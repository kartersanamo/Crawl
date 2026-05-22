package item;

import java.awt.Color;

public final class Ration extends Food {
    @Override
    public String getName() {
        return "Ration";
    }

    @Override
    public Color getColor() {
        return new Color(210, 160, 80);
    }
}
