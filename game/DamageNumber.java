package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

final class DamageNumber {
    static final double DURATION = 0.9;
    private static final Color DEALT = new Color(255, 255, 120);
    private static final Color TAKEN = new Color(255, 90, 90);

    private final double worldX;
    private final double worldY;
    private final String text;
    private final Color color;
    private double age;

    DamageNumber(double worldX, double worldY, int amount, boolean damageToPlayer) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.text = "-" + amount;
        this.color = damageToPlayer ? TAKEN : DEALT;
    }

    boolean update(double dt) {
        age += dt;
        return age < DURATION;
    }

    void draw(Graphics g, double camX, double camY, int tileSize) {
        float alpha = 1f - (float) (age / DURATION);
        int sx = (int) Math.round(worldX * tileSize - camX);
        int sy = (int) Math.round(worldY * tileSize - camY - age * 28);
        Color fade = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * alpha));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        g.setColor(new Color(0, 0, 0, (int) (120 * alpha)));
        g.drawString(text, sx + 1, sy + 1);
        g.setColor(fade);
        g.drawString(text, sx, sy);
    }
}
