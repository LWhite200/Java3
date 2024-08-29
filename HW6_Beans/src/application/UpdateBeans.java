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

/**
 * Class responsible for updating the position and STATE OF BEAN
 */
class UpdateBeans {
    /** Reference  */
    HW6_White HW6;

    /** Random number generator  */
    Random rand = new Random();

    /** Current score  */
    public int score = 0;

    /** Step size for bean movement. */
    private static final int STEP_SIZE = 2;

    /**
     * Constructs an UpdateBeans linkage
     * @param hw6 The HW6_White instance managing the game
     */
    public UpdateBeans(HW6_White hw6) {
        this.HW6 = hw6;
    }

    /**
     * Updates the state of the given bean.
     * @param bean The bean to update
     */
    public void updateBean(Bean bean) {
        score = 0; // Resets score for each update
        if (bean.getAlive()) {
            updateBeanPosition(bean);
        }
        score += bean.getScore();
    }

    /**
     * Updates the position of the bean and handles interactions with pegs.
     * @param bean The bean to update
     * @return true if the bean is still alive and moving, false otherwise
     */
    boolean updateBeanPosition(Bean bean) {
        int currentY = bean.getY();
        int targetY = bean.getTargetY();
        int targetX = bean.getTargetX();

        if (currentY < HW6.width) {
            // Update x position towards the target x
            if (bean.getX() < targetX) {
                bean.setX(bean.getX() + STEP_SIZE);
            } else if (bean.getX() > targetX) {
                bean.setX(bean.getX() - STEP_SIZE);
            }

            // Update y position
            bean.setY(currentY + STEP_SIZE);
        } else {
            // Bean has reached the bottom of the machine
            HW6.score += getTheBeansScore(bean);
            HW6.updateScore();
            killTheBean(bean, 0);
            return false;
        }

        // Checks if the bean is close to any peg's y position
        for (int i = 0; i < HW6.boxes.length; i++) {
            if (Math.abs(currentY - HW6.yVal[i]) < STEP_SIZE) {
                int newX = bean.getX();
                if (rand.nextBoolean()) {
                    newX += (HW6.ballSeparation / 2);
                } else {
                    newX -= (HW6.ballSeparation / 2);
                }
                bean.updateTarget(newX, HW6.yVal[i] + STEP_SIZE);
            }
        }

        return true;
    }

    /**
     * Calculates the score based on the bean's x position.
     * @param bean The bean to evaluate
     * @return The score for the bean
     */
    public int getTheBeansScore(Bean bean) {
        int x = bean.getX();
        int theScore = 0;

        // Uses the defined x coordinates to score
        if (x < 85 || x > 445) {
            theScore += 5;
        } else if (x < 145 || x > 385) {
            theScore += 4;
        } else if (x < 205 || x > 325) {
            theScore += 2;
        } else {
            theScore += 1;
        }

        return theScore;
    }

    /**
     * Marks the bean as not alive, assigns a score, and adds a new bean.
     * @param bean The bean to kill
     * @param score The score to assign to the bean
     */
    public void killTheBean(Bean bean, int score) {
        bean.setAlive(false);
        bean.setScore(score);
        this.score = 0; // Reset score after killing the bean, dead, gone, forgotten
        HW6.addBean();  // grows some more beans
    }

    /**
     * Gets the total score accumulated so far.
     * @return The total score
     */
    public int getTotalScore() {
        return score;
    }
}

