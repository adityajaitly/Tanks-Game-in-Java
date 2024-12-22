
package Tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import static org.junit.jupiter.api.Assertions.*;

public class TestHelpBox {

    private PApplet parent;
    private HelpBox helpBox;

    @BeforeEach
    public void setUp() {
        parent = new PApplet() {
            @Override
            public void settings() {
                size(800, 600);  
            }

            @Override
            public void setup() {
                noLoop();
            }

            @Override
            public int millis() {
                return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            }
        };
        PApplet.runSketch(new String[]{"Tanks.App"}, parent);
        helpBox = new HelpBox(parent);
    }

    @Test
    public void testHelpBoxInitialState() {
        // Check initial state
        assertFalse(helpBox.isExpanded());
        assertFalse(helpBox.isInside(0, 0));
    }

    @Test
    public void testToggleHelpBox() {
        // Toggle the help box and check the state
        helpBox.toggle();
        assertTrue(helpBox.isExpanded());

        helpBox.toggle();
        assertFalse(helpBox.isExpanded());
    }

    @Test
    public void testIsInsideCollapsed() {
        // Check if a point is inside the collapsed help box
        int mouseX = parent.width - 50;
        int mouseY = 30;
        assertTrue(helpBox.isInside(mouseX, mouseY));
        assertFalse(helpBox.isInside(mouseX - 100, mouseY));  // Outside the help box
    }

    @Test
    public void testIsInsideExpanded() {
        // Expand the help box and check if a point is inside the expanded help box
        helpBox.toggle();

        int mouseX = (parent.width - helpBox.getExpandedWidth()) / 2 + 10;
        int mouseY = (parent.height - helpBox.getExpandedHeight()) / 2 + 10;
        assertTrue(helpBox.isInside(mouseX, mouseY));

        // Check a point outside the expanded help box
        assertFalse(helpBox.isInside(mouseX - 100, mouseY));
    }

    @Test
    public void testExpandedDimensions() {
        // Toggle to expand the help box
        helpBox.toggle();
        assertTrue(helpBox.isExpanded());

        // Check if the expanded dimensions are correct
        assertEquals(parent.width - 75, helpBox.getExpandedWidth());
        assertEquals(parent.height - 215, helpBox.getExpandedHeight());
    }

    @Test
    public void testContentDrawingCollapsed() {
        // Checks if the help box is drawing in the collapsed state

        helpBox.draw();
        assertFalse(helpBox.isExpanded());
    }

    @Test
    public void testContentDrawingExpanded() {
        // Checks if the help box is drawing in the expanded state

        helpBox.toggle();
        helpBox.draw();
        assertTrue(helpBox.isExpanded());
    }

    @Test
    public void testToggleStateTransition() {
        // Check initial state
        assertFalse(helpBox.isExpanded());

        // Toggle and check state
        helpBox.toggle();
        assertTrue(helpBox.isExpanded());

        // Toggle again and check state
        helpBox.toggle();
        assertFalse(helpBox.isExpanded());
    }

    @Test
    public void testMouseInsideAfterToggle() {
        // Initial state should be collapsed
        assertFalse(helpBox.isExpanded());

        // Coordinates inside the collapsed help box
        int mouseXCollapsed = parent.width - 50;
        int mouseYCollapsed = 30;
        assertTrue(helpBox.isInside(mouseXCollapsed, mouseYCollapsed));

        // Toggle to expand
        helpBox.toggle();
        assertTrue(helpBox.isExpanded());

        // Coordinates inside the expanded help box
        int mouseXExpanded = (parent.width - helpBox.getExpandedWidth()) / 2 + 10;
        int mouseYExpanded = (parent.height - helpBox.getExpandedHeight()) / 2 + 10;
        assertTrue(helpBox.isInside(mouseXExpanded, mouseYExpanded));
    }

    @Test
    public void testClickInsideCollapsedHelpBox() {
        // Simulate a click inside the collapsed help box
        int mouseX = parent.width - 50;
        int mouseY = 30;
        if (helpBox.isInside(mouseX, mouseY)) {
            helpBox.toggle();
        }
        assertTrue(helpBox.isExpanded());
    }

    @Test
    public void testClickInsideExpandedHelpBox() {
        // Expand the help box
        helpBox.toggle();
        assertTrue(helpBox.isExpanded());

        // Simulate a click inside the expanded help box
        int mouseX = (parent.width - helpBox.getExpandedWidth()) / 2 + 10;
        int mouseY = (parent.height - helpBox.getExpandedHeight()) / 2 + 10;
        if (helpBox.isInside(mouseX, mouseY)) {
            helpBox.toggle();
        }
        assertFalse(helpBox.isExpanded());
    }

    @Test
    public void testClickOutsideHelpBox() {
        // Simulate a click outside the help box when collapsed
        int mouseX = 10;
        int mouseY = 10;
        boolean initialExpandedState = helpBox.isExpanded();
        if (!helpBox.isInside(mouseX, mouseY)) {
            helpBox.toggle();
        }
        // Check that the state hasn't changed
        assertEquals(initialExpandedState, helpBox.isExpanded());

        // Expand the help box and simulate a click outside
        helpBox.toggle();
        initialExpandedState = helpBox.isExpanded();
        if (!helpBox.isInside(mouseX, mouseY)) {
            helpBox.toggle();
        }
        // Check that the state hasn't changed
        assertEquals(initialExpandedState, helpBox.isExpanded());
    }

    @Test
    public void testSpecificCoordinatesDrawing() {
        // Simulate drawing the help box in collapsed state
        helpBox.draw();
        assertFalse(helpBox.isExpanded());

        // Toggle to expand and simulate drawing the help box in expanded state
        helpBox.toggle();
        helpBox.draw();
        assertTrue(helpBox.isExpanded());

        // Specific coordinates test
        int mouseX = (parent.width - helpBox.getExpandedWidth()) / 2;
        int mouseY = (parent.height - helpBox.getExpandedHeight()) / 2;
        assertTrue(helpBox.isInside(mouseX, mouseY));

        mouseX = parent.width - 75;
        mouseY = 18;
        assertTrue(helpBox.isInside(mouseX, mouseY));
    }


    

}

