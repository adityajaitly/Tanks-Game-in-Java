package Tanks;

import processing.core.PApplet;
import java.util.ArrayList;



/**
 * The Tank class represents a tank in the game, including its properties and behaviors.
 * This class extends the App class and interacts with the game environment.
 */
public class Tank extends App {
    private PApplet parent;
    private float x;
    private float y;
    private int width;
    private int height;
    private int color;
    private float turretPower = 50; 
    public float stickAngle = 0;
    private int health=100;
    private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    private int fuel=250;
    protected final int HEALTH_BAR_WIDTH = 100;
    public ArrayList<ArrayList<Integer>>[] heightList;
    public ArrayList<Integer>[] xCoordinates;
    private int parachutes ;
    private boolean isParachuteDeployed ;
    private char playerIdentifier; 
    private boolean nextProjectileLarge = false;

   /**
     * Constructs a new Tank instance.
     *
     * @param parent The PApplet instance.
     * @param x The x-coordinate of the tank.
     * @param y The y-coordinate of the tank.
     * @param width The width of the tank.
     * @param height The height of the tank.
     * @param color The color of the tank.
     * @param heightList The list of height coordinates.
     * @param xCoordinates The x-coordinate values.
     * @param initialParachutes The initial number of parachutes.
     * @param playerIdentifier The identifier for the player controlling the tank.
     */


    public Tank(PApplet parent, float x, float y, int width, int height, int color, ArrayList<ArrayList<Integer>>[] heightList, ArrayList<Integer>[] xCoordinates, int initialParachutes, char playerIdentifier) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.heightList = heightList;
        this.xCoordinates = xCoordinates;
        this.parachutes = initialParachutes;
        this.playerIdentifier = playerIdentifier; 
    }

    /**
     * Initializes the tank setup, including loading the parachute image.
     */

    public void setup(){
        //parachuteImage = loadImage("src/main/resources/Tanks/parachute.png");
    }

    /**
     * Moves the tank to the right.
     *
     * @param y The y-coordinate to move to.
     */
    
    // Move tank to the right

    public void moveRight(int y) {
        if(fuel>0){
            this.x = x+5; // Move right by  pixels
            adjustToTerrain();
            fuel--;
        } 

    }

    /**
     * Moves the tank to the left.
     *
     * @param y The y-coordinate to move to.
     */

    // Move tank to the left
    public void moveLeft(int y) {
        if(fuel>0){
            this.x = x-5; // Move left by 10 pixels
            adjustToTerrain();
            fuel--;
            
        }
        
    }  

    /**
     * Gets the x-coordinate of the tank.
     *
     * @return The x-coordinate.
     */
    
    public float getX(){
        return x;
    }

    /**
     * Gets the y-coordinate of the tank.
     *
     * @return The y-coordinate.
     */

    public float getY(){
        return y;
    }

    /**
     * Sets the y-coordinate of the tank.
     *
     * @param y The new y-coordinate.
     */

    public void setY(int y){
        this.y = y;

    }

    /**
     * Gets the current fuel level of the tank.
     *
     * @return The fuel level.
     */

    public int getFuel() {
        return fuel;
    }

    /**
     * Sets the fuel level of the tank.
     *
     * @param fuel The new fuel level.
     */

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    /**
     * Gets the current health of the tank.
     *
     * @return The health level.
     */

    public int getHealth() {
        return health;
    }

    /**
     * Sets the health level of the tank.
     *
     * @param health The new health level.
     */

    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Sets the angle of the turret stick.
     *
     * @param angle The new angle.
     */


    public void setStickAngle(float angle) {
        // Limit the angle of rotation of the turret to the range of -PI/2 to PI/2
        float restrictedAngle = parent.constrain(angle, -parent.HALF_PI, parent.HALF_PI);
        this.stickAngle = restrictedAngle;
    }

    /**
     * Gets the current angle of the turret stick.
     *
     * @return The angle of the turret stick.
     */
    

    public float getStickAngle(){
        return this.stickAngle;
    }

    /**
     * Changes the angle of the turret stick by a given amount.
     *
     * @param angleChange The amount to change the angle by.
     */

    public void changeStickAngle(float angleChange) {
        setStickAngle(getStickAngle() + angleChange);
    }

    /**
     * Gets the height of the tank.
     *
     * @return The height of the tank.
     */

    public int getHeight() {
        return height;
    }

    /**
     * Gets the current power level of the turret.
     *
     * @return The turret power level.
     */

    public float getPower() {
        return turretPower;
    }

    /**
     * Sets the power level of the turret.
     *
     * @param turretPower The new turret power level.
     */

    public void setPower(float turretPower) {
        this.turretPower = turretPower;
    }

    /**
     * Gets the number of parachutes the tank has.
     *
     * @return The number of parachutes.
     */

    public int getParachutes(){
        return parachutes;
    }

    /**
     * Sets the number of parachutes the tank has.
     *
     * @param parachutes The new number of parachutes.
     */

    public void setParachutes(int parachutes) {
        this.parachutes = parachutes;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    /**
     * Checks if the tank is still alive.
     *
     * @return True if the tank's health is greater than 0, false otherwise.
     */

    public boolean isAlive() {
        return health > 0;
    }

     /**
     * Checks if the tank is on the ground.
     *
     * @return True if the tank is on the ground, false otherwise.
     */

    public boolean isOnGround() {
        return y >= App.HEIGHT - height; 
    }

    /**
     * Gets the player identifier for the tank.
     *
     * @return The player identifier.
     */

    public char getPlayerIdentifier() {
        return this.playerIdentifier;
    }

    /**
     * Gets the color of the tank.
     *
     * @return The color of the tank.
     */

    public int getColor() {
        return this.color;
    }

    /**
     * Adjusts the tank's position to follow the terrain.
     */

    public void adjustToTerrain() {
        int terrainY = TerrainHeightAtX((int) this.x, heightList, xCoordinates);
        if (terrainY != -1) {
            this.y = terrainY - this.height + 5; // Adjust the Y position with a small offset to ensure it follows the terrain correctly
        }
    }

    /**
     * Gets the width of the tank.
     *
     * @return The width of the tank.
     */

    public int getWidth() {
        return width;
    }

    /**
     * Checks if the next projectile is large.
     *
     * @return True if the next projectile is large, false otherwise.
     */

    public boolean isNextProjectileLarge() {
        return nextProjectileLarge;
    }

    /**
     * Sets whether the next projectile is large.
     *
     * @param nextProjectileLarge The new value.
     */

    public void setNextProjectileLarge(boolean nextProjectileLarge) {
        this.nextProjectileLarge = nextProjectileLarge;
    }

    /**
     * Draws the tank.
     */


    public void draw() {
        parent.noStroke();
        
        // Draw the tank base
        float baseHeight = height / 3;
        float baseY = y + height - baseHeight;
        parent.fill(color);
        parent.rect(x, baseY, width, baseHeight);
        
        
        float turretWidth = width / 2;
        float turretHeight = height / 3;
        float turretX = x + (width - turretWidth) / 2;
        float turretY = baseY - turretHeight / 2;
        parent.rect(turretX, turretY, turretWidth, turretHeight);
        
        // Draw the turret
        float barrelHeight = height / 1.5f;
        parent.fill(40);  // Dark gray color for the barrel
        
        // Draw the turret with the specified angle
        parent.pushMatrix();
        parent.translate(x + width / 2, turretY + turretHeight / 2);
        parent.rotate(stickAngle);
        parent.fill(0);  // Black color for the turret
        float stickWidth = width / 6;
        float stickHeight = height / 2;
        float stickTopY = -turretHeight / 2;
        parent.rect(-stickWidth / 2, stickTopY - stickHeight, stickWidth, barrelHeight);
        parent.popMatrix();

        if (isParachuteDeployed && parachuteImage != null) {
            // Draw the parachute above the tank
            parent.image(parachuteImage, x + width / 2 - parachuteImage.width / 2, y - parachuteImage.height);
        }

    }

    /**
     * Fires a projectile from the tank.
     */



    public void fire() {
        float angle = stickAngle; 
        int power = (int) turretPower;
        float startX = x + (30 * PApplet.cos(angle));
        float startY = y - (30 * PApplet.sin(angle));
        float initialVelocityMultiplier = 1.25f;
        float initialVelocityMultiplier2 = 1.05f;
        
        float size = isNextProjectileLarge() ? 28 : 14; // Use a larger size if the next projectile is large
        float radius = isNextProjectileLarge() ? 60 : 30; // Set radius based on projectile size
        
        if(power <= 30){
            float initialVx = (float) (initialVelocityMultiplier * power / 1.5 * Math.sin(angle));
            float initialVy = (float) (-initialVelocityMultiplier * power / 1.5 * Math.cos(angle));
            projectiles.add(new Projectile(parent, startX, startY, initialVx, initialVy, color, playerIdentifier, size, radius));
        } else {
            float initialVx = (float) (initialVelocityMultiplier2 * power / 2.25 * Math.sin(angle));
            float initialVy = (float) (-initialVelocityMultiplier2 * power / 2.25 * Math.cos(angle));
            projectiles.add(new Projectile(parent, startX, startY, initialVx, initialVy, color, playerIdentifier, size, radius));
        }
    
        // Reset the flag after firing
        setNextProjectileLarge(false);
    }

    /**
     * Updates the state of the projectiles fired by the tank.
     */
    

    void updateProjectiles() {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update();
            if (p.isOffscreen()) {
                projectiles.remove(i);
            } else {
                int terrainY = TerrainHeightAtX((int) p.getx(), this.heightList, this.xCoordinates);
                if (p.gety() >= terrainY) {
                    if (this.parent instanceof App) {
                        App app = (App) this.parent;
                        Scoreboard scoreboard = app.getScoreboard();
                        Explosion explosion = new Explosion(app, p.getx(), p.gety(), p.getRadius(), scoreboard, p.firingPlayerIdentifier);
                        app.explosions.add(explosion);
    
                        app.collisionUpdate((int) p.getx(), (int) p.gety());
                    }
                    projectiles.remove(i);
                }
            }
        }
    }
    
    /**
     * Draws the projectiles fired by the tank.
     */

    void drawProjectiles() {
        for (Projectile p : projectiles) {
            p.draw();
        }
    }

    /**
     * Deploys a parachute for the tank.
     */

    public void deployParachute() {
        if (parachutes > 0 && !isParachuteDeployed) {
            isParachuteDeployed = true;
            parachutes--;
        }
    }

    /**
     * Resets the parachute deployment state.
     */

    public void resetParachute() {
        isParachuteDeployed = false;
    }

    /**
     * Checks if the parachute is deployed.
     *
     * @return True if the parachute is deployed, false otherwise.
     */

    public boolean isParachuteDeployed() {
        return isParachuteDeployed;
    }

    
}


    

