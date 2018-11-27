package net.thedanpage.worldshardestgame.astar;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.graph.Node;

import java.awt.*;
import java.util.ArrayList;

public class AStarController extends Controller<AStarPlayer, AStarGame> {

    ArrayList<Node> path = new ArrayList<>();

    @Override
    public Move getMove(AStarGame game, AStarPlayer player) {
        game.getLevel().updateGraph();
        var path = game.aStarSearch(player.getPosition(), game.getLevel().goals);
        if (path == null) return Move.NEUTRAL;
        this.path = new ArrayList<>(path);
        var from = path.pop().position;
        var to = path.pop().position;
        var move = pointToMove(from, to);
        return move;
    }

    private Move pointToMove(Point from, Point to) {
        if(from.x == to.x && from.y+1 == to.y) return Move.DOWN;
        if(from.x == to.x && from.y-1 == to.y) return Move.UP;
        if(from.x+1 == to.x && from.y+1 == to.y) return Move.RIGHTDOWN;
        if(from.x-1 == to.x && from.y-1 == to.y) return Move.LEFTUP;
        if(from.x+1 == to.x && from.y-1 == to.y) return Move.RIGHTUP;
        if(from.x-1 == to.x && from.y+1 == to.y) return Move.LEFTDOWN;
        if(from.x+1 == to.x && from.y == to.y) return Move.RIGHT;
        if(from.x-1 == to.x && from.y == to.y) return Move.LEFT;
        return Move.NEUTRAL;
    }

    public void drawPath(Graphics g) {
        this.path.forEach(node -> {
            g.setColor(Color.BLACK);
            g.fillRect(node.position.x, node.position.y, 1, 1);
        });
    }
}
