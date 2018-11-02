package net.thedanpage.worldshardestgame;

import net.thedanpage.worldshardestgame.controllers.Controller;
import net.thedanpage.worldshardestgame.controllers.GeneticController;
import net.thedanpage.worldshardestgame.genetic.GeneticGame;
import net.thedanpage.worldshardestgame.genetic.GeneticGameConfigs;

import java.util.ArrayList;
import java.util.function.Function;

import static net.thedanpage.worldshardestgame.Sound.BACKGROUND;

public class Main {
    public static void main(String[] args) {
        var sound = false;
        var test = false;
        var replay = false;

        var game = createGame(Algorithm.GENETIC);

        if (sound) MusicPlayer.play(BACKGROUND);

        if (test) {
            runTest(game, replay);
        }
        else {
            runGame(game, replay);
        }
    }

    private static void runGame(Game game, boolean replay) {
        var frame = new Frame();

        frame.add(game);
        frame.setVisible(true);
    }

    private static void runTest(Game game, boolean replay) {
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

    private enum Algorithm {
        GENETIC
    }

    private static Game createGame(Algorithm algorithm) {
        var levelNumber = 1;
        var level = createLevels().get(levelNumber-1);

        switch(algorithm) {
            case GENETIC:
                var populationSize = 100;
                var initialMoveCount = 5;
                var mutationRate = 0.05;
                Function<Double, Double> mutationChange = value -> value * 0.01;

                var controller = new GeneticController();
                var gameConfigs = new GeneticGameConfigs(populationSize, initialMoveCount, mutationRate, mutationChange);
                var geneticGame = new GeneticGame(controller, level, gameConfigs);


                return geneticGame;
        }

        return null;
    }
}

