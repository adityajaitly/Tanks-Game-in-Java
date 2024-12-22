package Tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestScoreboard {

    private App parent;
    private Scoreboard scoreboard;

    @BeforeEach
    public void setUp() {
        parent = new App();
        scoreboard = new Scoreboard(parent);
    }

    @Test
    public void testRegisterAndRetrievePlayer() {
        // Test registering player
        scoreboard.registerPlayer('A', parent.color(255, 0, 0));
        assertEquals(0, scoreboard.getScore('A'));

        // Test retrieving player color
        assertEquals(parent.color(255, 0, 0), scoreboard.getColor('A'));
    }

    @Test
    public void testUpdateScore() {
        scoreboard.registerPlayer('A', parent.color(255, 0, 0));

        // Test updating score
        scoreboard.updateScore('A', 10);
        assertEquals(10, scoreboard.getScore('A'));

        // Test updating score again
        scoreboard.updateScore('A', 5);
        assertEquals(15, scoreboard.getScore('A'));
    }

    @Test
    public void testScoreboardResetScores() {
        scoreboard.registerPlayer('A', parent.color(255, 0, 0));
        scoreboard.updateScore('A', 10);

        scoreboard.resetScores();
        assertEquals(0, scoreboard.getScore('A'));
    }

    @Test
    public void testUnregisteredPlayerHandling() {
        assertEquals(0, scoreboard.getScore('X'));
        assertEquals(parent.color(255), scoreboard.getColor('X'));
    }

    @Test
    public void testMultiplePlayerRegistration() {
        scoreboard.registerPlayer('A', parent.color(255, 0, 0));
        scoreboard.registerPlayer('B', parent.color(0, 255, 0));

        scoreboard.updateScore('A', 10);
        scoreboard.updateScore('B', 20);

        assertEquals(10, scoreboard.getScore('A'));
        assertEquals(20, scoreboard.getScore('B'));

        assertEquals(parent.color(255, 0, 0), scoreboard.getColor('A'));
        assertEquals(parent.color(0, 255, 0), scoreboard.getColor('B'));
    }

    @Test
    public void testColorAssociation() {
        scoreboard.registerPlayer('A', parent.color(255, 0, 0));
        scoreboard.registerPlayer('B', parent.color(0, 255, 0));
        scoreboard.registerPlayer('C', parent.color(0, 0, 255));

        assertEquals(parent.color(255, 0, 0), scoreboard.getColor('A'));
        assertEquals(parent.color(0, 255, 0), scoreboard.getColor('B'));
        assertEquals(parent.color(0, 0, 255), scoreboard.getColor('C'));
    }

    @Test
    public void testResetScoresAfterUpdates() {
        scoreboard.registerPlayer('A', parent.color(255, 0, 0));
        scoreboard.updateScore('A', 10);
        scoreboard.updateScore('A', 5);

        scoreboard.resetScores();
        assertEquals(0, scoreboard.getScore('A'));
    }

    @Test
    public void testScoreForUnregisteredPlayer() {
        assertEquals(0, scoreboard.getScore('Z'));
    }

    @Test
    public void testGetColorForUnregisteredPlayer() {
        assertEquals(parent.color(255), scoreboard.getColor('Z')); // Default color
    }

    @Test
    public void testNegativeScoreUpdate() {
        scoreboard.registerPlayer('A', parent.color(255, 0, 0));
        scoreboard.updateScore('A', -5);
        assertEquals(-5, scoreboard.getScore('A'));
    }

    @Test
    public void testMultiplePlayersScoreReset() {
        scoreboard.registerPlayer('A', parent.color(255, 0, 0));
        scoreboard.registerPlayer('B', parent.color(0, 255, 0));
        scoreboard.updateScore('A', 10);
        scoreboard.updateScore('B', 20);

        scoreboard.resetScores();
        assertEquals(0, scoreboard.getScore('A'));
        assertEquals(0, scoreboard.getScore('B'));
    }

    @Test
    public void testDrawMethod() {
        // Ensures that the draw method does not throw any exceptions
        scoreboard.draw();
    }

    @Test
    public void testGetScores() {
        scoreboard.registerPlayer('A', parent.color(255, 0, 0));
        scoreboard.updateScore('A', 10);
        scoreboard.registerPlayer('B', parent.color(0, 255, 0));
        scoreboard.updateScore('B', 20);

        Map<Character, Integer> scores = scoreboard.getScores();

        assertEquals(2, scores.size());
        assertEquals(10, (int) scores.get('A'));
        assertEquals(20, (int) scores.get('B'));
    }
}
