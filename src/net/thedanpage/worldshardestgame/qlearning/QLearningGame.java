package net.thedanpage.worldshardestgame.qlearning;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;

import java.util.ArrayList;
import java.util.List;

public class QLearningGame extends Game<QLearningPlayer> {
    public QLearningGame(Controller controller, GameLevel level) {
        super(controller, level);
        this.setup();
    }

    @Override
    public int generationCount() {
        return -1;
    }

    @Override
    public void populationIsDead() {

    }

    @Override
    public void playerIsDead(QLearningPlayer player) {

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
