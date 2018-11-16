package net.thedanpage.worldshardestgame.human;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.Player;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.*;

public class HumanController extends Controller<HumanPlayer, HumanGame> {

    @Override
    public Move getMove(HumanGame game, HumanPlayer player) {
        return Move.NEUTRAL;
    }
}
