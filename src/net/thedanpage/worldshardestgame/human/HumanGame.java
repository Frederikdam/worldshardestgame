package net.thedanpage.worldshardestgame.human;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Player;

import java.util.ArrayList;
import java.util.List;

public class HumanGame extends Game<HumanPlayer> {

    public HumanGame(Controller controller, GameLevel level) {
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
    public void playerDiedToEnemy(HumanPlayer player) {

    }

    @Override
    public List<HumanPlayer> initializePopulation() {
        var players = new ArrayList<HumanPlayer>();
        players.add(new HumanPlayer());
        return players;
    }

    @Override
    public void playerWon(HumanPlayer player) {

    }
}
