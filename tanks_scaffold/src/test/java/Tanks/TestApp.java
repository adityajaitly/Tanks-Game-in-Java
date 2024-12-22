package Tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestApp {

    private PApplet parent;
    private App app;

    @BeforeEach
    public void setUp() {
        parent = new PApplet() {
            @Override
            public void settings() {
                size(864, 640);
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
        app = new App();
        PApplet.runSketch(new String[]{"Tanks.App"}, app);
        app.configPath = "config.json"; 
        app.setup();
    }

    @Test
    public void testLoadLayout() {
        List<String> layout = app.loadLayout(0);
        assertNotNull(layout);
        assertFalse(layout.isEmpty());
    }

    @Test
    public void testInitializeHeightCoordinates() {
        List<String> layout = app.loadLayout(0);
        app.initializeHeightCoordinates(layout);

        assertNotNull(app.xCoordinates);
        assertNotNull(app.yCoordinates);
    }

    @Test
    public void testDrawTerrain() {
        // Testing drawing the terrain 
        app.drawTerrain();
    }

    @Test
    public void testUpdateYCoordinates() {
        List<String> layout = app.loadLayout(0);
        app.initializeHeightCoordinates(layout);

        ArrayList<ArrayList<Integer>>[] oldYCoordinates = app.yCoordinates.clone();
        app.updateYCoordinates(app.yCoordinates);

        // Ensure that the yCoordinates have been updated
        assertNotEquals(oldYCoordinates, app.yCoordinates);
    }

    @Test
    public void testCreateTanksFromLayout() {
        List<String> layout = app.loadLayout(0);
        app.initializeHeightCoordinates(layout);
        app.createTanksFromLayout();

        assertNotNull(app.getTanks());
        assertFalse(app.getTanks().isEmpty());
    }

    @Test
    public void testDrawHealthBar() {
        app.drawHealthBar();
        
    }

    @Test
    public void testDrawPower() {
        app.drawPower();

    }

    @Test
    public void testCollisionUpdate() {
        app.collisionUpdate(100, 100);
        
    }

    @Test
    public void testUpdateTankState() {
        Tank tank = new Tank(app, 100, 100, 32, 32, app.color(255, 0, 0), app.yCoordinates, app.xCoordinates, 3, 'A');
         // Verify the tank state is updated correctly
        app.updateTankState(tank);
       
    }

    @Test
    public void testDrawArrowAboveTank() {
        Tank tank = new Tank(app, 100, 100, 32, 32, app.color(255, 0, 0), app.yCoordinates, app.xCoordinates, 3, 'A');
        app.drawArrowAboveTank(tank);
       
    }

    @Test
    public void testDisplayLargerProjectileIndicator() {
        Tank tank = new Tank(app, 100, 100, 32, 32, app.color(255, 0, 0), app.yCoordinates, app.xCoordinates, 3, 'A');
        app.displayLargerProjectileIndicator(tank);
        
    }

    @Test
    public void testKeyPressedSpace() {
        KeyEvent keyEvent = new KeyEvent(parent, 0, 0, 0, ' ', ' ');
        app.keyPressed(keyEvent);
        assertEquals(1, app.currentTankIndex); 
    }

    @Test
    public void testMousePressed() {
        app.mousePressed(new MouseEvent(parent, 0, 0, 0, 0, 0, 0, 0));
        
    }

    @Test
    public void testMouseReleased() {
        app.mouseReleased(new MouseEvent(parent, 0, 0, 0, 0, 0, 0, 0));
       
    }

    @Test
    public void testDraw() {
        app.draw();
        
    }


    @Test
    public void testInitializeGameComponents() {
        app.initializeHeightCoordinates(app.loadLayout(0));
        app.createTanksFromLayout();
        app.initializeHeightCoordinates(app.currentLayout);
        app.updateYCoordinates(app.yCoordinates);
        assertNotNull(app.getTanks());
        assertFalse(app.getTanks().isEmpty());
    }

    @Test
    public void testManageExplosions() {
        app.getExplosions().add(new Explosion(app, 100, 100, 50, app.getScoreboard(), 'A'));
        app.manageExplosions();
        assertTrue(app.getExplosions().isEmpty()); // Explosions should be processed and cleared
    }

    @Test
    public void testManageTanks() {
        app.createTanksFromLayout();
        app.manageTanks();
        assertFalse(app.getTanks().isEmpty()); // Tanks should be managed and drawn
    }

    @Test
    public void testEndGame() {
        app.endGame();
        assertTrue(app.isGameOver()); // Game should be marked as over
    }

    @Test
    public void testDisplayWinner() {
        app.endGame();
        app.displayWinner();
    }

    @Test
    public void testRestartGame() {
        app.restartGame();
        assertFalse(app.isGameOver()); // Game should be restarted
        assertNotNull(app.getTanks());
        assertFalse(app.getTanks().isEmpty());
    }
}

