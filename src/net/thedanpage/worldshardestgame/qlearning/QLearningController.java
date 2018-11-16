package net.thedanpage.worldshardestgame.qlearning;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.Move;

public class QLearningController extends Controller<QLearningPlayer> {
    @Override
    public Move getMove(Game game, QLearningPlayer player) {
        return Move.NEUTRAL;
    }
}
