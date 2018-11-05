package net.thedanpage.worldshardestgame;

import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.Player;

public abstract class Controller<T extends Player> {
    public abstract Move getMove(Game game, T player);
}
