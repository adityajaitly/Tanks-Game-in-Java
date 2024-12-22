package Tanks;

import processing.core.PApplet;

/**
 * The HelpBox class represents a help box in the game, which displays instructions and controls to the player.
 * It can be toggled between a collapsed and an expanded state.
 */

public class HelpBox extends App {
    private PApplet parent;
    private boolean isExpanded;
    private int boxX, boxY, boxWidth, boxHeight;
    private int expandedWidth, expandedHeight;
    private int expandedX, expandedY;

    /**
     * Constructs a new HelpBox instance.
     *
     * @param parent The PApplet instance.
     */


    public HelpBox(PApplet parent) {
        this.parent = parent;
        this.isExpanded = false;
        this.boxX = parent.width - 75; // Adjusted position
        this.boxY = 18;
        this.boxWidth = 50;
        this.boxHeight = 25; // Reduced height
        this.expandedWidth = parent.width - 75;
        this.expandedHeight = parent.height - 215 ;
        this.expandedX = (parent.width - expandedWidth) / 2 - 20;
        this.expandedY = (parent.height - expandedHeight) / 2 + 45;
    }

    /**
     * Draws the help box on the canvas.
     * Displays different content based on whether the box is expanded or collapsed.
     */

    public void draw() {
        parent.fill(250); // Set fill color to white
        parent.stroke(0);
        parent.strokeWeight(2);
        if (isExpanded) {
            // Drawing the expanded rectangle
            parent.rect(expandedX, expandedY, expandedWidth, expandedHeight);
            parent.textSize(16);
            parent.fill(0);
            parent.text("HELP", expandedX + expandedWidth / 2 , expandedY + 20);
            parent.text("WELCOME TO THE GAME 'TANKS' ", expandedX + 10 , expandedY + 30);
            parent.text("THIS GAME IS BEING IMPLEMENTED AT A HARD DIFFICULTY ", expandedX + 10 , expandedY + 55);
            parent.text("GOOD LUCK , MAY THE BEST 'TANK' WIN ", expandedX + 10 , expandedY + 80);
            parent.text("Controls:\n- Move Right: Right Arrow\n- Move Left: Left Arrow\n- Change Angle: Up/Down Arrow\n- Fire: Spacebar\n- Increase Power: W\n- Decrease Power: S\n- Repair (cost 20): R\n- Fuel (cost 10): F\n- Parachute (cost 15): P\n- Larger Projectile (cost 20) : X", expandedX + 10, expandedY +115);
            parent.text("CLICK ANYWHERE INSIDE THE HELP BOX TO EXIT", expandedX + 10, expandedY + 405);

        } else {
            // Drawing the collapsed rectangle
            parent.rect(boxX, boxY, boxWidth, boxHeight);
            parent.textSize(16);
            parent.fill(0);
            parent.text("HELP", boxX + boxWidth / 2 - 18, boxY + boxHeight / 2 + 7);

        }
        
    }

    /**
     * Toggles the state of the help box between expanded and collapsed.
     * Adjusts the position and size of the box accordingly.
     */

    public void toggle() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            boxX = (parent.width - expandedWidth) / 2;
            boxY = (parent.height - expandedHeight) / 2;

        } else {
            // Revert box position and size when collapsed
            boxX = parent.width - 75;
            boxY = 18;
        }
    }

    /**
     * Checks if the given mouse coordinates are inside the help box.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @return true if the mouse coordinates are inside the help box, false otherwise.
     */

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= boxX && mouseX <= boxX + (isExpanded ? expandedWidth : boxWidth) && mouseY >= boxY && mouseY <= boxY + (isExpanded ? expandedHeight : boxHeight);
    }

    /**
     * Checks if the help box is expanded.
     *
     * @return {@code true} if the help box is expanded, {@code false} otherwise.
     */

    public boolean isExpanded() {
        return isExpanded;
    }

    /**
     * Gets the expanded width of the help box.
     *
     * @return The expanded width in pixels.
     */

    public int getExpandedWidth() {
        return expandedWidth;
    }

    /**
     * Gets the expanded height of the help box.
     *
     * @return The expanded height in pixels.
     */

    public int getExpandedHeight() {
        return expandedHeight;
    }
}
