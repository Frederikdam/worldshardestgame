package net.thedanpage.worldshardestgame.astar;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.qlearning.QLearningPlayer;

import java.util.ArrayList;
import java.util.List;

public class AStarGame extends Game<AStarPlayer> {

    public AStarGame(Controller controller, GameLevel level) {
        super(controller, level);
        this.setup();
    }

    @Override
    public void populationIsDead() {
        restartLevel();
    }

    @Override
    public void playerWon(AStarPlayer player) {
        restartLevel();
        System.out.println("We won!");
    }

    @Override
    public void playerDiedToEnemy(AStarPlayer player) {}

    @Override
    public List<AStarPlayer> initializePopulation() {
        var players = new ArrayList<AStarPlayer>();
        players.add(new AStarPlayer());
        return players;
    }

    @Override
    public int generationCount() {
        return -1;
    }

    private void restartLevel() {
        getLevel().reset();

        for(AStarPlayer player : this.population) {
            player.respawn(getLevel());
        }
    }
}
