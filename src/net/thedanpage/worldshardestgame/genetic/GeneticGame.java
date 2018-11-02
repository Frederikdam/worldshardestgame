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

    Controller controller;
    GameLevel level;
    GeneticGameConfigs gameConfigs;

    Player winningPlayer = null;


    public GeneticGame(Controller controller, GameLevel level, GeneticGameConfigs gameConfigs) {
        this.controller = controller;
        this.level = level;
        this.gameConfigs = gameConfigs;
        intializePopulation();
    }

    public boolean goalReached() { return goalReached; }
    public void goalReached(boolean goalReached) { this.goalReached = goalReached; }

    public GameLevel getLevel() { return level; }

    public double calculateFitness(Player player) {
        return getLevel().getDistanceToGoal(player);
    }

    @Override
    public void advanceGame() {
        var deadPlayerCount = 0;
        advanceDots(getLevel());

        for(Player player : population) {
            var nextMove = player.getNextMove();
            advancePlayer(nextMove, getLevel(), player);
            if(player.isDead()) deadPlayerCount++;
        }

        if(deadPlayerCount == gameConfigs.populationSize) {
            evaluateGeneration();
        }

        if (goalReached()) {
            System.out.println("Goal Reached!");
            System.exit(0);
        }
    }

    public void advanceDots(GameLevel level) {
        level.updateDots();
    }

    private void checkIfGoalReached(GameLevel level, Player player) {
        if (level.getTileMap() != new ArrayList<Tile>()) {
            if (level.allCoinsCollected()) {
                for (Tile t : level.getTileMap()) {
                    if (t.getType() == 3 && player.collidesWith(t.getBounds())) {
                        goalReached(true);
                        winningPlayer = player;
                    }
                }
            }
        }
    }

    private void checkCollisions(GameLevel level, Player player) {
        player.checkCollisionUp(level);
        player.checkCollisionDown(level);
        player.checkCollisionLeft(level);
        player.checkCollisionRight(level);
    }

    private void checkIfDead(GameLevel level, Player player) {
        if (!player.isDead()) {
            for (Dot dot : level.dots) {
                if (player.collidesWith(dot.getBounds())) {
                    player.setDead(true);
                    player.opacity = 0;
                }
            }
        }
        if (player.isDead()) {
            player.fitness = calculateFitness(player);
            player.opacity = 0;
        }
    }

    public void advancePlayer(Move move, GameLevel level, Player player) {
        player.move(move, level);

        checkIfCoinCollected(level, player);
        checkIfGoalReached(level, player);
        checkCollisions(level, player);
        checkIfDead(level, player);
    }

    public void evaluateGeneration() {
        this.generation++;
        if(this.generation % 5 == 0) {
            if (this.playerMoveCount < 5000) this.playerMoveCount *= 1.2;
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
        var childrenCountPerCandidate = populationSize / candidates.size();
        for(var candidate : candidates) {
            for(var i = 0; i < childrenCountPerCandidate; i++) {
                var child = new Player(this.playerMoveCount, candidate.getMoves());
                child.mutate();
                children.add(child);
            }
        }
        return children;
    }

    private void intializePopulation() {
        for (var i = 0; i < populationSize; i++) {
            var player = new Player(playerMoveCount);
            player.respawn(getLevel());
            population.add(player);
        }
    }
}
