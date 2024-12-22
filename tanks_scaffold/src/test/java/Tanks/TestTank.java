package Tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestTank {

    private PApplet parent;
    private Tank tank;

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
        PApplet.runSketch(new String[]{"Tanks.App"}, parent);
        tank = new Tank(parent, 100, 100, 32, 32, parent.color(255, 0, 0), null, null, 3, 'A');
    }

    @Test
    public void testMoveRight() {
        float initialX = tank.getX();
        int initialFuel = tank.getFuel();
        tank.moveRight(0);
        assertEquals(initialX + 5, tank.getX(), 0.001);
        assertEquals(initialFuel - 1, tank.getFuel());
    }

    @Test
    public void testMoveLeft() {
        float initialX = tank.getX();
        int initialFuel = tank.getFuel();
        tank.moveLeft(0);
        assertEquals(initialX - 5, tank.getX(), 0.001);
        assertEquals(initialFuel - 1, tank.getFuel());
    }

    @Test
    public void testSetAndGetY() {
        tank.setY(200);
        assertEquals(200, tank.getY());
    }

    @Test
    public void testSetAndGetFuel() {
        tank.setFuel(100);
        assertEquals(100, tank.getFuel());
    }

    @Test
    public void testSetAndGetHealth() {
        tank.setHealth(80);
        assertEquals(80, tank.getHealth());
    }

    @Test
    public void testSetAndGetStickAngle() {
        tank.setStickAngle(PApplet.PI / 4);
        assertEquals(PApplet.PI / 4, tank.getStickAngle(), 0.001);
    }

    @Test
    public void testChangeStickAngle() {
        float initialAngle = tank.getStickAngle();
        tank.changeStickAngle(PApplet.PI / 6);
        assertEquals(initialAngle + PApplet.PI / 6, tank.getStickAngle(), 0.001);
    }

    @Test
    public void testSetAndGetPower() {
        tank.setPower(75);
        assertEquals(75, tank.getPower());
    }

    @Test
    public void testSetAndGetParachutes() {
        tank.setParachutes(5);
        assertEquals(5, tank.getParachutes());
    }

    @Test
    public void testIsAlive() {
        assertTrue(tank.isAlive());
        tank.setHealth(0);
        assertFalse(tank.isAlive());
    }

    @Test
    public void testIsOnGround() {
        tank.setY(App.HEIGHT - tank.getHeight());
        assertTrue(tank.isOnGround());
    }

    @Test
    public void testDeployParachute() {
        assertFalse(tank.isParachuteDeployed());
        tank.deployParachute();
        assertTrue(tank.isParachuteDeployed());
        assertEquals(2, tank.getParachutes());
    }

    @Test
    public void testResetParachute() {
        tank.deployParachute();
        assertTrue(tank.isParachuteDeployed());
        tank.resetParachute();
        assertFalse(tank.isParachuteDeployed());
    }

    @Test
    public void testFire() {
        tank.fire();
        assertFalse(tank.isNextProjectileLarge());
        assertEquals(1, tank.getProjectiles().size());
    }

    @Test
    public void testAdjustToTerrain() {
        ArrayList<ArrayList<Integer>>[] heightList = new ArrayList[App.BOARD_WIDTH];
        ArrayList<Integer>[] xCoordinates = new ArrayList[App.BOARD_WIDTH];

        for (int i = 0; i < App.BOARD_WIDTH; i++) {
            heightList[i] = new ArrayList<>();
            xCoordinates[i] = new ArrayList<>();
            for (int j = 0; j < App.BOARD_HEIGHT; j++) {
                heightList[i].add(new ArrayList<>());
                xCoordinates[i].add(i * App.CELLSIZE);
            }
        }

        tank = new Tank(parent, 100, 100, 32, 32, parent.color(255, 0, 0), heightList, xCoordinates, 3, 'A');
        tank.adjustToTerrain();
        int expectedY = App.HEIGHT - tank.getHeight() + 5;
        assertEquals(expectedY, tank.getY(), 0.001);
    }

    @Test
    public void testUpdateProjectiles() {
        tank.setNextProjectileLarge(true);
        tank.fire();
        int initialProjectileCount = tank.getProjectiles().size();
        tank.updateProjectiles();
        assertEquals(initialProjectileCount, tank.getProjectiles().size());
        Projectile projectile = tank.getProjectiles().get(0);
        float initialX = projectile.getx();
        float initialY = projectile.gety();
        projectile.update();
        tank.updateProjectiles();
        assertNotEquals(initialX, projectile.getx());
        assertNotEquals(initialY, projectile.gety());
    }

    @Test
    public void testDrawProjectiles() {
        tank.setNextProjectileLarge(true);
        tank.fire();
        // Ensures that no exceptions are thrown during draw
        tank.drawProjectiles();
    }

    @Test
    public void testGetPlayerIdentifier() {
        assertEquals('A', tank.getPlayerIdentifier());
    }

    @Test
    public void testGetColor() {
        assertEquals(parent.color(255, 0, 0), tank.getColor());
    }

    @Test
    public void testSetNextProjectileLarge() {
        tank.setNextProjectileLarge(true);
        assertTrue(tank.isNextProjectileLarge());
        tank.setNextProjectileLarge(false);
        assertFalse(tank.isNextProjectileLarge());
    }

    @Test
    public void testProjectileCreation() {
        tank.setNextProjectileLarge(true);
        tank.fire();
        assertEquals(1, tank.getProjectiles().size());
        Projectile projectile = tank.getProjectiles().get(0);
        assertEquals(28, projectile.getSize());
        assertEquals(60, projectile.getRadius());
    }

    @Test
    public void testProjectileTrajectory() {
        tank.fire();
        Projectile projectile = tank.getProjectiles().get(0);

        float initialX = projectile.getx();
        float initialY = projectile.gety();

        projectile.update();

        assertNotEquals(initialX, projectile.getx());
        assertNotEquals(initialY, projectile.gety());
    }

    @Test
    public void testProjectileOffscreen() {
        Projectile projectile = new Projectile(parent, 50, 50, 5, 5, parent.color(255, 0, 0), 'A', 10, 30);
        projectile.update();

        assertFalse(projectile.isOffscreen());

        projectile = new Projectile(parent, -10, 50, 5, 5, parent.color(255, 0, 0), 'A', 10, 30);
        assertTrue(projectile.isOffscreen());

        projectile = new Projectile(parent, 50, -10, 5, 5, parent.color(255, 0, 0), 'A', 10, 30);
        assertTrue(projectile.isOffscreen());

        projectile = new Projectile(parent, parent.width + 10, 50, 5, 5, parent.color(255, 0, 0), 'A', 10, 30);
        assertTrue(projectile.isOffscreen());

        projectile = new Projectile(parent, 50, parent.height + 10, 5, 5, parent.color(255, 0, 0), 'A', 10, 30);
        assertTrue(projectile.isOffscreen());
    }

    @Test
    public void testParachuteDeploymentAndReset() {
        assertFalse(tank.isParachuteDeployed());
        tank.deployParachute();
        assertTrue(tank.isParachuteDeployed());
        assertEquals(2, tank.getParachutes());
        tank.resetParachute();
        assertFalse(tank.isParachuteDeployed());
        assertEquals(2, tank.getParachutes());
    }

    @Test
    public void testFireWithDifferentPowers() {
        tank.setPower(20);
        tank.fire();
        Projectile projectile = tank.getProjectiles().get(0);
        assertTrue(projectile.vx > 0);
        assertTrue(projectile.vy < 0);

        tank.setPower(40);
        tank.fire();
        projectile = tank.getProjectiles().get(1);
        assertTrue(projectile.vx > 0);
        assertTrue(projectile.vy < 0);
    }

    @Test
    public void testDeployParachuteWhenNoneLeft() {
        tank.setParachutes(0);
        tank.deployParachute();
        assertFalse(tank.isParachuteDeployed());
        assertEquals(0, tank.getParachutes());
    }

    @Test
    public void testGetWidth() {
        assertEquals(32, tank.getWidth());
    }

    @Test
    public void testDraw() {
        // Ensures that draw method works without exceptions
        tank.draw();
    }

    @Test
    public void testIsParachuteDeployed() {
        tank.deployParachute();
        assertTrue(tank.isParachuteDeployed());
    }
}

