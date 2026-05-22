package entity;

import java.awt.Color;

public final class Goblin extends Enemy {
    public Goblin(double x, double y) {
        super(x, y, 2);
    }

    @Override
    public String getName() {
        return "Goblin";
    }

    @Override
    public Color getColor() {
        return new Color(200, 70, 70);
    }

    @Override
    public double getSpeed() {
        return 3.5;
    }

    @Override
    public int getTouchDamage() {
        return 15;
    }

    @Override
    public int getSize() {
        return 14;
    }
}
