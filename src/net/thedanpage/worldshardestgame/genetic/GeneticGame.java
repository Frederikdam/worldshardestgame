package net.thedanpage.worldshardestgame.genetic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Player;
import net.thedanpage.worldshardestgame.controllers.Controller;

public class GeneticGame extends Game {

    List<Player> population = new ArrayList<>();

    int generation = 1;
    boolean goalReached = false;

    GameLevel level;
    Player winningPlayer = null;
    GeneticGameConfigs gameConfigs;

    public GeneticGame(Controller controller, GameLevel level, GeneticGameConfigs gameConfigs) {
        super(controller, level);
        this.gameConfigs = gameConfigs;
    }

    public boolean goalReached() { return goalReached; }
    public void goalReached(boolean goalReached) { this.goalReached = goalReached; }

    public GameLevel getLevel() { return level; }

    public double calculateFitness(Player player) {
        return getLevel().getDistanceToGoal(player);
    }

    public void advanceDots(GameLevel level) {
        level.updateDots();
    }

    @Override
    public void populationIsDead() {
        evaluateGeneration();
    }

    @Override
    public void playerIsDead(Player player) {
        player.fitness = calculateFitness(player);
    }

    @Override
    public int generationCount() {
        return generation;
    }

    public void evaluateGeneration() {
        this.generation++;
        if(this.generation % 5 == 0) {
            if (gameConfigs.initialMoveCount < 5000) gameConfigs.initialMoveCount *= 1.2;
        }

        var bestCandidates = selection();
        var newPopulation = mutate(bestCandidates);
        population = newPopulation;
        getLevel().reset();
        for(Player player : population) {
            player.respawn(getLevel());
        }

        System.out.println("Fitness: " + bestCandidates.get(0).fitness + " MoveCount: " + bestCandidates.get(0).getMoves().size());
    }

    public List<Player> selection() {
        Player bestPlayer = null;
        double bestFitness = Double.MAX_VALUE;
        for(var player : population) {
            if(player.fitness < bestFitness) {
                bestPlayer = player;
                bestFitness = player.fitness;
            }
        }
        return Arrays.asList(bestPlayer);
    }

    public List<Player> mutate(List<Player> candidates) {
        List<Player> children = new ArrayList<>();
        var childrenCountPerCandidate = gameConfigs.populationSize / candidates.size();
        for(var candidate : candidates) {
            for(var i = 0; i < childrenCountPerCandidate; i++) {
                var child = new Player(gameConfigs.initialMoveCount, candidate.getMoves());
                child.mutate();
                children.add(child);
            }
        }
        return children;
    }

    @Override
    public List<Player> initializePopulation() {
        for (var i = 0; i < gameConfigs.populationSize; i++) {
            var player = new Player(gameConfigs.initialMoveCount);
            player.respawn(getLevel());
            population.add(player);
        }
    }
}
