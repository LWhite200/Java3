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
* javaDoc as I forgot and eclipse guides me through it.
*/

/**
 * Represents a bean in the simulation, with its position, score, and target. The Object.
 */
public class Bean {

    /** Indicates whether the bean is alive. */
    private boolean alive;

    /** The x-coordinate of the bean's current position. */
    private int x;

    /** The y-coordinate of the bean's current position. */
    private int y;

    /** The score associated with the bean. */
    private int score;

    /** The x-coordinate of the bean's target position. */
    public int targetX;

    /** The y-coordinate of the bean's target position. */
    public int targetY;

    /**
     * @param alive Whether the bean is alive
     * @param x The x-coordinate of the bean's initial position
     * @param y The y-coordinate of the bean's initial position
     * @param score The score associated with the bean
     * @param targetX The x-coordinate of the bean's target position
     * @param targetY The y-coordinate of the bean's target position
     */
    public Bean(boolean alive, int x, int y, int score, int targetX, int targetY) {
        this.alive = alive;
        this.x = x;
        this.y = y;
        this.score = score;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    /**
     * Updates the target position of the bean.
     * @param x The new x-coordinate of the target position
     * @param y The new y-coordinate of the target position
     */
    public void updateTarget(int x, int y) {
        this.targetX = x;
        this.targetY = y;
    }

    /**
     * @return True if the bean is alive, false otherwise
     */
    public boolean getAlive() {
        return this.alive;
    }

    /**
     * @return The bean's score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * @return The x-coordinate of the bean's position
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return The y-coordinate of the bean's position
     */
    public int getY() {
        return this.y;
    }

    /**
     * @return The x-target
     */
    public int getTargetX() {
        return this.targetX;
    }

    /**
     * @return The y-target
     */
    public int getTargetY() {
        return this.targetY;
    }

    /**
     * @param alive The new alive status of the bean
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * @param score, but not used
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Sets the x
     * @param x The new x-coordinate of the bean's position
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y
     * @param y The new y-coordinate of the bean's position
     */
    public void setY(int y) {
        this.y = y;
    }
}
