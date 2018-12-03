package net.thedanpage.worldshardestgame.astar;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.graph.Node;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AStarController extends Controller<AStarPlayer, AStarGame> {

    ArrayList<Node> path = new ArrayList<>();
    ArrayList<Stack<Node>> paths = new ArrayList<>();
    ExecutorService executor = Executors.newWorkStealingPool();

    @Override
    public Move getMove(AStarGame game, AStarPlayer player) {
        game.trimmedGoals.forEach(goal -> {
            var p = game.aStarSearch(player.getPosition(), goal);
            if (p != null) paths.add(p);
        });

        int[] nMoves = new int[paths.size()];
        for(int i = 0; i<paths.size(); i++) {
            var pathClone = (Stack<Node>)paths.get(i).clone();
            var sim = new Simulation(game, player, pathClone);
            while (sim.playerIsAlive) {
                nMoves[i]++;
                sim.simulate();
            }
        }

        int bestValue = -1;
        int bestPathIndex = 0;
        for (int i = 0; i<nMoves.length; i++) {
            if (nMoves[i] > bestValue) {
                bestValue = nMoves[i];
                bestPathIndex = i;
            }
        }

        Stack<Node> bestPath = paths.get(bestPathIndex);
        this.path = new ArrayList<>(bestPath);
        var from = bestPath.pop().position;
        var to = bestPath.pop().position;
        var move = pointToMove(from, to);

        return move;
    }

    /*@Override
    public Move getMove(AStarGame game, AStarPlayer player) {
        final long startTime = System.currentTimeMillis();
        game.getLevel().updateGraph();
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time UPDATE GRAPH: " + (endTime - startTime));

        final long startTime2 = System.currentTimeMillis();
        Stack<Node> path = null;
        for (Point point : game.trimmedGoals) {
            path = game.aStarSearch(player.getPosition(), point);
            if (path != null) break;
        }
        final long endTime2 = System.currentTimeMillis();
        System.out.println("Total execution time ASTAR: " + (endTime2 - startTime2));

        if (path == null) return Move.NEUTRAL;
        this.path = new ArrayList<>(path);
        var from = path.pop().position;
        var to = path.pop().position;
        var move = pointToMove(from, to);
        return move;
    }*/

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
