package net.thedanpage.worldshardestgame;

import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Controller<T extends Player, K extends Game> {
    public Queue<Move> moves = new LinkedList<>();
    public abstract Move getMove(K game, T player);
    public void didMove(K game, T player, Move move) {
        if(!player.isDead()) {
            moves.add(move);
        } else {
            moves.clear();
        }
    }
}
