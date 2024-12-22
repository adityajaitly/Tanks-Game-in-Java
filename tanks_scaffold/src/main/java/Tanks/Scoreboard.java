package Tanks;

import processing.core.PApplet;

import java.util.HashMap;
import java.util.Map;

/**
 * The Scoreboard class manages player scores and their corresponding colors.
 * It provides methods to register players, update scores, reset scores, and draw the scoreboard.
 */

public class Scoreboard extends App {
    private PApplet parent;
    private Map<Character, Integer> scores;
    private Map<Character, Integer> colors;

    /**
     * Constructs a new Scoreboard instance.
     *
     * @param parent The PApplet instance.
     */

    public Scoreboard(PApplet parent) {
        this.parent = parent;
        scores = new HashMap<>();
        colors = new HashMap<>();
        registerPlayer('A', parent.color(0, 0, 255)); 
        registerPlayer('B', parent.color(255, 0, 0));  
        registerPlayer('C', parent.color(0, 255, 255));  
        registerPlayer('D', parent.color(255, 255, 0)); 
    }

    /**
     * Registers a player with a given identifier and color.
     *
     * @param playerIdentifier The identifier for the player.
     * @param color The color associated with the player.
     */

    public void registerPlayer(char playerIdentifier, int color) {
        scores.put(playerIdentifier, 0);  // Initialize score as 0
        colors.put(playerIdentifier, color);
        
    }

    /**
     * Updates the score for a given player.
     *
     * @param playerIdentifier The identifier for the player.
     * @param score The score to add to the player's current score.
     */

    public void updateScore(char playerIdentifier, int score) {
        if (scores.containsKey(playerIdentifier)) {
            scores.put(playerIdentifier, scores.get(playerIdentifier) + score);
            
        }
    }

    /**
     * Gets the score for a given player.
     *
     * @param playerIdentifier The identifier for the player.
     * @return The score for the player, or 0 if the player is not found.
     */

    public int getScore(char playerIdentifier) {
        return scores.getOrDefault(playerIdentifier, 0); // Return the score for the player or 0 if not found
    }

    /**
     * Gets the map of all player scores.
     *
     * @return A map of player identifiers to their scores.
     */
    
    public Map<Character, Integer> getScores() {
        return scores;
    }

    /**
     * Gets the color associated with a given player.
     *
     * @param playerIdentifier The identifier for the player.
     * @return The color associated with the player, or white if the player is not found.
     */

    public int getColor(char playerIdentifier) {
        return colors.getOrDefault(playerIdentifier, parent.color(255)); // Default to white if not found
    }

    /**
     * Resets all player scores to 0.
     */

    public void resetScores() {
        for (Character player : scores.keySet()) {
            scores.put(player, 0);
        }
    }

    /**
     * Draws the scoreboard on the screen.
     */

    
    public void draw() {
        int startX = parent.width - 190;  // Position the scoreboard on the right side
        int startY = 80;  
        int boxWidth = 180;
        int boxHeight = 120;
    
        parent.fill(240, 240, 240, 0);  // Semi-transparent white background
        parent.stroke(0);  // Black border
        parent.strokeWeight(4);
        parent.rect(startX, startY, boxWidth, boxHeight);
    
        parent.textSize(16);
        parent.fill(0);  // Black text color
        parent.text("Scores", startX + 10, startY + 20);
    
        // Correct line coordinates
        parent.line(startX, startY + 30, startX + boxWidth, startY + 30);  // Ensures line is straight and matches box width
    
        int textY = startY + 50;  // Adjust textY to place text under the line
    
        for (Map.Entry<Character, Integer> entry : scores.entrySet()) {
            parent.fill(colors.get(entry.getKey()));  // Use stored color for each player
            String scoreText = "Player " + entry.getKey() + ": " + entry.getValue();
            parent.text(scoreText, startX + 10, textY);
            textY += 20;  // Increment Y position for next score
        }
    }
    
}
