package net.thedanpage.worldshardestgame.astar;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Move;

public class AStarController extends Controller<AStarPlayer, AStarGame> {

    @Override
    public Move getMove(AStarGame game, AStarPlayer player) {
        game.getLevel().updateGraph();
        return Move.NEUTRAL;
    }
}
