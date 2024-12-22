package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;



/**
 * The App class is the main entry point for the Tanks game. It extends the PApplet class from the Processing library
 * and manages game initialization, rendering, and logic.
 */


public class App extends PApplet {
   private List<Tank> tanks = new ArrayList<>();

   public static final int CELLSIZE = 32; 
   public static final int CELLHEIGHT = 32;

   public static final int CELLAVG = 32;
   public static final int TOPBAR = 0;
   public List<int[]> treeCoordinates;
   public List<int[]> tankCoordinates; 

   public static int WIDTH = 864;
   public static int HEIGHT = 640; 
   public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
   public static final int BOARD_HEIGHT = 20;

   public ArrayList<Integer>[] xCoordinates = new ArrayList[28];
   // Array of arrays to store y coordinates
   public ArrayList<ArrayList<Integer>>[] yCoordinates = new ArrayList[28];

   public static final int FPS = 30;
   public static int parachutes = 1; 

   public String configPath;

   public static Random random = new Random();
   public PImage[] background; //PImage array for the backgrounds
   public List<String> currentLayout; // String list for the current level 
   public PImage treeImage; // Image for trees
   public int treeColor; 
   public Map<Character, PImage> playerImage; // Map to store player images
   public Map<Character, Integer> playerColor; // Map to store player colors

   public int currentlevel;
   public JSONObject config; //JSONObject reading the json file
   public String[] terrainColor;
   public int redColor;
   public int greenColor;
   public int blueColor;
   public boolean istankinitialised = false; 
   public int currentTankIndex = 0; // Index of the currently controlled tank
   public  int[][] terrainHeightMap;
   
   private static final float ROTATION_SPEED = 3.0f; // Rotation speed in radians per second
   public PImage fuelImage;
   protected List<Explosion> explosions = new ArrayList<>();
   public static PImage parachuteImage;
   public static float wind; // Current wind value
   private PImage windImageRight; // Image for wind blowing to the right
   private PImage windImageLeft; // Image for wind blowing to the left
   private PImage currentWindImage; // Current wind image based on direction

   private Scoreboard scoreboard;
   private HelpBox helpBox;
   private boolean gameOver = false; // To track if the game is over
   private boolean levelEnded = false; // To track if the current level has ended
   private long levelEndTime; // To store the time when the level ended
   private long levelEndDelay = 1000; // 1-second delay before loading the next level
   private List<Map.Entry<Character, Integer>> finalScores; // To store final scores for display
   private long arrowDisplayStartTime = 0;
 


    /**
     * Constructs the App object with the config.json file path.
     */


   public App() {
       this.configPath = "config.json";
   }

   /**
    * Initialise the setting of the window size.
    */
   @Override
   public void settings() {
       size(864, 640);
   }

   /**
    * Load all resources such as images. Initialise the elements such as the player and map elements.
    */
   @Override
   public void setup() {
       frameRate(FPS);

       currentlevel = 0;

       // Load JSON configuration file
       config = loadJSONObject(configPath);

       // Extract levels from JSON file
       JSONArray levels = config.getJSONArray("levels");
       int size = levels.size();
       background = new PImage[size];

       for (int i = 0; i < size; i++) {
           JSONObject level = levels.getJSONObject(i);
           String bgString = level.getString("background");
           String bgPath = "src/main/resources/Tanks/" + bgString;
           background[i] = loadImage(bgPath);
       }

       // Load the layout

       currentLayout = loadLayout(currentlevel);
       fuelImage = loadImage("src/main/resources/Tanks/fuel.png");
       parachuteImage = loadImage("src/main/resources/Tanks/parachute.png");
       windImageRight = loadImage("src/main/resources/Tanks/wind.png");
       windImageLeft = loadImage("src/main/resources/Tanks/wind-1.png");

       wind = random(-35, 35); // Generates a random wind value between -35 and 35
       updateWindImage(); // Sets the appropriate wind image based on the wind direction

 

    // Extract tree image and its color according to the current level 

    if (currentlevel != 1) {
        String treeImagePath = "src/main/resources/Tanks/" + levels.getJSONObject(currentlevel).getString("trees");
        treeImage = loadImage(treeImagePath); // Store the trees image for the current level index
        treeImage.resize(CELLSIZE, CELLHEIGHT);
    }
       

       String color = config.getJSONArray("levels").getJSONObject(currentlevel).getString("foreground-colour");
       this.terrainColor = color.split(","); 
       redColor = Integer.parseInt(terrainColor[0]);
       greenColor = Integer.parseInt(terrainColor[1]);
       blueColor = Integer.parseInt(terrainColor[2]);
       initializeHeightCoordinates(currentLayout);

    scoreboard = new Scoreboard(this);
    for (Tank tank : tanks) {
        scoreboard.registerPlayer(tank.getPlayerIdentifier(), tank.getColor());
    }

    helpBox = new HelpBox(this); // Initialize the help box

    arrowDisplayStartTime = millis();


       
    }

    /**
     * Loads the layout for the specified level.
     *
     * @param index The level index.
     * @return A list of strings representing the layout.
     */


   public List<String> loadLayout(int index) {
       List<String> layout = new ArrayList<>();
       try {
           // Read the .txt file for the given level
           String levelName = "level" + (index + 1) + ".txt";
           BufferedReader reader = createReader(levelName); // BufferedReader reads from a character input stream
           String line;
           while ((line = reader.readLine()) != null) {
               layout.add(line);
           }
           reader.close();
       } catch (Exception e) {
           e.printStackTrace();
       }

       return layout;
   }

   /**
     * Initializes the height coordinates for the terrain based on the layout.
     * 
     * If 'X' is found it depicts the terrain and 'T' depicts trees
     * @param layout The layout of the current level.
     */


    public void initializeHeightCoordinates(List<String> layout) {
        treeCoordinates = new ArrayList<>(); // Initialize the list here

        // Initializing the arrays
        for (int i = 0; i < 28; i++) {
            xCoordinates[i] = new ArrayList<>();
            yCoordinates[i] = new ArrayList<>();
        }

        // Processing the layout to find 'X' and 'T'
        for (int y = 0; y < layout.size(); y++) {
            String line = layout.get(y);
            for (int x = 0; x < line.length(); x++) {
                char part = line.charAt(x);
                if (part == 'X' && x < 28) {
                    xCoordinates[x].add(x * CELLSIZE);
                    
                    ArrayList<Integer> newYList = new ArrayList<>();
                    for (int j = 0; j < 32; j++) {
                        newYList.add(y * CELLHEIGHT);
                    }
                    yCoordinates[x].add(newYList);
                } else if (part == 'T' && currentlevel != 1) {
                    treeCoordinates.add(new int[]{x * CELLSIZE, y * CELLSIZE});
                }
            }
        }
        updateYCoordinates(yCoordinates);
        updateYCoordinates(yCoordinates);

    }

    /**
     * Draws the terrain based on the height coordinates.
     */


    public void drawTerrain() {
        noStroke();  // Disable outlines for the rectangles drawn for the terrain
        fill(redColor, greenColor, blueColor);  // Setting the color of the rectangles
    
        // Draw rectangles based on yCoordinates
        int x = 0;
        for (ArrayList<ArrayList<Integer>> yListArray : yCoordinates) {
            if (yListArray != null && !yListArray.isEmpty()) {
                for (ArrayList<Integer> yList : yListArray) {
                    for (Integer yCoord : yList) {
                        // Draw a rectangle with width of 1 pixel
                        rect(x, yCoord, 1, HEIGHT - yCoord);
                        x++;
                    }
                }
            }
            // Adjusting the x position to align to the nearest cell boundary after drawing all lines in a column
            x = (x + CELLSIZE - 1) / CELLSIZE * CELLSIZE;
        }
    
        // Drawing trees based on treeCoordinates
        for (int[] treeCoord : treeCoordinates) {
            if (treeCoord != null && treeCoord.length == 2) {
                int index = treeCoord[0] / 32;
                int yCoord = yCoordinates[index].get(0).get(16);
                image(treeImage, treeCoord[0], yCoord - 25);
            }
        }
    
        // Initialize tanks if not already initialized
        if (!istankinitialised) {
            createTanksFromLayout();
        }
    }

   /**
     * Updates the y coordinates to smooth out the terrain by taking moving average of 32 values twice.
     *
     * @param yCoordinates The y coordinates of the terrain.
     */
    

   
   public void updateYCoordinates(ArrayList<ArrayList<Integer>>[] yCoordinates) {

    // smooth the terrain by calculating the moving average of 32 values twice 
       for (int i = 0; i < yCoordinates.length - 1; i++) {

           // Create a new ArrayList to store the smoothed y-coordinates for the current segment
           ArrayList<ArrayList<Integer>> newYCoordinates = new ArrayList<>();

           for (int j = 0; j < yCoordinates[i].size(); j++) {
               ArrayList<Integer> newYList = new ArrayList<>();
               for (int k = 0; k < yCoordinates[i].get(j).size(); k++) {
                   int sum = 0;
                   int count = 0;

                   // Calculate the sum of up to 32 y-coordinates starting from the current position k
                   // within the current segment
                   for (int m = k; m < yCoordinates[i].get(j).size() && count < 32; m++) {
                       sum += yCoordinates[i].get(j).get(m);
                       count++;
                   }

                   // If less than 32 values were summed, continue summing from the next segment
                   for (int m = 0; count < 32 && m < yCoordinates[i + 1].get(j).size(); m++) {
                       sum += yCoordinates[i + 1].get(j).get(m);
                       count++;
                   }
                   newYList.add(sum / 32);
               }
               newYCoordinates.add(newYList);
               
           }
           yCoordinates[i] = newYCoordinates;
       }  
       
   }

   /**
     * Creates tanks based on the layout.
     */


void createTanksFromLayout() {
    tankCoordinates = new ArrayList<>();

    // First loop: Collects all tank coordinates based on character parts
    for (int y = 0; y < currentLayout.size(); y++) {
        String line = currentLayout.get(y);
        for (int x = 0; x < line.length(); x++) {
            char part = line.charAt(x);
            if (!config.getJSONObject("player_colours").isNull(Character.toString(part))) {
                tankCoordinates.add(new int[]{x * CELLSIZE, y * CELLHEIGHT, part}); // Add part to the array
            }
        }
    }

    // Sorts the tankCoordinates based on the x coordinates (from left to right)
    Collections.sort(tankCoordinates, new Comparator<int[]>() {
        @Override
        public int compare(int[] a, int[] b) {
            return a[0] - b[0]; // Compare x coordinates
        }
    });

    for (int[] tankCoord : tankCoordinates) {
        if (tankCoord != null && tankCoord.length == 3) {
            int x = tankCoord[0] - 10;
            int terrainY = TerrainHeightAtX(x, yCoordinates, xCoordinates) - 25; // Adjust Y position for the tank
            char playerIdentifier = (char) tankCoord[2]; // Use the character as player identifier
            int tankColor = parseColor(playerIdentifier); // Get color based on the player identifier
            if (terrainY != -1) { // Check if terrain height is valid
                // Pass the initial value of 3 for parachutes and include player identifier
                Tank tank = new Tank(this, x, terrainY, CELLSIZE, CELLHEIGHT, tankColor, yCoordinates, xCoordinates, 3, playerIdentifier);
                tank.adjustToTerrain(); // Ensure the tank is correctly placed on the terrain
                tanks.add(tank);
            }
        }
    }

    istankinitialised = true; // tank is initialised
}



   /**
     * Parses the color for a tank based on the player identifier.
     *
     * @param playerIdentifier The player identifier.
     * @return The color as an integer.
     * 
     */



int parseColor(char playerIdentifier) {
    // Parsing color from JSON based on player identifier
    String colorString = config.getJSONObject("player_colours").getString(Character.toString(playerIdentifier));
    String[] components = colorString.split(",");
    int r = Integer.parseInt(components[0]);
    int g = Integer.parseInt(components[1]);
    int b = Integer.parseInt(components[2]);
    return color(r, g, b);
}



   /**
     * Calculates the terrain height at a given x-coordinate.
     *
     * @param x           The x-coordinate.
     * @param heightsList The list of height coordinates.
     * @param xValues     The x-coordinate values.
     * @return The terrain height at the given x-coordinate.
     */

   

    public int TerrainHeightAtX(int x, ArrayList<ArrayList<Integer>>[] heightsList, ArrayList<Integer>[] xValues) {
        int xIndex = x / 32; // Calculate the index of the x-coordinate
        if (xIndex < xValues.length) { // Check if xIndex is within the bounds of the xValues array
            ArrayList<ArrayList<Integer>> listOfHeights = heightsList[xIndex];
            if (!listOfHeights.isEmpty()) {
                ArrayList<Integer> heights = listOfHeights.get(0);
                int heightIndex = x % 32; // Calculating the specific index of the height within the list
                if (heightIndex < heights.size()) {
                    return heights.get(heightIndex); // Return the height at the specific index
                }
            }
        }
        return -1;
    }

    /**
     * Draws the health bar for the current tank.
     */


    public void drawHealthBar() {
        if (tanks.isEmpty()) {
            return; 
        }
        
        Tank currentTank = tanks.get(currentTankIndex);
        float healthWidth = map(currentTank.getHealth(), 0, 100, 0, 200);
        float healthBarX = 400;
        float healthBarY = 17;
    
        // Draw the grey background rectangle with a grey border
        fill(255);  // White color for the lost health
        stroke(192);  // Grey color for the border
        rect(healthBarX, healthBarY, 200, 20);
    
        // Draw the filled portion representing health
        noStroke();
        fill(currentTank.getColor());
        rect(healthBarX, healthBarY, healthWidth, 20);
    
        // Draw the power indicator line
        float powerIndicatorX = healthBarX + map(currentTank.getPower(), 0, 100, 0, 200);
        stroke(76, 153, 0);  // Power indicator line 
        line(powerIndicatorX, healthBarY, powerIndicatorX, healthBarY + 20);
    
        // Draw the border of the health bar
        noFill();
        stroke(0);
        rect(healthBarX, healthBarY, 200, 20);
    
        // Draw the health text
        fill(0);
        String healthLabel = "Health: ";
        String healthValue = Integer.toString((int) currentTank.getHealth());
        String healthText = healthLabel + healthValue;
        textSize(18);
        textAlign(RIGHT);
        text(healthText, healthBarX - 10, 35);
    }
    
    /**
     * Draws the power indicator for the current tank.
     */
    
    public void drawPower() {
        // Set the fill color to black

        if (tanks.isEmpty()) return;  // Exit if there are no tanks to handle.
        fill(0);
    
        // Get the power of the current tank
        Tank currentTank = tanks.get(currentTankIndex);
        int power = (int) currentTank.getPower(); // Casting the power to integer
    
        // Display the power label and value
        String powerLabel = "Power: ";
        String powerValue = Integer.toString(power); // Converting integer to string
        String powerText = powerLabel + powerValue;
        // Displays the power text above the health bar
        textSize(18);
        textAlign(LEFT);
        text(powerText, 289, 54); 
    }

    /**
     * Updates the terrain after a collision.
     *
     * @param x The x-coordinate of the collision.
     * @param y The y-coordinate of the collision.
     */



    public void collisionUpdate(int x, int y) {
        int radius = 30;
    
        // We need to consider that yCoordinates is an array, so we use .length for arrays
        for (int i = x - radius; i <= x + radius; i++) {
            if (i >= 0 && i < yCoordinates.length * 32) {
                int arrayIndex = i / 32; // Determines which ArrayList of ArrayLists to access
                int valueIndex = i % 32; // Determines which Integer in the inner ArrayList to access
    
                // Calculate the distance from the point of collision
                int distance = Math.abs(x - i);
    
                // Calculate the new height based on the distance from the point of collision
                int newHeight = y + (int) Math.sqrt(Math.pow(radius, 2) - Math.pow(distance, 2));
    
                // Update the height if the new height is greater than the current height
                ArrayList<Integer> heights = yCoordinates[arrayIndex].get(0); // Accessing the inner ArrayList
                if (newHeight > heights.get(valueIndex)) {
                    heights.set(valueIndex, newHeight);
                }
            }
        }
        drawTerrain(); // Assuming drawTerrain is implemented elsewhere to redraw the terrain
    }

    /**
     * Updates the state of a tank, including handling parachute deployment and fall damage.
     *
     * @param tank The tank to update.
     */



    public void updateTankState(Tank tank) {
        int terrainHeight = TerrainHeightAtX((int) tank.getX(), yCoordinates, xCoordinates);
        boolean isMidair = tank.getY() + tank.getHeight() < terrainHeight;
    
        if (isMidair) {
            if (!tank.isParachuteDeployed() && tank.getParachutes() > 0) {
                tank.deployParachute();
                // Descends with parachute at 60 pixels per second and type casting the result to integer
                int newY = (int) (tank.getY() + 60.0 / FPS);
                tank.setY(newY);
            } else {
                // No parachute available or already deployed, falls faster
                double descentIncrement;
                if (tank.isParachuteDeployed()) {
                    descentIncrement = 60.0 / FPS;
                } else {
                    descentIncrement = 120.0 / FPS;
                }
                int newY = (int) (tank.getY() + descentIncrement);
                tank.setY(newY);
    
                // Checks if tank is still above the terrain
                if (tank.getY() + tank.getHeight() >= terrainHeight) {
                    // Hits the ground without a parachute and calculate damage from the fall
                    int fallHeight = (int) (tank.getY() + tank.getHeight() - terrainHeight);
                    tank.setHealth(tank.getHealth() - fallHeight); // 1hp damage per pixel fallen
                }
            }
        } else {

            int newY = terrainHeight - tank.getHeight();
            tank.setY(newY);
            tank.resetParachute();
        }
    
        // Check if the tank is below the window and not supported by terrain
        if (tank.getY() + tank.getHeight() >= HEIGHT && terrainHeight < 0) {
            tank.setHealth(0); // Set health to 0 to mark it for removal
        }
    }

    /**
     * Updates the wind image based on the current wind direction.
     */
    
    public void updateWindImage() {
        if (wind < 0) {
            currentWindImage = windImageLeft;
        } else {
            currentWindImage = windImageRight;
        }
    }

    /**
     * Manages the explosions, updating and drawing them, and applying damage to tanks.
     */

    public void manageExplosions() {
        Iterator<Explosion> iterator = explosions.iterator();
        while (iterator.hasNext()) {
            Explosion explosion = iterator.next();
            explosion.update();
            explosion.draw();
            if (explosion.isDone()) {
                explosion.applyDamage(tanks);
                iterator.remove(); // Remove explosion after it finishes
            }
        }
    }


    /**
     * Manages the tanks, updating their state and drawing them.
     */


    public void manageTanks() {
        Iterator<Tank> iterator = tanks.iterator();
        while (iterator.hasNext()) {
            Tank tank = iterator.next();
            updateTankState(tank); // Update the state of the tank, adjust position or check conditions
            if (tank.getHealth() <= 0 || (tank.isOnGround() && TerrainHeightAtX((int) tank.getX(), yCoordinates, xCoordinates) < 0)) {
                float explosionRadius = 30; // Standard explosion radius
                explosions.add(new Explosion(this, tank.getX(), tank.getY(), explosionRadius, scoreboard, tank.getPlayerIdentifier()));
                iterator.remove(); // Remove the tank safely using iterator
            } else {
                tank.draw(); // Draw the tank if it's still active
            }
        }
    
        // Update the current tank index to avoid out-of-bound errors
        if (currentTankIndex >= tanks.size() && !tanks.isEmpty()) {
            currentTankIndex = 0; // Reset to first tank or any valid index
        }
    }
    
    
    /**
     * Gets the scoreboard.
     *
     * @return The scoreboard.
     */

    public Scoreboard getScoreboard() {
        return this.scoreboard;  // Getter for the scoreboard
    }

    public List<Tank> getTanks() {
        return tanks;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public boolean isGameOver() {
        return gameOver;
    }
 
    /**
     * Checks if the current level has ended.
     */
    

    public void checkLevelEnd() {
        if (tanks.size() == 1 && !levelEnded) {
            levelEnded = true;
            levelEndTime = millis(); // Capture the time when the level ended
        }
    }

    /**
     * Loads the next level.
     */
    
    
    public void loadNextLevel() {
        if (currentlevel < background.length - 1) {
            currentlevel++;
            prepareNextLevel(); // Call this to set up the next level
        } else {
            endGame(); // End the game if there are no more levels
        }
    }

    /**
     * Prepares the game for the next level.
     * <p>
     * This method clears the current game state, loads the configuration for the next level,
     * updates the background image, foreground color, and tree image (if applicable), and initializes
     * the new level layout, terrain, and tanks. If there are no more levels, the game ends.
     */


    public void prepareNextLevel() {
        // Clear existing game state
        explosions.clear();
        tanks.clear();
    
        // Check if the next level exists
        if (currentlevel < background.length) {
            // Update the background image according to the new level
            JSONObject levelConfig = config.getJSONArray("levels").getJSONObject(currentlevel);
            background[currentlevel] = loadImage("src/main/resources/Tanks/" + levelConfig.getString("background"));
    
            // Update the foreground color according to the new level
            String[] color = levelConfig.getString("foreground-colour").split(",");
            redColor = Integer.parseInt(color[0]);
            greenColor = Integer.parseInt(color[1]);
            blueColor = Integer.parseInt(color[2]);
    
            // Update tree image, if applicable
            if (!levelConfig.isNull("trees")) {
                String treeImagePath = "src/main/resources/Tanks/" + levelConfig.getString("trees");
                treeImage = loadImage(treeImagePath);
                treeImage.resize(CELLSIZE, CELLHEIGHT);
            } else {
                treeImage = null; // Clear tree image if not used in the level
            }
    
            // Load and initialize the layout
            currentLayout = loadLayout(currentlevel);
            initializeHeightCoordinates(currentLayout);
            createTanksFromLayout();
            // draw terrain based on the new layout
            drawTerrain();
    
            levelEnded = false;  // Reset the flag indicating the level is ongoing
        } else {
            endGame();
        }
    }

    
    
    

    /**
     * Ends the game and displayes the final scores.
     */
    
   

    public void endGame() {
        gameOver = true; // Mark the game as over
        finalScores = new ArrayList<>(scoreboard.getScores().entrySet());
        finalScores.sort((a, b) -> b.getValue().compareTo(a.getValue())); // Sort scores in descending order
    
        displayWinner(); // Display the final scores
    }

    /**
     * Displays the winner and the final scores.
     */
    
     public void displayWinner() {
        fill(0); 
        textSize(36);
        textAlign(CENTER, CENTER);
    
        if (!finalScores.isEmpty()) {
            text("Player " + finalScores.get(0).getKey() + " wins!", width / 2, height / 2 - 50);
    
            textSize(24);
            float yPos = height / 2 + 20;
            for (Map.Entry<Character, Integer> score : finalScores) {
                int color = scoreboard.getColor(score.getKey());
                fill(color, 127); // Color slightly lighter and transparent
                text("Player " + score.getKey() + ": " + score.getValue(), width / 2, yPos);
                yPos += 30;
                delay(700); // Delay between showing scores
            }
        }
    }

    
    
    /**
     * Restarts the game.
     */


    public void restartGame() {
        gameOver = false;
        levelEnded = false;
        currentlevel = 0; // Ensure you start from the first level
    
        // Load and initialize layout and settings for the first level
        currentLayout = loadLayout(currentlevel);
        initializeHeightCoordinates(currentLayout);
    
        // Reset the images and color for terrain and trees based on the first level
        JSONObject levelConfig = config.getJSONArray("levels").getJSONObject(currentlevel);
        background[currentlevel] = loadImage("src/main/resources/Tanks/" + levelConfig.getString("background"));
        String[] color = levelConfig.getString("foreground-colour").split(",");
        redColor = Integer.parseInt(color[0]);
        greenColor = Integer.parseInt(color[1]);
        blueColor = Integer.parseInt(color[2]);
        if (!levelConfig.isNull("trees")) {
            String treeImagePath = "src/main/resources/Tanks/" + levelConfig.getString("trees");
            treeImage = loadImage(treeImagePath);
            treeImage.resize(CELLSIZE, CELLHEIGHT);
        } else {
            treeImage = null; // No trees in the first level
        }
    
        // Clear existing game state
        tanks.clear();
        explosions.clear();
        createTanksFromLayout();
        drawTerrain();
    
        // Reset the scoreboard and scores
        scoreboard.resetScores();
        finalScores = null;
    }
    

    /**
     * Draws an arrow above the tank taking the turn.
     *
     * @param tank The tank to draw the arrow above.
     */


    public void drawArrowAboveTank(Tank tank) {
        float arrowX = tank.getX() + tank.getWidth() / 2;
        float arrowY = tank.getY() - tank.getHeight() - 10;
        // Drawing the arrow 
        line(arrowX, arrowY - 25, arrowX, arrowY); 
        line(arrowX, arrowY, arrowX - 7, arrowY - 7); 
        line(arrowX, arrowY, arrowX + 7, arrowY - 7); 
    }

    /**
     * Displays an indicator if the current tank has a larger projectile ready.
     *
     * @param currentTank The current tank.
     */

    public void displayLargerProjectileIndicator(Tank currentTank) {
        if (currentTank.isNextProjectileLarge()) {
            fill(255, 0, 0); // Red color for indication
            textSize(18);
            textAlign(LEFT);
            text("Larger Projectile Ready!", 10, height - 10); // Display at the bottom-left of the screen
        }
    }
    
    



  /**
    * Receive key pressed signal from the keyboard.
    */

@Override
public void keyPressed() {
    Tank currentTank = tanks.get(currentTankIndex);
    char playerIdentifier = currentTank.getPlayerIdentifier();
    int currentScore = scoreboard.getScore(playerIdentifier);

    if (gameOver) {
        if (key == 'r') {
            restartGame();
            return;
        }
    } else {
        if (keyCode == RIGHT) {
            int newY = TerrainHeightAtX((int) currentTank.getX(), yCoordinates, xCoordinates);
            currentTank.moveRight(newY);
        } else if (keyCode == LEFT) {
            int newY = TerrainHeightAtX((int) currentTank.getX(), yCoordinates, xCoordinates);
            currentTank.moveLeft(newY);
        } else if (keyCode == UP || keyCode == DOWN) {
            float angleChange = 0;
            if (keyCode == UP) {
                angleChange = ROTATION_SPEED / FPS;
            } else if (keyCode == DOWN) {
                angleChange = -ROTATION_SPEED / FPS;
            }
            currentTank.changeStickAngle(angleChange);
        } else if (keyCode == ' ') {
            currentTank.fire();
            currentTankIndex = (currentTankIndex + 1) % tanks.size();
            wind += random(-5, 5);
            wind = constrain(wind, -35, 35);
            updateWindImage();
            arrowDisplayStartTime = millis();
        } else if (keyCode == 'W' || keyCode == 'w') {
            float newPower = Math.min(currentTank.getPower() + (36.0f / FPS), currentTank.getHealth());
            currentTank.setPower(newPower);
        } else if (keyCode == 'S' || keyCode == 's') {
            float newPower = Math.max(currentTank.getPower() - (36.0f / FPS), 0);
            currentTank.setPower(newPower);
        } else if (key == 'r' && currentScore >= 20) {
            if (currentTank.getHealth() < 100) {
                currentTank.setHealth(Math.min(currentTank.getHealth() + 20, 100));
                scoreboard.updateScore(playerIdentifier, -20);
            }
        } else if (key == 'f' && currentScore >= 10) {
            currentTank.setFuel(currentTank.getFuel() + 200);
            scoreboard.updateScore(playerIdentifier, -10);
        } else if (key == 'p' && currentScore >= 15) {
            currentTank.setParachutes(currentTank.getParachutes() + 1);
            scoreboard.updateScore(playerIdentifier, -15);
        } else if (key == 'x' && currentScore >= 20) {
            currentTank.setNextProjectileLarge(true);
            scoreboard.updateScore(playerIdentifier, -20);
        }
    }
}



  /**
    * Receive key released signal from the keyboard.
    */

    
   @Override
   public void keyReleased(){
    

   }

   /**
     * Handles mouse pressed events.
     *
     * @param e The mouse event.
     */

   @Override
   public void mousePressed(MouseEvent e) {
       //TODO - powerups, like repair and extra fuel and teleport
       if (helpBox.isInside(mouseX, mouseY)) {
        helpBox.toggle();
       }


   }

   /**
     * Handles mouse released events.
     *
     * @param e The mouse event.
     */

   @Override
   public void mouseReleased(MouseEvent e) {

   }

   /**
    * Draw all elements in the game by current frame.
    */
   @Override
   public void draw() {

        if (gameOver) {
            displayWinner();
            return;
        }
    
       image(background[currentlevel], 0, 0);

       manageTanks();
       manageExplosions();


    if (!tanks.isEmpty() && currentTankIndex < tanks.size()) {
        for (Tank tank : tanks) {
            updateTankState(tank);  // Ensures updateTankState method handles empty scenarios
            tank.draw();
        }
    }
       drawTerrain();

       //----------------------------------
       //display HUD:
       //----------------------------------

       if (!tanks.isEmpty() && currentTankIndex < tanks.size()){

        String playerTurnLabel = "Player " + (char)('A' + currentTankIndex) + "'s Turn";
       fill(0); // Set text color to black
       textSize(18); // Set text size
       textAlign(LEFT); // Align text to the left
       text(playerTurnLabel, 30, 32); // Adjust position as needed
       if (fuelImage != null) {
        
        image(fuelImage, 175, 10, 27, 27); 
       }

       if (parachuteImage != null) {
        
        image(parachuteImage, 175, 47, 27, 27); 
       }

       if (currentWindImage != null) {
        
        image(currentWindImage, WIDTH - 195, 8, 50, 50); 
       }


       drawHealthBar();
       drawPower();

       if (millis() - arrowDisplayStartTime < 1000) {
        Tank currentTank = tanks.get(currentTankIndex);
        drawArrowAboveTank(currentTank);
    }


       int x = 205;
       for(Tank tank:tanks){
        tank.draw();
        tank.updateProjectiles();
        tank.drawProjectiles();
        
        

        if (tanks.indexOf(tank) == currentTankIndex) {
            fill(0); // Set text color to black
            textSize(21); // Set text size
            text(tank.getFuel(), x, 34); // Adjust position as needed
        }

        if (tanks.indexOf(tank) == currentTankIndex) {
            fill(0); // Set text color to black
            textSize(21); // Set text size
            text(tank.getParachutes(), x, 70); // Adjust position as needed
        }

        if (tanks.indexOf(tank) == currentTankIndex) {
            fill(0); // Set text color to black
            textSize(21); // Set text size
            text((int) wind, WIDTH - 140,39); // Adjust position as needed
        }


       }

       

        scoreboard.draw();
        helpBox.draw();
        
        

       }

       Tank currentTank = tanks.get(currentTankIndex);
       displayLargerProjectileIndicator(currentTank);

       if (levelEnded) {

        if (millis() - levelEndTime >= levelEndDelay) {
            loadNextLevel();
        }

    } 
        
      else {
        checkLevelEnd();
    }

   }


   /**
     * The main method to run the application.
     *
     * @param args The command line arguments.
     */


   public static void main(String[] args) {
       PApplet.main("Tanks.App");
   }

}

