package net.thedanpage.worldshardestgame.replay;

import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;

import java.util.ArrayList;
import java.util.List;

public class ReplayGame extends Game<ReplayPlayer> {

    public ReplayGame(ReplayController controller, GameLevel level) {
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
    public void playerDiedToEnemy(ReplayPlayer player) {

    }

    @Override
    public List<ReplayPlayer> initializePopulation() {
        ArrayList<ReplayPlayer> players = new ArrayList<>();
        players.add(new ReplayPlayer());
        return players;
    }

    @Override
    public void playerWon(ReplayPlayer player) {

    }
}
