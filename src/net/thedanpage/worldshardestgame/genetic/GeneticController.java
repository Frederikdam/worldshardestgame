package net.thedanpage.worldshardestgame.genetic;

import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.Player;
import net.thedanpage.worldshardestgame.Controller;

public class GeneticController extends Controller {

    @Override
    public Move getMove(Game game, Player player) {
        var geneticPlayer = (GeneticPlayer) player;
        return geneticPlayer.getNextMove();
    }
}
