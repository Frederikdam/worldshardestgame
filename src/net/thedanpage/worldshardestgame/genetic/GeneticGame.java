package net.thedanpage.worldshardestgame.genetic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.thedanpage.worldshardestgame.*;

public class GeneticGame extends Game<GeneticPlayer> {
    int generation = 1;
    GeneticGameConfigs gameConfigs;
    GeneticPlayer bestPlayer = null;

    public GeneticGame(Controller controller, GameLevel level, GeneticGameConfigs gameConfigs) {
        super(controller, level);
        this.gameConfigs = gameConfigs;
        this.setup();
    }

    public double calculateFitness(GeneticPlayer player) {
        double fitness;
        if (getLevel().allCoinsCollected()) {
            fitness = getLevel().getDistanceToGoal(player);
        } else {
            fitness = getLevel().getDistanceToNextCoin(player);
        }

        if(bestPlayer == null) return fitness;

        //Novelty reward
        var distance = player.distanceTo(bestPlayer);
        double noveltyReward = distance * 0.4;
        fitness -= noveltyReward;

        //Death penalty
        if (player.deadByDot) { fitness += 2; }

        return fitness;
    }

    @Override
    public void populationIsDead() {
        evaluateGeneration();
    }

    @Override
    public void playerIsDead(GeneticPlayer player) {
        player.fitness = calculateFitness(player);
    }

    @Override
    public int generationCount() {
        return generation;
    }

    @Override
    public void playerWon(GeneticPlayer player) {
        getLevel().reset();
        for(GeneticPlayer p : population) {
            p.setDead(true);
        }
        player.respawn(getLevel());
    }

    public void evaluateGeneration() {
        this.generation++;
        if(this.generation % 5 == 0) {
            gameConfigs.moveCount = gameConfigs.mutationChange.apply(gameConfigs.moveCount);
        }

        var bestCandidate = selection();
        bestPlayer = bestCandidate;
        var newPopulation = mutate(bestCandidate);
        population = newPopulation;
        getLevel().reset();

        for(GeneticPlayer player : this.population) {
            player.respawn(getLevel());
        }

        System.out.println("Fitness: " + bestCandidate.fitness + " MoveCount: " + bestCandidate.getMoves().size());
    }

    public GeneticPlayer selection() {
        GeneticPlayer bestPlayer = null;
        double bestFitness = Double.MAX_VALUE;
        for(var player : population) {
            if(player.fitness < bestFitness) {
                bestPlayer = player;
                bestFitness = player.fitness;
            }
        }
        return bestPlayer;
    }

    public List<GeneticPlayer> mutate(GeneticPlayer candidate) {
        List<GeneticPlayer> children = new ArrayList<>();
        for(var i = 0; i < gameConfigs.populationSize; i++) {
            var child = new GeneticPlayer(gameConfigs.moveCount, candidate.getMoves());
            child.mutate(gameConfigs.mutationRate);
            children.add(child);
        }
        return children;
    }

    @Override
    public List<GeneticPlayer> initializePopulation() {
        var players = new ArrayList<GeneticPlayer>();
        for (var i = 0; i < gameConfigs.populationSize; i++) {
            var player = new GeneticPlayer(gameConfigs.moveCount);
            players.add(player);
        }

        return players;
    }
}
