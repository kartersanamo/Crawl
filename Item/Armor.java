public abstract class Armor {
    private String name;
    private int defense;
    private int durability;
    private int price;
    private int level;
    private int rarity;
    private int type;
    private int rarity; 
    
    public Armor(String name, int defense, int durability, int price, int level, int rarity, int type, int rarity) {
        this.name = name;
        this.defense = defense;
        this.durability = durability;
        this.price = price;
        this.level = level;
        this.rarity = rarity;
        this.type = type;
        this.rarity = rarity;
    }
}