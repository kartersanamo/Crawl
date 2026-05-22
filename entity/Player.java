package entity;

import dungeon.Dungeon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public final class Player {
    public static final double MOVE_SPEED = 10.0;
    public static final double RADIUS = 0.35;
    public static final int SIZE = 14;
    public static final Color COLOR = new Color(240, 210, 90);

    private double x;
    private double y;
    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Player(int spawnTileX, int spawnTileY) {
        this(spawnTileX + 0.5, spawnTileY + 0.5);
    }

    public void spawnAt(int tileX, int tileY) {
        x = tileX + 0.5;
        y = tileY + 0.5;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getTileX() {
        return (int) Math.floor(x);
    }

    public int getTileY() {
        return (int) Math.floor(y);
    }

    public boolean isOnTile(int tileX, int tileY) {
        return getTileX() == tileX && getTileY() == tileY;
    }

    public void setKey(int keyCode, boolean pressed) {
        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = pressed;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = pressed;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = pressed;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = pressed;
            default -> {
            }
        }
    }

    /** @return true if the player moved this tick */
    public boolean update(double dt, Dungeon dungeon) {
        double dx = 0;
        double dy = 0;
        if (right) {
            dx += 1;
        }
        if (left) {
            dx -= 1;
        }
        if (down) {
            dy += 1;
        }
        if (up) {
            dy -= 1;
        }
        if (dx == 0 && dy == 0) {
            return false;
        }

        double len = Math.hypot(dx, dy);
        dx = dx / len * MOVE_SPEED * dt;
        dy = dy / len * MOVE_SPEED * dt;
        return move(dx, dy, dungeon);
    }

    private boolean move(double dx, double dy, Dungeon dungeon) {
        boolean moved = false;
        if (!blocked(x + dx, y, dungeon)) {
            x += dx;
            moved = true;
        }
        if (!blocked(x, y + dy, dungeon)) {
            y += dy;
            moved = true;
        }
        return moved;
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

    public void drawCentered(Graphics g, int panelWidth, int panelHeight) {
        int screenX = panelWidth / 2 - SIZE / 2;
        int screenY = panelHeight / 2 - SIZE / 2;
        g.setColor(COLOR);
        g.fillRect(screenX, screenY, SIZE, SIZE);
    }
}
