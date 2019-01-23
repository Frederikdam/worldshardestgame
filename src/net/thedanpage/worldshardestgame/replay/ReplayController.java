package net.thedanpage.worldshardestgame.replay;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Move;

import java.util.Queue;

public class ReplayController extends Controller<ReplayPlayer, ReplayGame> {

    @Override
    public Move getMove(ReplayGame game, ReplayPlayer player) {
        System.out.println("moves: " + game.controller.moves.size());
        if (moves.isEmpty()) return Move.NEUTRAL;
        return moves.remove();
    }

    @Override
    public void didMove(ReplayGame game, ReplayPlayer player, Move move) {
    }
}
