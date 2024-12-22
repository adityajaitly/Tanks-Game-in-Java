package Tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import static org.junit.jupiter.api.Assertions.*;

public class TestProjectile {

    private PApplet parent;

    @BeforeEach
    public void setUp() {
        parent = new PApplet();
        PApplet.runSketch(new String[]{"Tanks.MockPApplet"}, parent);
    }

    @Test
    public void testProjectileInitialization() {
        Projectile projectile = new Projectile(parent, 100, 100, 10, 5, parent.color(255, 0, 0), 'A', 10, 5);
        
        assertEquals(100, projectile.getx(), 0.001);
        assertEquals(100, projectile.gety(), 0.001);
        assertEquals(10, projectile.vx, 0.001);
        assertEquals(5, projectile.vy, 0.001);
        assertEquals(5, projectile.getRadius(), 0.001);
        assertEquals('A', projectile.firingPlayerIdentifier);
    }

    @Test
    public void testProjectileUpdate() {
        Projectile projectile = new Projectile(parent, 100, 100, 10, 5, parent.color(255, 0, 0), 'A', 10, 5);
        
        // Simulate wind and gravity effects
        App.wind = 2;
        projectile.update();

        // Check the new position and velocity
        assertEquals(110, projectile.getx(), 0.001);
        assertEquals(105, projectile.gety(), 0.001);
        assertEquals(10.06, projectile.vx, 0.001); // Initial vx + wind effect
        assertEquals(8.6, projectile.vy, 0.001);   // Initial vy + gravity
    }

    @Test
    public void testProjectileOffscreen() {
        Projectile projectile = new Projectile(parent, 100, 100, 10, 5, parent.color(255, 0, 0), 'A', 10, 5);
        
        // Move projectile to a position offscreen
        projectile.x = -10;
        assertTrue(projectile.isOffscreen());

        projectile.x = 810;
        assertTrue(projectile.isOffscreen());

        projectile.x = 100;
        projectile.y = 610;
        assertTrue(projectile.isOffscreen());

        projectile.y = -10;
        assertTrue(projectile.isOffscreen());
    }

    @Test
    public void testProjectileDrawing() {
        // Ensures that the draw method does not throw any exceptions
        Projectile projectile = new Projectile(parent, 100, 100, 10, 5, parent.color(255, 0, 0), 'A', 10, 5);

        // Simulate the draw call by invoking the parent.draw() method
        parent.draw();
        projectile.draw();
    }
}
