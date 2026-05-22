package entity;

import java.awt.Color;

public final class Rat extends Enemy {
    public Rat(double x, double y) {
        super(x, y, 2);
    }

    @Override
    public String getName() {
        return "Rat";
    }

    @Override
    public Color getColor() {
        return new Color(140, 110, 90);
    }

    @Override
    public double getSpeed() {
        return 4.5;
    }

    @Override
    public int getTouchDamage() {
        return 10;
    }

    @Override
    public int getSize() {
        return 12;
    }
}
