package entity;

import java.awt.Color;

public final class Slime extends Enemy {
    public Slime(double x, double y) {
        super(x, y, 3);
    }

    @Override
    public String getName() {
        return "Slime";
    }

    @Override
    public Color getColor() {
        return new Color(80, 200, 120);
    }

    @Override
    public double getSpeed() {
        return 2.0;
    }

    @Override
    public int getTouchDamage() {
        return 8;
    }

    @Override
    public int getSize() {
        return 14;
    }
}
