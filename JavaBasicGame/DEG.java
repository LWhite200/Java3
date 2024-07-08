import java.util.Random;
import java.util.Scanner;

public class DEG {
    public static Player player;
    private static final Scanner scanner = new Scanner(System.in);

    private static boolean worldReady = false;
    private static char[][] world;
    private static int worldX = 25;
    private static int worldY = 12;
    private static int playerX = 1;
    private static int playerY = 1;
    private static int enemyCount = 2; // Initial number of enemies
    private static int[] enemy1 = {20, 6};
    private static int[] enemy2 = {15, 8};
    private static boolean bombPlaced = false;
    private static int bombX;
    private static int bombY;
    private static int bombTimer = 0;

    public static void main(String[] args) {
        System.out.println("Welcome to Simple RPG!");

        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine();
        player = new Player(playerName);

        createWorld();
        initPlay();

        while (true) {
            updateScreen();
            getInput();
        }
    }

    private static void createWorld() {
        world = new char[worldX][worldY];
        // Initialize the world with empty spaces
        for (int y = 0; y < worldY; y++) {
            for (int x = 0; x < worldX; x++) {
                world[x][y] = ' ';
            }
        }
        // Add some walls
        for (int y = 0; y < worldY; y++) {
            world[0][y] = '#'; // left boundary
            world[worldX - 1][y] = '#'; // right boundary
        }
        worldReady = true;
    }

    private static void initPlay() {
        placeEntity(playerX, playerY, 'P'); // Place the player in the world
        placeEntity(enemy1[0], enemy1[1], 'E'); // Place enemy 1 in the world
        placeEntity(enemy2[0], enemy2[1], 'E'); // Place enemy 2 in the world
    }

    private static void placeEntity(int x, int y, char symbol) {
        if (x >= 0 && x < worldX && y >= 0 && y < worldY) {
            world[x][y] = symbol;
        }
    }

    private static void getInput() {
        System.out.print("Enter movement (W for up, S for down, A for left, D for right, F for bomb): ");
        String input = scanner.nextLine().toUpperCase();

        switch (input) {
            case "W":
                movePlayer(0, -1);
                break;
            case "S":
                movePlayer(0, 1);
                break;
            case "A":
                movePlayer(-1, 0);
                break;
            case "D":
                movePlayer(1, 0);
                break;
            case "F":
                placeBomb();
                break;
            default:
                System.out.println("Invalid input. Try again.");
                break;
        }

        checkEncounter();
        moveEnemies();
        checkBomb();
    }

    private static void movePlayer(int deltaX, int deltaY) {
        int newPlayerX = playerX + deltaX;
        int newPlayerY = playerY + deltaY;

        if (isValidMove(newPlayerX, newPlayerY)) {
            moveEntity(playerX, playerY, newPlayerX, newPlayerY, 'P');
            playerX = newPlayerX;
            playerY = newPlayerY;
        } else {
            System.out.println("Cannot move outside boundaries. Try again.");
        }
    }

    private static boolean isValidMove(int x, int y) {
        return x > 0 && x < worldX - 1 && y > 0 && y < worldY - 1;
    }

    private static void moveEntity(int fromX, int fromY, int toX, int toY, char symbol) {
        if (fromX >= 0 && fromX < worldX && fromY >= 0 && fromY < worldY) {
            world[fromX][fromY] = ' ';
        }
        placeEntity(toX, toY, symbol);
    }

    private static void moveEnemies() {
        moveEnemy(enemy1);
        moveEnemy(enemy2);
    }

    private static void moveEnemy(int[] enemy) {
        int deltaX, deltaY;

        Random random = new Random();
        if (random.nextDouble() < 0.25) {
            deltaX = random.nextInt(3) - 1;
            deltaY = random.nextInt(3) - 1;
        } else {
            deltaX = Integer.compare(playerX, enemy[0]);
            deltaY = Integer.compare(playerY, enemy[1]);
        }

        int newEnemyX = enemy[0] + deltaX;
        int newEnemyY = enemy[1] + deltaY;

        if (isValidMove(newEnemyX, newEnemyY) && (newEnemyX != playerX || newEnemyY != playerY)) {
            moveEntity(enemy[0], enemy[1], newEnemyX, newEnemyY, 'E');
            enemy[0] = newEnemyX;
            enemy[1] = newEnemyY;
        }
    }

    private static void placeBomb() {
        if (!bombPlaced) {
            bombX = playerX;
            bombY = playerY;
            placeEntity(bombX, bombY, 'B');
            bombPlaced = true;
            System.out.println("Bomb placed at (" + bombX + ", " + bombY + ")");
        } else {
            System.out.println("Bomb already placed. Wait for explosion or clear it.");
        }
    }

    private static void checkBomb() {
        if (bombPlaced) {
            bombTimer++;
            if (bombTimer == 3) {
                explodeBomb();
            }
        }
    }

    private static void explodeBomb() {
        bombPlaced = false;
        bombTimer = 0;
        world[bombX][bombY] = ' '; // Clear bomb position

        eliminateEnemiesNear(bombX, bombY, 5);

        System.out.println("Bomb exploded!");
    }

    private static void eliminateEnemiesNear(int centerX, int centerY, int radius) {
        for (int y = Math.max(0, centerY - radius); y <= Math.min(worldY - 1, centerY + radius); y++) {
            for (int x = Math.max(0, centerX - radius); x <= Math.min(worldX - 1, centerX + radius); x++) {
                if (world[x][y] == 'E') {
                    world[x][y] = ' '; // Eliminate enemy
                    enemyCount--; // Decrease enemy count
                    System.out.println("You defeated an enemy! Remaining enemies: " + enemyCount);
                }
            }
        }
    }

    private static void updateScreen() {
        for (int y = 0; y < worldY; y++) {
            for (int x = 0; x < worldX; x++) {
                System.out.print(world[x][y]);
            }
            System.out.println();
        }
        System.out.println("-----------");
    }

    private static void checkEncounter() {
        for (int[] enemy : new int[][]{enemy1, enemy2}) {
            if (playerX == enemy[0] && playerY == enemy[1]) {
                RPG.battleRPG();
                if (world[enemy[0]][enemy[1]] == 'E') { // Check if enemy still exists in world
                    world[enemy[0]][enemy[1]] = ' '; // Remove enemy from world after battle
                    enemyCount--; // Decrease enemy count
                    System.out.println("You defeated an enemy! Remaining enemies: " + enemyCount);
                }
                return;
            }
        }
    }
}
