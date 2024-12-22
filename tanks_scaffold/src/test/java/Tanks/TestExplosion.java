package Tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestExplosion {

    private PApplet parent;
    private Scoreboard scoreboard;

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
        PApplet.runSketch(new String[]{"Tanks.MockPApplet"}, parent);
        scoreboard = new Scoreboard(parent);
    }

    @Test
    public void testExplosionInitialization() {
        Explosion explosion = new Explosion(parent, 100, 100, 50, scoreboard, 'A');

        assertEquals(100, explosion.getX(), 0.001);
        assertEquals(100, explosion.getY(), 0.001);
        assertEquals(50, explosion.getMaxRadius(), 0.001);
        assertEquals('A', explosion.getFiringPlayerIdentifier());
    }

    @Test
    public void testExplosionUpdate() {
        Explosion explosion = new Explosion(parent, 100, 100, 50, scoreboard, 'A');
        
        // Simulate the passage of time
        explosion.update();
        assertTrue(explosion.getCurrentRadius() > 0);
        assertTrue(explosion.getCurrentRadius() <= 50);

        // Update again to test the full duration
        int initialMillis = parent.millis();
        while (parent.millis() - initialMillis < 200) {
            explosion.update();
        }

        assertEquals(50, explosion.getCurrentRadius(), 0.001); // Max radius reached
    }

    @Test
    public void testExplosionIsDone() {
        Explosion explosion = new Explosion(parent, 100, 100, 50, scoreboard, 'A');
        
        // Before duration passes
        assertFalse(explosion.isDone());

        // Waits for the duration to pass
        int initialMillis = parent.millis();
        while (parent.millis() - initialMillis < 250) {
            explosion.update();
        }

        // After duration has passed
        assertTrue(explosion.isDone());
    }

    @Test
    public void testExplosionDrawing() {
        Explosion explosion = new Explosion(parent, 100, 100, 50, scoreboard, 'A');
        
        // Check initial draw state
        explosion.draw();
        assertTrue(explosion.getCurrentRadius() > 0);

        // Update to mid-point
        int initialMillis = parent.millis();
        while (parent.millis() - initialMillis < 100) {
            explosion.update();
        }
        explosion.draw();
        assertTrue(explosion.getCurrentRadius() > 0 && explosion.getCurrentRadius() < 50);

        // Update to end
        initialMillis = parent.millis();
        while (parent.millis() - initialMillis < 200) {
            explosion.update();
        }
        explosion.draw();
        assertEquals(50, explosion.getCurrentRadius(), 0.001);
    }

    @Test
    public void testApplyDamage() {
        // Create some tanks near the explosion
        Tank tank1 = new Tank(parent, 100, 100, 32, 32, parent.color(255, 0, 0), null, null, 3, 'A');
        Tank tank2 = new Tank(parent, 120, 120, 32, 32, parent.color(0, 255, 0), null, null, 3, 'B');
        Tank tank3 = new Tank(parent, 200, 200, 32, 32, parent.color(0, 0, 255), null, null, 3, 'C');
        Tank tank4 = new Tank(parent, 100, 150, 32, 32, parent.color(255, 255, 0), null, null, 3, 'D');

        List<Tank> tanks = new ArrayList<>();
        tanks.add(tank1);
        tanks.add(tank2);
        tanks.add(tank3);
        tanks.add(tank4);

        Explosion explosion = new Explosion(parent, 100, 100, 50, scoreboard, 'A');
        explosion.applyDamage(tanks);

        // Check health of tanks
        assertEquals(100, tank1.getHealth()); // Should not be damaged because it's the firing player's tank
        assertTrue(tank2.getHealth() < 100); // Should be damaged (closer)
        assertEquals(100, tank3.getHealth()); // Should not be damaged because it's outside the explosion radius
        assertTrue(tank4.getHealth() < 100); // Should be damaged (further away but still within radius)

        // Check if the scoreboard is updated
        assertEquals(0, scoreboard.getScore('A')); // No score should be added for damaging own tank
        assertTrue(scoreboard.getScore('A') > 0); // Score should be added for damaging other tanks
    }

    @Test
    public void testExplosionNoTanksNearby() {
        // Create some tanks far from the explosion
        Tank tank1 = new Tank(parent, 300, 300, 32, 32, parent.color(255, 0, 0), null, null, 3, 'A');
        Tank tank2 = new Tank(parent, 350, 350, 32, 32, parent.color(0, 255, 0), null, null, 3, 'B');

        List<Tank> tanks = new ArrayList<>();
        tanks.add(tank1);
        tanks.add(tank2);

        Explosion explosion = new Explosion(parent, 100, 100, 50, scoreboard, 'A');
        explosion.applyDamage(tanks);

        // Check health of tanks
        assertEquals(100, tank1.getHealth()); // Should not be damaged
        assertEquals(100, tank2.getHealth()); // Should not be damaged

        // Check if the scoreboard is updated
        assertEquals(0, scoreboard.getScore('A')); // No score should be added as no tanks are damaged
    }

    @Test
    public void testExplosionExactRadiusBoundary() {
        // Create tanks exactly on the boundary of the explosion radius
        Tank tank1 = new Tank(parent, 150, 100, 32, 32, parent.color(255, 0, 0), null, null, 3, 'A');
        Tank tank2 = new Tank(parent, 100, 150, 32, 32, parent.color(0, 255, 0), null, null, 3, 'B');

        List<Tank> tanks = new ArrayList<>();
        tanks.add(tank1);
        tanks.add(tank2);

        Explosion explosion = new Explosion(parent, 100, 100, 50, scoreboard, 'A');
        explosion.applyDamage(tanks);

        // Check health of tanks
        assertEquals(100, tank1.getHealth()); // Should be at the boundary
        assertTrue(tank2.getHealth() < 100); // Should be damaged (within radius)

        // Check if the scoreboard is updated
        assertTrue(scoreboard.getScore('A') > 0); // Score should be added for damaging the second tank
    }

    @Test
    public void testExplosionDifferentDurations() {
        Explosion explosionShort = new Explosion(parent, 100, 100, 50, scoreboard, 'A');
        explosionShort.update();

        Explosion explosionLong = new Explosion(parent, 100, 100, 50, scoreboard, 'A');
        int initialMillis = parent.millis();
        while (parent.millis() - initialMillis < 300) {
            explosionLong.update();
        }

        // Checking radii after different durations
        assertTrue(explosionShort.getCurrentRadius() > 0);
        assertTrue(explosionShort.getCurrentRadius() < 50);
        assertEquals(50, explosionLong.getCurrentRadius(), 0.001);
    }

    @Test
    public void testDraw() {
        // Ensures that draw method works without any exceptions
        Explosion explosion = new Explosion(parent, 100, 100, 50, scoreboard, 'A');
        explosion.draw();
        
    }

   

}
