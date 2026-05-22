package entity;

import dungeon.Dungeon;

import java.awt.Color;
import java.awt.Graphics;

public abstract class Enemy {
    public static final double RADIUS = 0.35;

    protected double x;
    protected double y;
    protected int hp;

    protected Enemy(double x, double y, int hp) {
        this.x = x;
        this.y = y;
        this.hp = hp;
    }

    public abstract String getName();
    public abstract Color getColor();
    public abstract double getSpeed();
    public abstract int getTouchDamage();
    public abstract int getSize();

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getHp() {
        return hp;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int amount) {
        hp -= amount;
    }

    public boolean touches(Player player) {
        double dist = Math.hypot(player.getX() - x, player.getY() - y);
        return dist < Player.RADIUS + RADIUS;
    }

    public void update(double dt, Dungeon dungeon, Player player) {
        if (!isAlive()) {
            return;
        }

        double dx = player.getX() - x;
        double dy = player.getY() - y;
        double len = Math.hypot(dx, dy);
        if (len < 0.001) {
            return;
        }

        dx = dx / len * getSpeed() * dt;
        dy = dy / len * getSpeed() * dt;
        move(dx, 0, dungeon);
        move(0, dy, dungeon);
    }

    private void move(double dx, double dy, Dungeon dungeon) {
        if (!blocked(x + dx, y, dungeon)) {
            x += dx;
        }
        if (!blocked(x, y + dy, dungeon)) {
            y += dy;
        }
    }

    private boolean blocked(double cx, double cy, Dungeon dungeon) {
        int minX = (int) Math.floor(cx - RADIUS);
        int maxX = (int) Math.floor(cx + RADIUS);
        int minY = (int) Math.floor(cy - RADIUS);
        int maxY = (int) Math.floor(cy + RADIUS);
        for (int ty = minY; ty <= maxY; ty++) {
            for (int tx = minX; tx <= maxX; tx++) {
                if (!dungeon.walkable(tx, ty)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void draw(Graphics g, double camX, double camY, int tileSize) {
        if (!isAlive()) {
            return;
        }
        int screenX = (int) Math.round(x * tileSize - camX) - getSize() / 2;
        int screenY = (int) Math.round(y * tileSize - camY) - getSize() / 2;
        g.setColor(getColor());
        g.fillRect(screenX, screenY, getSize(), getSize());
    }
}
