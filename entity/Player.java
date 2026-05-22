package entity;

import dungeon.Dungeon;
import item.Fists;
import item.Weapon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;

public final class Player {
    public static final double MOVE_SPEED = 10.0;
    public static final double RADIUS = 0.35;
    public static final int SIZE = 16;
    public static final Color COLOR = new Color(240, 210, 90);
    public static final int MAX_HEALTH = 100;
    public static final int MAX_ARMOR = 100;
    public static final double ATTACK_RANGE = 1.1;
    public static final double INVULN_SECONDS = 0.6;
    public static final double MELEE_HIT_COOLDOWN = 1.0;
    public static final double BOW_FIRE_COOLDOWN = 1.0;

    private double x;
    private double y;
    private int health = MAX_HEALTH;
    private int armor;
    private double invulnTimer;
    private double meleeHitCooldown;
    private double bowCooldown;
    private double speedMultiplier = 1.0;
    private double potionTimer;
    private Weapon weapon = new Fists();
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

    public void resetForRound() {
        health = MAX_HEALTH;
        armor = 0;
        invulnTimer = 0;
        speedMultiplier = 1.0;
        potionTimer = 0;
    }

    /** New floor: keep weapon, HP, and armor; only clear potion buffs. */
    public void enterNextFloor() {
        invulnTimer = 0;
        speedMultiplier = 1.0;
        potionTimer = 0;
    }

    /** Melee can swing freely until a hit connects; then this blocks for 1s. */
    public boolean canMeleeAttack() {
        return meleeHitCooldown <= 0;
    }

    public void triggerMeleeHitCooldown() {
        meleeHitCooldown = MELEE_HIT_COOLDOWN;
    }

    public boolean canFireBow() {
        return bowCooldown <= 0;
    }

    public void triggerBowCooldown() {
        bowCooldown = BOW_FIRE_COOLDOWN;
    }

    /** Death restart: back to fists, no armor, no active effects. */
    public void resetOnDeath() {
        health = MAX_HEALTH;
        armor = 0;
        invulnTimer = 0;
        meleeHitCooldown = 0;
        bowCooldown = 0;
        speedMultiplier = 1.0;
        potionTimer = 0;
        weapon = new Fists();
        up = false;
        down = false;
        left = false;
        right = false;
    }

    public int getHealth() {
        return health;
    }

    public int getArmor() {
        return armor;
    }

    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    public int getMaxArmor() {
        return MAX_ARMOR;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void equipWeapon(Weapon newWeapon) {
        weapon = newWeapon;
    }

    public void addArmor(int amount) {
        armor = Math.min(MAX_ARMOR, armor + amount);
    }

    public void healToFull() {
        health = MAX_HEALTH;
    }

    public void applySpeedBoost(double multiplier, double durationSeconds) {
        speedMultiplier = multiplier;
        potionTimer = durationSeconds;
    }

    public boolean isAlive() {
        return health > 0;
    }

    /** @return total HP + armor lost */
    public int takeDamage(int amount) {
        if (invulnTimer > 0 || !isAlive()) {
            return 0;
        }

        int totalLost = 0;
        int remaining = amount;
        if (armor > 0) {
            int absorbed = Math.min(armor, remaining);
            armor -= absorbed;
            remaining -= absorbed;
            totalLost += absorbed;
        }
        if (remaining > 0) {
            int healthLost = Math.min(health, remaining);
            health -= healthLost;
            totalLost += healthLost;
        }
        invulnTimer = INVULN_SECONDS;
        return totalLost;
    }

    public void tickTimers(double dt) {
        if (invulnTimer > 0) {
            invulnTimer = Math.max(0, invulnTimer - dt);
        }
        if (meleeHitCooldown > 0) {
            meleeHitCooldown = Math.max(0, meleeHitCooldown - dt);
        }
        if (bowCooldown > 0) {
            bowCooldown = Math.max(0, bowCooldown - dt);
        }
        if (potionTimer > 0) {
            potionTimer = Math.max(0, potionTimer - dt);
            if (potionTimer == 0) {
                speedMultiplier = 1.0;
            }
        }
    }

    public boolean tryMeleeAttack(List<Enemy> enemies) {
        if (weapon.isRanged()) {
            return false;
        }
        int before = livingCount(enemies);
        weapon.meleeAttack(this, enemies);
        return livingCount(enemies) != before || !weapon.isRanged();
    }

    public void fireRanged(List<Enemy> enemies, List<Arrow> arrows, double targetWorldX, double targetWorldY) {
        if (weapon.isRanged()) {
            weapon.rangedAttack(this, enemies, arrows, targetWorldX, targetWorldY);
        }
    }

    private int livingCount(List<Enemy> enemies) {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                count++;
            }
        }
        return count;
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

        double speed = MOVE_SPEED * speedMultiplier;
        double len = Math.hypot(dx, dy);
        dx = dx / len * speed * dt;
        dy = dy / len * speed * dt;
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
        g.setColor(invulnTimer > 0 ? new Color(255, 180, 180) : COLOR);
        g.fillRect(screenX, screenY, SIZE, SIZE);
    }

    public void drawHealthBar(Graphics g, int x, int y, int width, int height) {
        g.setColor(new Color(40, 40, 40));
        g.fillRect(x, y, width, height);
        int fill = (int) (width * (health / (double) MAX_HEALTH));
        g.setColor(health > 30 ? new Color(60, 180, 80) : new Color(200, 50, 50));
        g.fillRect(x, y, fill, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
        g.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 11));
        g.drawString(health + " / " + MAX_HEALTH, x + 6, y + height - 5);
    }

    public void drawArmorBar(Graphics g, int x, int y, int width, int height) {
        g.setColor(new Color(40, 40, 40));
        g.fillRect(x, y, width, height);
        int fill = (int) (width * (armor / (double) MAX_ARMOR));
        g.setColor(new Color(100, 140, 220));
        g.fillRect(x, y, fill, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
        g.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 11));
        g.drawString(armor + " / " + MAX_ARMOR, x + 6, y + height - 5);
    }
}
