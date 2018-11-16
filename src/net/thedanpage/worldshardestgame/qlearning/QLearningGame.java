package net.thedanpage.worldshardestgame.qlearning;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;

import java.util.ArrayList;
import java.util.List;

public class QLearningGame extends Game<QLearningPlayer> {

    QTable qTable;
    int generation = 1;

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
    }

    @Override
    public void playerIsDead(QLearningPlayer player) {
        int distanceToGoal = (int)getLevel().getDistanceToGoal(player);
        int reward = distanceToGoal;
        if (player.deadByDot) reward = -Integer.MAX_VALUE;
        qTable.updateQvalue(reward, this);
        System.out.println(distanceToGoal);
    }

    @Override
    public List<QLearningPlayer> initializePopulation() {
        var players = new ArrayList<QLearningPlayer>();
        players.add(new QLearningPlayer());
        return players;
    }

    @Override
    public void playerWon(QLearningPlayer player) {

    }
}
