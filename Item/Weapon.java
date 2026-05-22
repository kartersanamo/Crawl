public abstract class Weapon {
    private String name;
    private int damage;
    private int durability;
    private int price;
    private int level;
    private int rarity;
    private int type;
    private int rarity; 

    public Weapon(String name, int damage, int durability, int price, int level, int rarity, int type, int rarity) {
        this.name = name;
        this.damage = damage;
        this.durability = durability;
        this.price = price;
        this.level = level;
        this.rarity = rarity;
        this.type = type;
        this.rarity = rarity;
    }
}