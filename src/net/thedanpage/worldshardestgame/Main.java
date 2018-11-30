package net.thedanpage.worldshardestgame;

import net.thedanpage.worldshardestgame.astar.AStarController;
import net.thedanpage.worldshardestgame.astar.AStarGame;
import net.thedanpage.worldshardestgame.genetic.GeneticController;
import net.thedanpage.worldshardestgame.genetic.GeneticGame;
import net.thedanpage.worldshardestgame.genetic.GeneticGameConfigs;
import net.thedanpage.worldshardestgame.human.HumanController;
import net.thedanpage.worldshardestgame.human.HumanGame;
import net.thedanpage.worldshardestgame.human.HumanPlayer;
import net.thedanpage.worldshardestgame.qlearning.QLearningController;
import net.thedanpage.worldshardestgame.qlearning.QLearningGame;
import net.thedanpage.worldshardestgame.qlearning.QLearningGameConfigs;
import net.thedanpage.worldshardestgame.qlearning.QTable;

import java.util.ArrayList;
import java.util.function.Function;

import static net.thedanpage.worldshardestgame.Sound.BACKGROUND;

public class Main {

    public static void main(String[] args) {
        var sound = false;
        var test = false;
        var replay = true;

        var game = createGame(Algorithm.QLEARNING);

        if (sound) MusicPlayer.play(BACKGROUND);

        if (test) {
            runTest(game, replay);
        }
        else {
            runGame(game);
        }
    }

    private static void runGame(Game game) {
        render(game);
    }

    private static void runTest(Game game, boolean replay) {
        while(!game.goalReached) {
            game.advanceGame();
        }
        if(replay) { render(game); }
    }

    private static void render(Game game) {
        var frame = new Frame();
        frame.add(game);
        frame.setVisible(true);
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
        GENETIC,
        HUMAN,
        QLEARNING,
        ASTAR
    }

    private static Game createGame(Algorithm algorithm) {
        var levelNumber = 2;
        var level = createLevels().get(levelNumber-1);

        switch(algorithm) {
            case GENETIC:
                var populationSize = 100;
                var initialMoveCount = 5;
                var mutationRate = 0.005;
                Function<Integer, Integer> mutationChange = value -> {
                    var newValue = value < 5000 ? value + 4 : value;
                    return newValue;
                };

                var controller = new GeneticController();
                var gameConfigs = new GeneticGameConfigs(populationSize, initialMoveCount, mutationRate, mutationChange);
                var geneticGame = new GeneticGame(controller, level, gameConfigs);

                return geneticGame;
            case HUMAN:
                var humanController = new HumanController();
                var humanGame = new HumanGame(humanController, level);
                return humanGame;
            case QLEARNING:
                int actionRange = Move.values().length;
                float explorationChance=0.2f;
                float gammaValue=0.1f;
                float learningRate=0.8f;
                var qLearningGameConfigs = new QLearningGameConfigs(actionRange, explorationChance, gammaValue, learningRate);
                var qTable = new QTable(qLearningGameConfigs);
                var qLearningController = new QLearningController(qTable);
                var qLearningGame = new QLearningGame(qLearningController, level, qTable);
                return qLearningGame;
            case ASTAR:
                var aStarController = new AStarController();
                var aStarGame = new AStarGame(aStarController, level);
                return aStarGame;
        }

        return null;
    }
}

