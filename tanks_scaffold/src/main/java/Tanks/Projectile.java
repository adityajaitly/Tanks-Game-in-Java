package Tanks;

import processing.core.PApplet;

/**
 * The Projectile class represents a projectile fired by a tank.
 * It handles the physics, drawing, and properties of the projectile.
 */
public class Projectile extends App {
    private static final float GRAVITY = 3.6f;
    float x, y; // Position
    float vx, vy; // Velocity components
    int color; // Color of the projectile
    PApplet parent; // Reference to PApplet to draw on the canvas
    public static final int CELLSIZE = 32; // Width of each cell in pixels
    public static final int CELLHEIGHT = 32; // Height of each cell in pixels
    public char firingPlayerIdentifier;
    private float size;
    private float radius; // Add radius field

    /**
     * Constructs a new Projectile instance.
     *
     * @param parent The PApplet instance.
     * @param x The initial x position of the projectile.
     * @param y The initial y position of the projectile.
     * @param vx The initial horizontal velocity of the projectile.
     * @param vy The initial vertical velocity of the projectile.
     * @param color The color of the projectile.
     * @param firingPlayerIdentifier The identifier of the player who fired the projectile.
     * @param size The size of the projectile.
     * @param radius The radius of the projectile.
     */

    public Projectile(PApplet parent, float x, float y, float vx, float vy, int color, char firingPlayerIdentifier, float size, float radius) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.firingPlayerIdentifier = firingPlayerIdentifier;
        this.size = size;
        this.radius = radius; // Initialize radius
    }

    /**
     * Updates the position and velocity of the projectile based on gravity and wind.
     */


    void update() {
        x += vx; // Update horizontal position
        y += vy; // Update vertical position

        vx += wind * 0.03; //Applies  Wind effect on the projectile
        // Apply gravity to the vertical velocity
        vy += GRAVITY;
    }

    /**
     * Gets the x position of the projectile.
     *
     * @return The x position of the projectile.
     */


    public float getx(){
        return this.x;
    }

    /**
     * Gets the y position of the projectile.
     *
     * @return The y position of the projectile.
     */

    public float gety(){
        return this.y;
    }

    /**
     * Gets the radius of the projectile.
     *
     * @return The radius of the projectile.
     */

    public float getRadius() {
        return this.radius;
    }

    /**
     * Gets the size of the projectile.
     *
     * @return The size of the projectile.
     */

    public float getSize() {
        return this.size;
    }


    /**
     * Draws the projectile on the canvas.
     */


    public void draw() {
        parent.noStroke();
        parent.fill(0); // Set color to black
        parent.ellipse(x, y, 14, 14); // Draw a slightly larger black ellipse
        parent.fill(color);
        parent.ellipse(x, y, 10, 10); // Draw the projectile
    }

    /**
     * Checks if the projectile is offscreen.
     *
     * @return true if the projectile is offscreen, false otherwise.
     */


    boolean isOffscreen() {
        return x < 0 || x > parent.width || y > parent.height || y < 0; // Also check if y is less than 0
    }

    

 }
    
    



