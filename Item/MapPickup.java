package item;

import entity.Player;

import java.awt.Color;
import java.awt.Graphics;

public final class MapPickup {
    public enum Kind {
        ARMOR, FOOD, POTION, WEAPON
    }

    private final double x;
    private final double y;
    private final Kind kind;
    private final Armor armor;
    private final Food food;
    private final Potion potion;
    private final Weapon weapon;

    private MapPickup(double x, double y, Kind kind, Armor armor, Food food, Potion potion, Weapon weapon) {
        this.x = x;
        this.y = y;
        this.kind = kind;
        this.armor = armor;
        this.food = food;
        this.potion = potion;
        this.weapon = weapon;
    }

    public static MapPickup armor(double x, double y, Armor armor) {
        return new MapPickup(x, y, Kind.ARMOR, armor, null, null, null);
    }

    public static MapPickup food(double x, double y, Food food) {
        return new MapPickup(x, y, Kind.FOOD, null, food, null, null);
    }

    public static MapPickup potion(double x, double y, Potion potion) {
        return new MapPickup(x, y, Kind.POTION, null, null, potion, null);
    }

    public static MapPickup weapon(double x, double y, Weapon weapon) {
        return new MapPickup(x, y, Kind.WEAPON, null, null, null, weapon);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Kind getKind() {
        return kind;
    }

    public String getLabel() {
        return switch (kind) {
            case ARMOR -> armor.getName();
            case FOOD -> food.getName();
            case POTION -> potion.getName();
            case WEAPON -> weapon.getName();
        };
    }

    public Color getColor() {
        return switch (kind) {
            case ARMOR -> armor.getColor();
            case FOOD -> food.getColor();
            case POTION -> potion.getColor();
            case WEAPON -> weapon.getColor();
        };
    }

    public boolean touches(Player player) {
        return Math.hypot(player.getX() - x, player.getY() - y) < Player.RADIUS + 0.4;
    }

    public void collect(Player player) {
        switch (kind) {
            case ARMOR -> armor.onPickup(player);
            case FOOD -> food.onPickup(player);
            case POTION -> potion.apply(player);
            case WEAPON -> player.equipWeapon(weapon);
        }
    }

    public void draw(Graphics g, double camX, double camY, int tileSize) {
        int sx = (int) Math.round(x * tileSize - camX) - 5;
        int sy = (int) Math.round(y * tileSize - camY) - 5;
        g.setColor(getColor());
        g.fillOval(sx, sy, 10, 10);
        g.setColor(Color.WHITE);
        g.drawOval(sx, sy, 10, 10);
    }
}
