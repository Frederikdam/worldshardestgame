package net.thedanpage.worldshardestgame;

import net.thedanpage.worldshardestgame.controllers.Controller;
import net.thedanpage.worldshardestgame.controllers.GeneticController;
import java.util.ArrayList;

import static net.thedanpage.worldshardestgame.Sound.BACKGROUND;

public class Main {
    public static void main(String[] args) {
        var sound = false;
        var test = true;
        var replay = false;
        var levelNumber = 1;

        var controller = new GeneticController();
        var level = createLevels().get(levelNumber-1);

        if (sound) MusicPlayer.play(BACKGROUND);

        if (test) {
            runTest(controller, level, replay);
        }
        else {
            runGame(controller, level, replay);
        }
    }

    private static void runGame(Controller controller, GameLevel level, boolean replay) {
        var game = new Game(controller, level);
        var frame = new Frame();

        frame.add(game);
        frame.setVisible(true);
    }

    private static void runTest(Controller controller, GameLevel level, boolean replay) {
        var game = new Game(controller, level);

        while(!game.goalReached()) {
            game.advanceGame();
        }
    }

    private static boolean levelExists(int levelNumber) {
        String fileUrl = "net/thedanpage/worldshardestgame/resources/maps/level_" + levelNumber + ".txt";
        return ClassLoader.getSystemResource(fileUrl) != null;
    }

    private static ArrayList<GameLevel> createLevels() {
        ArrayList<GameLevel> levels = new ArrayList<>();
        int levelCount = 1;
        while (levelExists(levelCount)) {
            GameLevel level = new GameLevel(levelCount);
            levels.add(level);
            levelCount++;
        }
        return levels;
    }
}
