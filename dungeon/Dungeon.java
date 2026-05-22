package dungeon;

import java.util.Random;

public final class Dungeon {
    // Default map width in tiles. Higher = larger dungeon (more space for rooms).
    // 100×70 feels like a classic floor: big enough to explore, not an empty wasteland.
    public static final int DEFAULT_WIDTH = 100;
    // Default map height in tiles. Higher = larger dungeon (more space for rooms).
    public static final int DEFAULT_HEIGHT = 70;

    // Wall thickness around the map edge in tiles. Higher = thicker outer wall border.
    public static final int MAP_BORDER = 1;

    // Smallest region (width or height) that BSP will still try to split.
    // Lower = more splits = more rooms. Higher = fewer, bigger regions and fewer rooms.
    // Must be greater than SPLIT_PADDING * 2 or horizontal/vertical splits cannot run.
    // 14 gives a mix of small chambers and medium halls without tiny sliver rooms.
    public static final int MIN_PARTITION_SIZE = 14;
    // Minimum gap from a region edge when placing a split line (each side).
    // Lower = splits can happen closer to edges (more irregular rooms). Higher = safer margins, fewer valid splits.
    // 3 keeps corridors narrow and organic; 8+ looks grid-like and wastes space.
    public static final int SPLIT_PADDING = 3;

    // Smallest room width/height in tiles. Higher = every room is at least this big (fewer tiny closets).
    // 4 allows closets and closets-plus; 6+ makes every room feel like a boss arena.
    public static final int MIN_ROOM_SIZE = 4;
    // Empty space kept around a room inside its BSP leaf (per side, doubled in code).
    // Higher = smaller max rooms inside each leaf. Lower = rooms can fill more of the leaf.
    // 2 leaves a natural gutter between room walls and where corridors meet.
    public static final int ROOM_PARTITION_MARGIN = 2;
    // Minimum distance from the leaf edge when placing a room corner.
    // Higher = rooms sit farther from corridors/walls. Lower = rooms can hug leaf edges.
    public static final int ROOM_INSET = 1;
    // Extra slack used when randomizing room position inside a leaf.
    // Higher = more position variety. Lower = tighter, more predictable placement.
    // 2 nudges rooms off-center so layouts do not look stamped on a grid.
    public static final int ROOM_POSITION_MARGIN = 2;

    // Internal marker: room center not set yet. Do not change unless you update Node logic.
    public static final int NO_CENTER = -1;
    // Internal marker: player spawn not chosen yet. Do not change unless you update build() logic.
    public static final int SPAWN_UNSET = -1;

    public final boolean[][] floor;
    public final int spawnX;
    public final int spawnY;

    private Dungeon(boolean[][] floor, int spawnX, int spawnY) {
        this.floor = floor;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public static Dungeon generate(long seed) {
        return generate(seed, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static Dungeon generate(long seed, int w, int h) {
        Random rng = new Random(seed);
        boolean[][] tiles = new boolean[h][w];
        Node root = new Node(
                MAP_BORDER,
                MAP_BORDER,
                w - MAP_BORDER * 2,
                h - MAP_BORDER * 2
        );
        split(root, rng);
        int[] spawn = {SPAWN_UNSET, SPAWN_UNSET};
        build(root, tiles, rng, spawn);
        return new Dungeon(tiles, spawn[0], spawn[1]);
    }

    public boolean walkable(int x, int y) {
        return y >= 0 && y < floor.length && x >= 0 && x < floor[0].length && floor[y][x];
    }

    /** Picks a random floor tile away from spawn for the exit portal. */
    public static int[] pickExitTile(Dungeon dungeon, Random rng, int spawnX, int spawnY) {
        int w = dungeon.floor[0].length;
        int h = dungeon.floor.length;
        for (int attempt = 0; attempt < 5000; attempt++) {
            int x = rng.nextInt(w);
            int y = rng.nextInt(h);
            if (!dungeon.walkable(x, y) || (x == spawnX && y == spawnY)) {
                continue;
            }
            int dx = x - spawnX;
            int dy = y - spawnY;
            if (dx * dx + dy * dy >= 64) {
                return new int[] {x, y};
            }
        }
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (dungeon.walkable(x, y) && !(x == spawnX && y == spawnY)) {
                    return new int[] {x, y};
                }
            }
        }
        return new int[] {spawnX, spawnY};
    }

    private static int minSizeToSplit() {
        return Math.max(MIN_PARTITION_SIZE, SPLIT_PADDING * 2 + 1);
    }

    private static boolean canSplitHorizontally(Node n) {
        return n.h >= minSizeToSplit();
    }

    private static boolean canSplitVertically(Node n) {
        return n.w >= minSizeToSplit();
    }

    private static void split(Node n, Random rng) {
        boolean preferHorizontal = n.h > n.w ? true : n.w > n.h ? false : rng.nextBoolean();
        if (preferHorizontal) {
            if (canSplitHorizontally(n)) {
                splitHorizontal(n, rng);
            } else if (canSplitVertically(n)) {
                splitVertical(n, rng);
            }
        } else {
            if (canSplitVertically(n)) {
                splitVertical(n, rng);
            } else if (canSplitHorizontally(n)) {
                splitHorizontal(n, rng);
            }
        }
    }

    private static void splitHorizontal(Node n, Random rng) {
        int range = n.h - SPLIT_PADDING * 2;
        int sy = n.y + SPLIT_PADDING + rng.nextInt(range);
        n.L = new Node(n.x, n.y, n.w, sy - n.y);
        n.R = new Node(n.x, sy, n.w, n.y + n.h - sy);
        split(n.L, rng);
        split(n.R, rng);
    }

    private static void splitVertical(Node n, Random rng) {
        int range = n.w - SPLIT_PADDING * 2;
        int sx = n.x + SPLIT_PADDING + rng.nextInt(range);
        n.L = new Node(n.x, n.y, sx - n.x, n.h);
        n.R = new Node(sx, n.y, n.x + n.w - sx, n.h);
        split(n.L, rng);
        split(n.R, rng);
    }

    private static void build(Node n, boolean[][] tiles, Random rng, int[] spawn) {
        if (n.L == null) {
            int partitionMargin = ROOM_PARTITION_MARGIN * 2;
            int rw = MIN_ROOM_SIZE + rng.nextInt(Math.max(1, n.w - partitionMargin));
            int rh = MIN_ROOM_SIZE + rng.nextInt(Math.max(1, n.h - partitionMargin));
            int rx = n.x + ROOM_INSET + rng.nextInt(Math.max(1, n.w - rw - ROOM_POSITION_MARGIN));
            int ry = n.y + ROOM_INSET + rng.nextInt(Math.max(1, n.h - rh - ROOM_POSITION_MARGIN));
            fill(tiles, rx, ry, rw, rh);
            n.cx = rx + rw / 2;
            n.cy = ry + rh / 2;
            if (spawn[0] == SPAWN_UNSET) {
                spawn[0] = n.cx;
                spawn[1] = n.cy;
            }
            return;
        }
        build(n.L, tiles, rng, spawn);
        build(n.R, tiles, rng, spawn);
        tunnel(tiles, center(n.L), center(n.R), rng.nextBoolean());
    }

    private static int[] center(Node n) {
        if (n.cx >= 0) {
            return new int[] {n.cx, n.cy};
        }
        return n.L != null ? center(n.L) : center(n.R);
    }

    private static void tunnel(boolean[][] tiles, int[] a, int[] b, boolean hFirst) {
        if (hFirst) {
            lineH(tiles, a[0], b[0], a[1]);
            lineV(tiles, a[1], b[1], b[0]);
        } else {
            lineV(tiles, a[1], b[1], a[0]);
            lineH(tiles, a[0], b[0], b[1]);
        }
    }

    private static void fill(boolean[][] tiles, int x, int y, int w, int h) {
        for (int dy = 0; dy < h; dy++) {
            for (int dx = 0; dx < w; dx++) {
                tiles[y + dy][x + dx] = true;
            }
        }
    }

    private static void lineH(boolean[][] tiles, int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            tiles[y][x] = true;
        }
    }

    private static void lineV(boolean[][] tiles, int y1, int y2, int x) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            tiles[y][x] = true;
        }
    }

    private static final class Node {
        final int x, y, w, h;
        Node L, R;
        int cx = NO_CENTER;
        int cy = NO_CENTER;

        Node(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
