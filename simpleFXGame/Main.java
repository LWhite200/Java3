package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Main extends Application {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int PLAYER_SIZE = 30;
    private static final int MOVE_AMOUNT = 3;
    private static final int ENEMY_SIZE = 20;
    private static final int COLLECTIBLE_SIZE = 10;
    private static final int PROJECTILE_SIZE = 5;

    private Player player;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Collectible> collectibles = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();
    private Set<KeyCode> keysPressed = new HashSet<>();
    private Timeline timeline;
    private int score = 0;
    private int lives = 3;
    private Text scoreText;
    private Text livesText;
    private int countdown = 3; // Countdown for starting the game
    private Timeline countdownTimeline;

    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, WIDTH, HEIGHT);
            primaryStage.setScene(scene);

            player = new Player(WIDTH / 2 - (PLAYER_SIZE / 2), HEIGHT / 2 - (PLAYER_SIZE / 2), PLAYER_SIZE);
            root.getChildren().add(player.getRectangle());

            scoreText = new Text("Score: " + score);
            scoreText.setLayoutX(10);
            scoreText.setLayoutY(20);
            root.getChildren().add(scoreText);

            livesText = new Text("Lives: " + lives);
            livesText.setLayoutX(WIDTH - 80);
            livesText.setLayoutY(20);
            root.getChildren().add(livesText);

            scene.setOnKeyPressed(this::handleKeyPressed);
            scene.setOnKeyReleased(this::handleKeyReleased);
            scene.setOnMouseMoved(this::handleMouseMoved);
            scene.setOnMouseClicked(this::handleMouseClick);

            startCountdown(root);

            primaryStage.setTitle("Dynamic JavaFX Game");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCountdown(BorderPane root) {
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> { // Changed 'e' to 'event'
            if (countdown > 0) {
                scoreText.setText("Get Ready: " + countdown);
                countdown--;
            } else {
                scoreText.setText("Score: " + score);
                countdownTimeline.stop();
                spawnEnemies(5, root);
                spawnCollectibles(3, root);
                timeline = new Timeline(new KeyFrame(Duration.millis(16), gameEvent -> gameLoop(root))); // Changed 'e' to 'gameEvent'
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            }
        }));
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }


    private void handleMouseMoved(MouseEvent event) {
        player.setTarget(event.getX(), event.getY());
    }

    private void handleMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            shootProjectile();
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        keysPressed.add(event.getCode());
    }

    private void handleKeyReleased(KeyEvent event) {
        keysPressed.remove(event.getCode());
    }

    private void gameLoop(BorderPane root) {
        movePlayer();
        moveEnemies();
        moveProjectiles();
        checkCollisions(root);
        checkGameOver();
        updateScoreAndLives();
    }

    private void movePlayer() {
        if (keysPressed.contains(KeyCode.UP)) {
            player.move(0, -MOVE_AMOUNT);
        }
        if (keysPressed.contains(KeyCode.DOWN)) {
            player.move(0, MOVE_AMOUNT);
        }
        if (keysPressed.contains(KeyCode.LEFT)) {
            player.move(-MOVE_AMOUNT, 0);
        }
        if (keysPressed.contains(KeyCode.RIGHT)) {
            player.move(MOVE_AMOUNT, 0);
        }
    }

    private void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.chase(player);
        }
    }

    private void moveProjectiles() {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.move();
            if (projectile.isOutOfBounds() || projectile.getLifetime() > 3000) { // Remove projectile after 3 seconds
                projectiles.remove(i);
            }
        }
    }

    private void checkCollisions(BorderPane root) {
        // Check for collisions with enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (player.getRectangle().getBoundsInParent().intersects(enemy.getRectangle().getBoundsInParent())) {
                lives--;
                enemy.resetPosition();
                if (lives <= 0) {
                    return; // Stop the game loop
                }
            }
            // Check for projectile collisions with enemies
            for (int j = projectiles.size() - 1; j >= 0; j--) {
                Projectile projectile = projectiles.get(j);
                if (projectile.getRectangle().getBoundsInParent().intersects(enemy.getRectangle().getBoundsInParent())) {
                    enemies.remove(i);
                    projectiles.remove(j);
                    score++;
                    enemy.resetPosition();
                    break;
                }
            }
        }

        // Check for collisions with collectibles
        for (int i = collectibles.size() - 1; i >= 0; i--) {
            Collectible collectible = collectibles.get(i);
            if (player.getRectangle().getBoundsInParent().intersects(collectible.getRectangle().getBoundsInParent())) {
                score++;
                collectibles.remove(i);
                root.getChildren().remove(collectible.getRectangle());
                spawnCollectibles(1, root); // Add new collectible
            }
        }
    }

    private void spawnEnemies(int count, BorderPane root) {
        for (int i = 0; i < count; i++) {
            Enemy enemy = new Enemy();
            enemies.add(enemy);
            root.getChildren().add(enemy.getRectangle()); // Add enemy to the scene
        }
    }

    private void spawnCollectibles(int count, BorderPane root) {
        for (int i = 0; i < count; i++) {
            Collectible collectible = new Collectible();
            collectibles.add(collectible);
            root.getChildren().add(collectible.getRectangle()); // Add collectible to the scene
        }
    }

    private void shootProjectile() {
        Projectile projectile = new Projectile(player.getX() + PLAYER_SIZE / 2, player.getY(), PROJECTILE_SIZE);
        projectiles.add(projectile);
        ((BorderPane) player.getRectangle().getParent()).getChildren().add(projectile.getRectangle());
    }

    private void updateScoreAndLives() {
        scoreText.setText("Score: " + score);
        livesText.setText("Lives: " + lives);
    }

    private void checkGameOver() {
        if (lives <= 0) {
            System.out.println("Game Over! Final Score: " + score);
            timeline.stop();
            countdownTimeline.stop();
        }
    }

    public class Player {
        private Rectangle rectangle;

        public Player(int x, int y, int size) {
            rectangle = new Rectangle(x, y, size, size);
            rectangle.setFill(Color.BLUE);
        }

        public Rectangle getRectangle() {
            return rectangle;
        }

        public double getX() {
            return rectangle.getX();
        }

        public double getY() {
            return rectangle.getY();
        }

        public void move(int deltaX, int deltaY) {
            double newX = rectangle.getX() + deltaX;
            double newY = rectangle.getY() + deltaY;

            if (newX >= 0 && newX <= WIDTH - PLAYER_SIZE) {
                rectangle.setX(newX);
            }
            if (newY >= 0 && newY <= HEIGHT - PLAYER_SIZE) {
                rectangle.setY(newY);
            }
        }

        public void setTarget(double targetX, double targetY) {
            double deltaX = targetX - (rectangle.getX() + PLAYER_SIZE / 2);
            double deltaY = targetY - (rectangle.getY() + PLAYER_SIZE / 2);
            double angle = Math.atan2(deltaY, deltaX);
            rectangle.setRotate(Math.toDegrees(angle)); // Rotate player towards mouse
        }
    }

    public class Enemy {
        private Rectangle rectangle;
        private Random random = new Random();
        private final int speed = 2;

        public Enemy() {
            rectangle = new Rectangle(random.nextInt(WIDTH - ENEMY_SIZE), random.nextInt(HEIGHT - ENEMY_SIZE), ENEMY_SIZE, ENEMY_SIZE);
            rectangle.setFill(Color.RED);
        }

        public Rectangle getRectangle() {
            return rectangle;
        }

        public void chase(Player player) {
            double deltaX = player.getX() - rectangle.getX();
            double deltaY = player.getY() - rectangle.getY();
            double angle = Math.atan2(deltaY, deltaX);
            rectangle.setX(rectangle.getX() + Math.cos(angle) * speed);
            rectangle.setY(rectangle.getY() + Math.sin(angle) * speed);
        }

        public void resetPosition() {
            rectangle.setX(random.nextInt(WIDTH - ENEMY_SIZE));
            rectangle.setY(random.nextInt(HEIGHT - ENEMY_SIZE));
        }
    }

    public class Collectible {
        private Rectangle rectangle;

        public Collectible() {
            Random random = new Random();
            rectangle = new Rectangle(random.nextInt(WIDTH - COLLECTIBLE_SIZE), random.nextInt(HEIGHT - COLLECTIBLE_SIZE), COLLECTIBLE_SIZE, COLLECTIBLE_SIZE);
            rectangle.setFill(Color.GOLD);
        }

        public Rectangle getRectangle() {
            return rectangle;
        }
    }

    public class Projectile {
        private Rectangle rectangle;
        private final double speed = 5;
        private long creationTime;

        public Projectile(double x, double y, double size) {
            rectangle = new Rectangle(x, y, size, size);
            rectangle.setFill(Color.GREEN);
            creationTime = System.currentTimeMillis();
        }

        public Rectangle getRectangle() {
            return rectangle;
        }

        public void move() {
            rectangle.setY(rectangle.getY() - speed);
        }

        public boolean isOutOfBounds() {
            return rectangle.getY() < 0;
        }

        public long getLifetime() {
            return System.currentTimeMillis() - creationTime;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
