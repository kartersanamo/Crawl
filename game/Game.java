package game;

import dungeon.Dungeon;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public final class Game extends JPanel {
    private static final int TILE = 16;

    private final long seed;
    private final Dungeon dungeon;
    private int px, py;

    public Game(long seed) {
        this.seed = seed;
        dungeon = Dungeon.generate(seed);
        px = dungeon.spawnX;
        py = dungeon.spawnY;
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();
                int dx = (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT) ? 1 : (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT) ? -1 : 0;
                int dy = (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN) ? 1 : (k == KeyEvent.VK_W || k == KeyEvent.VK_UP) ? -1 : 0;
                if (dungeon.walkable(px + dx, py + dy)) {
                    px += dx;
                    py += dy;
                    repaint();
                }
            }
        });
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

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cols = getWidth() / TILE, rows = getHeight() / TILE;
        int ox = px - cols / 2, oy = py - rows / 2;
        for (int sy = 0; sy < rows; sy++) {
            for (int sx = 0; sx < cols; sx++) {
                int wx = ox + sx, wy = oy + sy;
                if (wx == px && wy == py) g.setColor(new Color(240, 210, 90));
                else g.setColor(dungeon.walkable(wx, wy) ? new Color(58, 58, 72) : new Color(30, 30, 38));
                g.fillRect(sx * TILE, sy * TILE, TILE, TILE);
            }
        }
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Seed: " + seed, 10, 16);
    }
}
