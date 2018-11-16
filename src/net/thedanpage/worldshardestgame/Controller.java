package net.thedanpage.worldshardestgame;

import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.Player;

public abstract class Controller<T extends Player, K extends Game> {
    public abstract Move getMove(K game, T player);
    public void didMove(K game, T player) {}
}
