package application;
//Homework 6: Bean Machine 
//Course: CIS 357
//Due date: August 7, 2024
//Name: Lukas A. White
//Instructor: Il-Hyung Cho
/* 
 * This program uses javaFX to display a bean machine
 * that gives the user power to display the amount of pegs, 
 * the amount of beans, and the speed of the bean.
 * it uses a lot of bad, but working code to do all that.
 * it takes advantage of java's OOP to update and make beans
 * 
 */


import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main class for the bean machine application.
 */
public class HW6_White extends Application {
    private static Stage primaryStage;
    private static Pane pane;

    /** Label to display the score. */
    Label scoreLabel;

    /** Random number generator */
    Random rand = new Random();

    /** Bean object  */
    public Bean bean;

    /** The width of the bean machine. */
    public int length = 548;

    /** The height of the bean machine. */
    public int width = 432;

    /** The width of the scene for extra GUI components. */
    public int sceneWidth = 550;

    /** Title of the application window. */
    String title = "White's Bean Machine";

    /**  the game needs to be reset.  Reset function did not work*/
    Boolean reset = true;

    /** Radius of the bean's circular shape. */
    int circRad = 10;

    
    
    
    /** Cur score */
    public static int score = 0;

    /** Speed of the bean's animation. */
    int speed = 10;

    /** Number of pegs in the bean machine. */
    public int numPegs = 68;

    /** Number of beans remaining to be dropped. */
    int numBeans = 20;

    
    
    
    /** Levels of peg configurations. */
    public int[] level = {1, 3, 5, 11, 18, 27, 39, 52, 68};

    /** Array representing the number of pegs in each row. */
    int[] boxes;

    /** Separation between the balls (pegs) horizontally. */
    public int ballSeparation = 60;

    /** Maximum number of layers of pegs. */
    public int maxLayers = 0;

    /** Vertical positions of the peg rows. */
    public int[] yVal = {36 + 20, 72 + 25, 108 + 30, 144 + 35, 180 + 40, 216 + 45, 252 + 50, 288 + 55};

    /** Horizontal positions for the boxes. */
    public int[] xVal = {85, 145, 205, 265, 325, 385, 445};

    /** Instance of UpdateBeans class for updating beans. */
    static UpdateBeans upBeans;

    /**
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Updates the score display.
     */
    public void updateScore() {
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Initializes and starts the application.
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        HW6_White.primaryStage = primaryStage;
        pane = new Pane();
        pane.setPrefSize(length, width);
        upBeans = new UpdateBeans(this);

        drawBoxes(); 

        // Create labels for sliders
        Label speedLabel = new Label("Speed:");
        speedLabel.setLayoutX(10);
        speedLabel.setLayoutY(sceneWidth - 120);

        
        Label numBallsLabel = new Label("Number of Balls:");
        numBallsLabel.setLayoutX(10);
        numBallsLabel.setLayoutY(sceneWidth - 90);

        
        Label numPegsLabel = new Label("Number of Pegs:");
        numPegsLabel.setLayoutX(10);
        numPegsLabel.setLayoutY(sceneWidth - 60);

        
        // Create sliders for speed, number of balls, and number of pegs
        // Sliders are fun, got help from a past assignment for different class
        
        // speed
        Slider speedSlider = new Slider(1, 100, speed);
        speedSlider.setPrefWidth(200); 
        
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        
        speedSlider.setMajorTickUnit(10);
        speedSlider.setBlockIncrement(1);
        speedSlider.setLayoutX(120);
        speedSlider.setLayoutY(sceneWidth - 120);
        
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            speed = newVal.intValue();
        });

        // num beans
        Slider numBallsSlider = new Slider(1, 100, numBeans);
        numBallsSlider.setPrefWidth(200); 
        numBallsSlider.setShowTickLabels(true);
        numBallsSlider.setShowTickMarks(true);
        numBallsSlider.setMajorTickUnit(10);
        numBallsSlider.setBlockIncrement(1);
        numBallsSlider.setLayoutX(120);
        numBallsSlider.setLayoutY(sceneWidth - 90);
        numBallsSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            numBeans = newVal.intValue();
        });

        // num pegs
        Slider numPegsSlider = new Slider(1, level[level.length - 1], numPegs);
        numPegsSlider.setPrefWidth(200); 
        numPegsSlider.setShowTickLabels(true);
        numPegsSlider.setShowTickMarks(true);
        numPegsSlider.setMajorTickUnit(1);
        numPegsSlider.setBlockIncrement(1);
        numPegsSlider.setLayoutX(120);
        numPegsSlider.setLayoutY(sceneWidth - 60);
        numPegsSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            numPegs = newVal.intValue(); // get the right values from the fucntion
            if (!contains(level, numPegs)) {
                numPegs = level[level.length - 1];
                numPegsSlider.setValue(numPegs);
            }
        });

        
        
        // button reset/begin
        Button beginButton = new Button("Begin");
        beginButton.setLayoutX(length - 80);
        beginButton.setLayoutY(sceneWidth - 50);
        beginButton.setOnAction(e -> begin());

        
        // more score label stuff, too complicated
        scoreLabel = new Label("Score: " + score);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");

        // grey background box with labell
        Rectangle greyBox = new Rectangle(0, sceneWidth - 100, length, 110);
        greyBox.setFill(Color.GRAY);
        pane.getChildren().addAll(greyBox, scoreLabel);
        scoreLabel.setLayoutX(length / 2 - 40); 
        scoreLabel.setLayoutY(sceneWidth - 80);

        // Adds labels, sliders, and button to pane. (create whole pane)
        pane.getChildren().addAll(speedLabel, numBallsLabel, numPegsLabel, speedSlider, numBallsSlider, numPegsSlider, beginButton);

        Scene scene = new Scene(pane, length, sceneWidth);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Starts the game by initializing the components and beginning the simulation.
     */
    void begin() {
        pane.getChildren().clear();
        drawBoxes();
        createPegs();
        drawPegs();
        addBean();

        
        // this class is repetative as javaFX is weird. or i am just slow
        
        // Create the bean shape
        Circle beanShape = new Circle(bean.getX(), bean.getY(), 10);
        beanShape.setFill(Color.BLUE); // Default color, changes to oragne if dead
        beanShape.setStroke(Color.WHITE);
        beanShape.setStrokeWidth(2);
        pane.getChildren().add(beanShape);

        
        scoreLabel = new Label("Score: " + score);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
        scoreLabel.setLayoutX(length / 2 - 40); 
        scoreLabel.setLayoutY(sceneWidth - 80); 
 
        Rectangle greyBox = new Rectangle(0, sceneWidth - 100, length, 110);
        greyBox.setFill(Color.GRAY);

        pane.getChildren().addAll(greyBox, scoreLabel);


        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> {
            upBeans.updateBean(bean);
            beanShape.setCenterY(bean.getY());
            beanShape.setCenterX(bean.getX());

            // Change bean color based on its Y position
            // orange means dead
            if (bean.getY() >= width - 15) {
                beanShape.setFill(Color.ORANGE);
            } else {
                beanShape.setFill(Color.BLUE);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Update the score display
        // took too long
        updateScore();
    }

    /**
     * Checks if the specified array contains the given value.
     * @param array The array to check
     * @param value The value to find
     * @return True if the array contains the value, false otherwise
     */
    private boolean contains(int[] array, int value) {
        for (int v : array) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates the pegs based on the number of pegs.
     * set amount of pegs, case statemtn
     */
    void createPegs() {
        int[] bTemp;
        switch (numPegs) {
            case 1:
                bTemp = new int[]{1};
                maxLayers = 1;
                break;
            case 3:
                bTemp = new int[]{2, 1};
                maxLayers = 2;
                break;
            case 5:
                bTemp = new int[]{2, 3};
                maxLayers = 2;
                break;
            case 11:
                bTemp = new int[]{4, 3, 4};
                maxLayers = 3;
                break;
            case 18:
                bTemp = new int[]{4, 5, 4, 5};
                maxLayers = 4;
                break;
            case 27:
                bTemp = new int[]{5, 6, 5, 6, 5};
                maxLayers = 5;
                break;
            case 39:
                bTemp = new int[]{6, 7, 6, 7, 6, 7};
                maxLayers = 6;
                break;
            case 52:
                bTemp = new int[]{7, 8, 7, 8, 7, 8, 7};
                maxLayers = 7;
                break;
            case 68:
                bTemp = new int[]{8, 9, 8, 9, 8, 9, 8, 9};
                maxLayers = 8;
                break;
            default:
                bTemp = new int[]{2, 2, 2, 3};
                break;
        }
        boxes = bTemp;
    }
    
    

    /**
     * Draws the pegs on the pane.
     */
    void drawPegs() {
        for (int i = 0; i < boxes.length; i++) {
            int row = boxes[i];
            
            double totalWidth = (row - 1) * ballSeparation; 
            double startX = (length - totalWidth) / 2; 

            for (int j = 0; j < row; j++) {
                double x = startX + j * ballSeparation; 
                
                
                Circle redCircle = new Circle(x, yVal[i], circRad);
                redCircle.setFill(i == 0 ? Color.GREEN : Color.RED);
                pane.getChildren().add(redCircle);
            }
        }
    }

    /**
     * Draws the boxes at the top of the bean machine.
     */
    void drawBoxes() {
        for (int i = 1; i < 8; i++) {
            Rectangle rectangle = new Rectangle(xVal[i - 1], width - 50, 12, 70);
            rectangle.setFill(Color.BLACK); // Set the rectangle color to black
            pane.getChildren().add(rectangle);
        }
    }


    /**
     * Resets the game by clearing the pane and initializing the components again.
     */
    void reset() {
        pane.getChildren().clear();
        createPegs();
        drawPegs();
        addBean();
        reset = false;
    }

    /**
     * Adds a new bean to the simulation.
     */
    public void addBean() {
        numBeans--;

        int radius = boxes[0];
        Random rand = new Random(); 


        int randX = rand.nextInt(radius / 2);


        if (rand.nextBoolean()) {
            randX = -randX;
        }

        randX *= 60;

        // same as yesterday, except the randX is added to the x for randomness I forgot.
        if (numBeans >= 0) {
            if (boxes[0] % 2 != 0) {
                bean = new Bean(true, (length / 2) + randX, 0, 0, (length / 2) + randX, yVal[0]);
            } else {
                int startX = length / 2;
                if (rand.nextBoolean()) {
                    startX += 30;
                } else {
                    startX -= 30;
                }
                bean = new Bean(true, startX + randX, 0, 0, startX + randX, yVal[0]);
            }
        }
    }
}
