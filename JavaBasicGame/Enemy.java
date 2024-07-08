class Enemy {
    private String name;
    private int level;
    private int maxHealth;
    private int currentHealth;
    private int damage;

    public Enemy(String name, int level) {
        this.name = name;
        this.level = level;
        this.maxHealth = 50 + level * 10; // Example: Increase enemy health with level
        this.currentHealth = maxHealth;
        this.damage = 5 + level * 2; // Example: Increase enemy damage with level
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }

    public int getDamage() {
        return damage;
    }

    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }
}