import java.util.Random;
import java.util.Scanner;

public class RPG {
    private static final Random random = new Random();
    private static final Scanner scanner = new Scanner(System.in);
    private static Player player; // Assuming Player class exists

    

    public static void battleRPG() {
    	player = DEG.player;
        while (player.isAlive()) {
            Enemy enemy = generateRandomEnemy();

            System.out.println("\nEncountered an enemy: " + enemy.getName() + " | Level: " + enemy.getLevel());
            System.out.println("Enemy Health: " + enemy.getCurrentHealth());

            while (enemy.isAlive() && player.isAlive()) {
                playerTurn(enemy);

                if (enemy.isAlive()) {
                    enemyAttack(enemy);
                }
            }

            if (player.isAlive()) {
                System.out.println("You defeated the " + enemy.getName() + "!");
                player.gainExperience(50); // Gain experience after defeating an enemy
            } else {
                System.out.println("Game Over - You have been defeated.");
            }

            // Ask if player wants to continue
            System.out.print("Do you want to continue? (yes/no): ");
            String continueGame = scanner.nextLine().trim().toLowerCase();
            if (!continueGame.equals("yes")) {
                break;
            }
        }

        System.out.println("Congratulations! You have won the game!");
    }

    private static Enemy generateRandomEnemy() {
        String[] enemyNames = {"Goblin", "Orc", "Skeleton", "Wolf"};
        int randomLevel = random.nextInt(player.getLevel()) + 1; // Enemy level <= player level
        int randomIndex = random.nextInt(enemyNames.length);
        return new Enemy(enemyNames[randomIndex], randomLevel); // Assuming Enemy class exists
    }

    private static void playerTurn(Enemy enemy) {
    	System.out.println(" ");
    	System.out.println("---------------------------");
        System.out.println("Player: " + player.getName() + " | Level: " + player.getLevel());
        System.out.println("Health: " + player.getCurrentHealth() + "/" + player.getMaxHealth());
        System.out.println(enemy.getName() + ": " + enemy.getCurrentHealth() + "/" + enemy.getMaxHealth());
        System.out.println("Experience: " + player.getExperience());

        System.out.println("\n1. Attack");
        System.out.println("2. Run");

        System.out.print("\nChoose your action: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        switch (choice) {
            case 1:
                attackEnemy(enemy);
                break;
            case 2:
                boolean escaped = tryToEscape();
                if (escaped) {
                    System.out.println("You managed to escape!");
                    return; // End battle if escaped
                } else {
                    System.out.println("You failed to escape!");
                }
                break;
            default:
                System.out.println("Invalid choice. Try again.");
                break;
        }
    }

    private static void attackEnemy(Enemy enemy) {
        int playerDamage = random.nextInt(15) + 1; // Random damage between 1 to 15
        enemy.takeDamage(playerDamage);
        System.out.println("You attacked the " + enemy.getName() + " for " + playerDamage + " damage.");

        if (!enemy.isAlive()) {
            System.out.println("You defeated the " + enemy.getName() + "!");
        }
    }

    private static void enemyAttack(Enemy enemy) {
        if (enemy.isAlive()) {
            int enemyDamage = random.nextInt(10) + 1; // Random damage between 1 to 10 for enemy
            player.takeDamage(enemyDamage);
            System.out.println("The " + enemy.getName() + " attacked you for " + enemyDamage + " damage.");

            if (!player.isAlive()) {
                System.out.println("You have been defeated by the " + enemy.getName() + ".");
            }
        }
    }

    private static boolean tryToEscape() {
        return random.nextBoolean(); // 50% chance to escape
    }
}
