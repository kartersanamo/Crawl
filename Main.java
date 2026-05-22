import game.Game;

public class Main {
    public static void main(String[] args) {
        long seed = args.length == 0 ? System.currentTimeMillis() : Long.parseLong(args[0]);
        System.out.println("Seed: " + seed);
        Game.start(seed);
    }
}
