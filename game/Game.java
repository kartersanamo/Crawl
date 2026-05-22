package game;

import dungeon.Dungeon;
import entity.Player;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public final class Game extends JPanel {
    private static final int TILE = 16;
    private static final int MAP_GROWTH_PER_ROUND = 8;

    private static final Color FLOOR = new Color(58, 58, 72);
    private static final Color WALL = new Color(30, 30, 38);
    private static final Color EXIT = new Color(50, 180, 80);

    private final long baseSeed;
    private final Timer timer;
    private final Player player = new Player(0, 0);
    private long lastTickNanos;

    private int round = 1;
    private Dungeon dungeon;
    private int exitX, exitY;
    private boolean showOverlay;

    public Game(long seed) {
        this.baseSeed = seed;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);

        loadRound();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                player.setKey(e.getKeyCode(), true);
            }

            public void keyReleased(KeyEvent e) {
                player.setKey(e.getKeyCode(), false);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && showOverlay) {
                    round++;
                    loadRound();
                    repaint();
                }
            }
        });

        lastTickNanos = System.nanoTime();
        timer = new Timer(16, e -> tick());
        timer.start();
    }

    private void tick() {
        long now = System.nanoTime();
        double dt = Math.min((now - lastTickNanos) / 1_000_000_000.0, 0.05);
        lastTickNanos = now;

        boolean moved = player.update(dt, dungeon);
        boolean onExit = player.isOnTile(exitX, exitY);
        if (onExit != showOverlay) {
            showOverlay = onExit;
            repaint();
        } else if (moved) {
            repaint();
        }
    }

    private void loadRound() {
        int w = Dungeon.DEFAULT_WIDTH + (round - 1) * MAP_GROWTH_PER_ROUND;
        int h = Dungeon.DEFAULT_HEIGHT + (round - 1) * MAP_GROWTH_PER_ROUND;
        long roundSeed = baseSeed + (long) round * 9973L;
        dungeon = Dungeon.generate(roundSeed, w, h);
        player.spawnAt(dungeon.spawnX, dungeon.spawnY);
        Random rng = new Random(roundSeed ^ 0x5DEECE66DL);
        int[] exit = Dungeon.pickExitTile(dungeon, rng, dungeon.spawnX, dungeon.spawnY);
        exitX = exit[0];
        exitY = exit[1];
        showOverlay = false;
    }

    public static void start(long seed) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game(seed);
            JFrame frame = new JFrame("Crawl");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            game.requestFocusInWindow();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        double camX = player.getX() * TILE - getWidth() / 2.0;
        double camY = player.getY() * TILE - getHeight() / 2.0;

        int startTileX = (int) Math.floor(camX / TILE);
        int startTileY = (int) Math.floor(camY / TILE);
        int tilesX = getWidth() / TILE + 2;
        int tilesY = getHeight() / TILE + 2;

        for (int ty = 0; ty < tilesY; ty++) {
            for (int tx = 0; tx < tilesX; tx++) {
                int wx = startTileX + tx;
                int wy = startTileY + ty;
                int sx = (int) Math.round(wx * TILE - camX);
                int sy = (int) Math.round(wy * TILE - camY);

                if (wx == exitX && wy == exitY) {
                    g.setColor(EXIT);
                } else {
                    g.setColor(dungeon.walkable(wx, wy) ? FLOOR : WALL);
                }
                g.fillRect(sx, sy, TILE, TILE);
            }
        }

        player.drawCentered(g, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g.drawString("Round " + round, 10, 22);

        if (showOverlay) {
            drawOverlay(g);
        }
    }

    private void drawOverlay(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int boxW = 420;
        int boxH = 100;
        int boxX = (getWidth() - boxW) / 2;
        int boxY = (getHeight() - boxH) / 2;

        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
        g2.setColor(new Color(220, 220, 220));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        String line1 = "Stairs to the next floor.";
        String line2 = "Left-click to descend.";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(line1, boxX + (boxW - fm.stringWidth(line1)) / 2, boxY + 38);
        g2.drawString(line2, boxX + (boxW - fm.stringWidth(line2)) / 2, boxY + 62);
    }
}
