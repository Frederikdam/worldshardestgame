package net.thedanpage.worldshardestgame.qlearning;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;

import java.util.ArrayList;
import java.util.List;

public class QLearningGame extends Game<QLearningPlayer> {

    QTable qTable;
    int generation = 1;
    double bestDistance = Double.MAX_VALUE;

    public QLearningGame(Controller controller, GameLevel level, QTable qTable) {
        super(controller, level);
        this.qTable = qTable;
        this.setup();
    }

    @Override
    public int generationCount() {
        return generation;
    }

    @Override
    public void populationIsDead() {
        generation++;
        getLevel().reset();

        for(QLearningPlayer player : this.population) {
            player.respawn(getLevel());
        }
        System.out.println(qTable.table.size());
    }

    @Override
    public void playerDiedToEnemy(QLearningPlayer player) {
        var distanceToGoal = getLevel().getDistanceToGoal(player);
        System.out.println(bestDistance);
        if (distanceToGoal < bestDistance) {
            bestDistance = distanceToGoal;
        }
    }

    @Override
    public List<QLearningPlayer> initializePopulation() {
        var size = 1;
        var players = new ArrayList<QLearningPlayer>();
        for(int i = 0; i < size; i++) {
            players.add(new QLearningPlayer());
        }
        return players;
    }

    @Override
    public void playerWon(QLearningPlayer player) {
        System.out.println("We won!");
        getLevel().reset();
        player.respawn(getLevel());
    }
}
