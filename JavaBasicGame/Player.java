
// Player class to store player information
class Player {
    private static String name;
    private static int level;
    private static int maxHealth;
    private static int currentHealth;
    private static int experience;
    private static int x;
    private static int y;

    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.experience = 0;
        this.x = 0;
        this.y = 0;
    }
    
    
    
    public int getX() {
    	return x;
    }
    
    public void setX(int a) {
    	x = a;
    }
    
    
    
    public boolean isAlive() {
        return currentHealth > 0;
    }
    
    public int getY() {
    	return y;
    }
    
    public void setY(int a) {
    	y = a;
    }
    

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getExperience() {
        return experience;
    }

    public void gainExperience(int experience) {
        this.experience += experience;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int requiredExp = level * 100; // Example: Level up every 100 experience points
        if (experience >= requiredExp) {
            level++;
            maxHealth += 20; // Example: Increase max health on level up
            currentHealth = maxHealth; // Heal player to full health on level up
            System.out.println("Congratulations! You have leveled up to level " + level + ".");
        }
    }

    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }

    public void heal(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }
}