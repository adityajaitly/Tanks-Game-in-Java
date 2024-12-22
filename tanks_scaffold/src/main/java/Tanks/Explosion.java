package Tanks;

import processing.core.PApplet;

/**
 * The Explosion class represents an explosion in the game, including its properties and behaviors.
 * It handles the explosion's animation, damage application, and drawing.
 */

public class Explosion extends App {
    private PApplet parent;
    private float x, y; // Center of the explosion
    private float maxRadius; // Maximum radius of the explosion
    private float currentRadius = 0; // Current radius during animation
    private int duration = 200; // Duration of the explosion in milliseconds
    private long startTime; // Start time of the explosion
    private Scoreboard scoreboard; 
    private char firingPlayerIdentifier; 

    /**
     * Constructs a new Explosion instance.
     *
     * @param parent The PApplet instance.
     * @param x The x-coordinate of the explosion's center.
     * @param y The y-coordinate of the explosion's center.
     * @param radius The maximum radius of the explosion.
     * @param scoreboard The scoreboard instance for updating scores.
     * @param firingPlayerIdentifier The identifier of the player who initiated the explosion.
     */


    public Explosion(PApplet parent, float x, float y, float radius, Scoreboard scoreboard, char firingPlayerIdentifier) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.maxRadius = radius;
        this.startTime = parent.millis();
        this.scoreboard = scoreboard;
        this.firingPlayerIdentifier = firingPlayerIdentifier;
    }

    /**
     * Updates the explosion's state based on the elapsed time.
     */

    public void update() {
        float elapsed = parent.millis() - startTime;
        if (elapsed < duration) {
            currentRadius = PApplet.map(elapsed, 0, duration, 0, maxRadius);
        } else {
            currentRadius = maxRadius;
        }
    }

    /**
     * Draws the explosion animation.
     */

    public void draw() {
        if (currentRadius > 0) {
            float maxOuterRadius = maxRadius; // Outer circle reaches the full radius
            float maxMiddleRadius = maxRadius * 0.75f; // Middle circle is 75% of full radius
            float maxInnerRadius = maxRadius * 0.5f; // Inner circle is 50% of full radius
    
            // Map current radius to adjusted values for each circle
            float outerRadius = PApplet.map(currentRadius, 0, maxRadius, 0, maxOuterRadius);
            float middleRadius = PApplet.map(currentRadius, 0, maxRadius, 0, maxMiddleRadius);
            float innerRadius = PApplet.map(currentRadius, 0, maxRadius, 0, maxInnerRadius);
    
            // Draw the circles in the desired order (red, orange, yellow)
            parent.noStroke();
            
            // Draw the red circle (outermost)
            parent.fill(255, 0, 0); // Red fill for outermost circle
            parent.ellipse(x, y, outerRadius * 2, outerRadius * 2);
    
            // Draw the orange circle (middle)
            parent.fill(255, 165, 0); // Orange fill for middle circle
            parent.ellipse(x, y, middleRadius * 2, middleRadius * 2);
    
            // Draw the yellow circle (innermost)
            parent.fill(255, 255, 0); // Yellow fill for innermost circle
            parent.ellipse(x, y, innerRadius * 2, innerRadius * 2);
        }
    }

     /**
     * Checks if the explosion animation is done.
     *
     * @return True if the explosion duration has elapsed, false otherwise.
     */
    
    
    

    public boolean isDone() {
        return parent.millis() - startTime > duration;
    }

    

    /**
     * Applies damage to nearby tanks based on their distance from the explosion center.
     *
     * @param tanks The list of tanks to check for damage.
     */


    public void applyDamage(java.util.List<Tank> tanks) {
        for (Tank tank : tanks) {
            float dist = PApplet.dist(tank.getX(), tank.getY(), x, y);
            if (dist < maxRadius) {
                int damage = 60; 
                if (dist > 0) {
                    damage = (int) (60 * (1 - (dist / maxRadius)));
                }
                tank.setHealth(tank.getHealth() - damage);
                if (scoreboard != null) {
                    // Only update the score if the tank being damaged does not belong to the player who fired the projectile
                    if (tank.getPlayerIdentifier() != firingPlayerIdentifier) {
                        scoreboard.updateScore(firingPlayerIdentifier, damage);
                    }
                }
            }
        }
    }

    // Getter methods 

    /**
     * Gets the x-coordinate of the object.
     *
     * @return The x-coordinate.
     */
    public float getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the object.
     *
     * @return The y-coordinate.
     */

    public float getY() {
        return y;
    }

    /**
     * Gets the maximum radius of the explosion.
     *
     * @return The maximum radius.
     */

    public float getMaxRadius() {
        return maxRadius;
    }

    /**
     * Gets the current radius of the explosion.
     *
     * @return The current radius.
     */

    public float getCurrentRadius() {
        return currentRadius;
    }

    /**
     * Gets the identifier of the player who fired the projectile.
     *
     * @return The firing player's identifier.
     */

    public char getFiringPlayerIdentifier() {
        return firingPlayerIdentifier;
    }
    
    

}
