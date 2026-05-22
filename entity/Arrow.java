package entity;

import dungeon.Dungeon;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

public final class Arrow {
    private static final double RADIUS = 0.15;

    private double x;
    private double y;
    private final double vx;
    private final double vy;
    private final int damage;
    private boolean active = true;

    public Arrow(double x, double y, double vx, double vy, int damage) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
    }

    public boolean isActive() {
        return active;
    }

    public HitResult update(double dt, Dungeon dungeon, List<Enemy> enemies) {
        if (!active) {
            return null;
        }

        x += vx * dt;
        y += vy * dt;

        if (!dungeon.walkable((int) Math.floor(x), (int) Math.floor(y))) {
            active = false;
            return null;
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            if (Math.hypot(enemy.getX() - x, enemy.getY() - y) < Enemy.RADIUS + RADIUS) {
                int dealt = enemy.takeDamage(damage);
                active = false;
                return dealt > 0 ? new HitResult(enemy, dealt) : null;
            }
        }
        return null;
    }

    public void draw(Graphics g, double camX, double camY, int tileSize) {
        if (!active) {
            return;
        }
        int sx = (int) Math.round(x * tileSize - camX) - 2;
        int sy = (int) Math.round(y * tileSize - camY) - 2;
        g.setColor(new Color(220, 220, 100));
        g.fillOval(sx, sy, 5, 5);
    }

    public static final class HitResult {
        public final Enemy enemy;
        public final int damage;

        HitResult(Enemy enemy, int damage) {
            this.enemy = enemy;
            this.damage = damage;
        }
    }
}
