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
    ExecutorService executor = Executors.newWorkStealingPool();

    boolean winningPathFound = false;
    Stack<Node> winningPath;
    Move lastWinningMove;


    @Override
    public Move getMove(AStarGame game, AStarPlayer player) {

        var playerPos = player.getPosition();

        if(winningPathFound && game.getLevel().allCoinsCollected()) {
            if (winningPath.isEmpty()) {
                winningPathFound = false;
                return lastWinningMove;
            }
            var nextMove = pointToMove(playerPos, winningPath.pop().position);
            lastWinningMove = nextMove;
            return nextMove;
        }

        final long startTime = System.currentTimeMillis();
        game.getLevel().updateGraph();
        ArrayList<Stack<Node>> paths = new ArrayList<>();

        if(game.getLevel().allCoinsCollected()) {
            game.trimmedGoals.forEach(goal -> {
                var p = game.aStarSearch(playerPos, goal);
                if (p != null) paths.add(p);
            });
        } else {
            game.getLevel().coins.forEach(coin -> {
                var p = game.aStarSearch(playerPos, new Point((int)coin.getBounds().getCenterX(), (int)coin.getBounds().getCenterY()+22));
                if (p != null) paths.add(p);
            });
        }

        if(paths.isEmpty()) return nextFleeMove(game, player.clone());

        int[] nMoves = new int[paths.size()];
        outer:for(int i = 0; i<paths.size(); i++) {
            var pathClone = (Stack<Node>)paths.get(i).clone();
            var playerClone = player.clone();
            var sim = new Simulation(game, playerClone, pathClone);
            while (sim.playerIsAlive) {
                nMoves[i]++;
                sim.simulateWithNodes();
                if(sim.path.isEmpty() && game.getLevel().allCoinsCollected() && !playerClone.isDead()) {
                    game.getLevel().resetGraph();
                    winningPathFound = true;
                    winningPath = sim.pathCopy;
                    path = new ArrayList<>(winningPath);
                    break outer;
                }
            }
        }

        if(winningPathFound) {
            return didFindWinningPath();
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
        if(bestPath.isEmpty()) return Move.NEUTRAL;
        this.path = new ArrayList<>(bestPath);
        var from = bestPath.pop().position;
        var to = bestPath.pop().position;
        var move = pointToMove(from, to);

        final long endTime = System.currentTimeMillis();
        System.out.println("getMove: " + (endTime - startTime));

        return move;
    }

    private Move didFindWinningPath() {
        var from = winningPath.pop().position;
        var to = winningPath.pop().position;
        var move = pointToMove(from, to);
        return move;
    }

    private Move nextFleeMove(AStarGame game, AStarPlayer player) {
        int[] numberOfMoves = new int[Move.values().length];
        for (int i = 0; i < Move.values().length; i++) {
            var simDepth = 10;
            var stack = new Stack<Move>();
            var move = Move.values()[i];
            for (int j = 0; j < simDepth; j++) stack.push(move);
            var sim = new Simulation(game, stack, player);
            while(sim.playerIsAlive) {
                numberOfMoves[i]++;
                sim.simulateWithMoves();
            }
        }
        int bValue = -1;
        int bPathIndex = 0;
        for (int i = 0; i<numberOfMoves.length; i++) {
            if (numberOfMoves[i] > bValue) {
                bValue = numberOfMoves[i];
                bPathIndex = i;
            }
        }
        var nextMove = Move.values()[bPathIndex];
        return nextMove;
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
