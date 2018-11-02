package net.thedanpage.worldshardestgame.genetic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Player;
import net.thedanpage.worldshardestgame.Controller;

public class GeneticGame extends Game {
    int generation = 1;
    GeneticGameConfigs gameConfigs;

    public GeneticGame(Controller controller, GameLevel level, GeneticGameConfigs gameConfigs) {
        super(controller, level);
        this.gameConfigs = gameConfigs;
        this.setup();
    }

    public double calculateFitness(Player player) {
        return getLevel().getDistanceToGoal(player);
    }

    @Override
    public void populationIsDead() {
        evaluateGeneration();
    }

    @Override
    public void playerIsDead(Player player) {
        var geneticPlayer = (GeneticPlayer) player;
        geneticPlayer.fitness = calculateFitness(player);
    }

    @Override
    public int generationCount() {
        return generation;
    }

    public void evaluateGeneration() {
        this.generation++;
        if(this.generation % 5 == 0) {
            gameConfigs.moveCount = gameConfigs.mutationChange.apply(gameConfigs.moveCount);
        }

        var bestCandidates = selection();
        var newPopulation = mutate(bestCandidates);
        population = newPopulation;
        getLevel().reset();

        for(Player player : this.population) {
            player.respawn(getLevel());
        }

        System.out.println("Fitness: " + bestCandidates.get(0).fitness + " MoveCount: " + bestCandidates.get(0).getMoves().size());
    }

    public List<GeneticPlayer> selection() {
        GeneticPlayer bestPlayer = null;
        double bestFitness = Double.MAX_VALUE;
        for(var player : population) {
            var geneticPlayer = (GeneticPlayer) player;
            if(geneticPlayer.fitness < bestFitness) {
                bestPlayer = geneticPlayer;
                bestFitness = geneticPlayer.fitness;
            }
        }
        return Arrays.asList(bestPlayer);
    }

    public List<Player> mutate(List<GeneticPlayer> candidates) {
        List<Player> children = new ArrayList<>();
        var childrenCountPerCandidate = gameConfigs.populationSize / candidates.size();
        for(var candidate : candidates) {
            for(var i = 0; i < childrenCountPerCandidate; i++) {
                var child = new GeneticPlayer(gameConfigs.moveCount, candidate.getMoves());
                child.mutate(gameConfigs.mutationRate);
                children.add(child);
            }
        }
        return children;
    }

    @Override
    public List<Player> initializePopulation() {
        var players = new ArrayList<Player>();
        for (var i = 0; i < gameConfigs.populationSize; i++) {
            var player = new GeneticPlayer(gameConfigs.moveCount);
            players.add(player);
        }

        return players;
    }
}
