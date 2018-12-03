package net.thedanpage.worldshardestgame.astar;

import net.thedanpage.worldshardestgame.Dot;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.Player;
import net.thedanpage.worldshardestgame.graph.Node;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Stack;

public class Simulation {

    AStarGame game;
    Stack<Node> path;
    Stack<Node> pathCopy;
    Stack<Move> pathMoves;
    AStarPlayer player;
    Node lastPos;
    public Stack<Node> winningPath;

    public boolean playerIsAlive = true;

    ArrayList<Dot> enemies;

    public Simulation(AStarGame game, AStarPlayer player, Stack<Node> path) {
        this.game = game;
        this.player = player;
        this.enemies = cloneDots(game.getLevel().dots);
        this.path = path;
        this.pathCopy = (Stack<Node>) path.clone();
        this.lastPos = path.pop();
    }

    public Simulation(AStarGame game, Stack<Move> path, AStarPlayer player) {
        this.game = game;
        this.player = player;
        this.enemies = cloneDots(game.getLevel().dots);
        this.pathMoves = path;
    }

    public void simulateWithNodes() {
        advanceGameWithNodes();
        checkIfDead();
    }

    public void simulateWithMoves() {
        advanceGameWithMoves();
        checkIfDead();
    }

    private void advanceGameWithMoves() {
        if (pathMoves.isEmpty()) {
            playerIsAlive = false;
            //winningPath = pathCopy;
            return;
        }

        var move = pathMoves.pop();
        player.move(move, game.getLevel());
        moveEnemies();
    }

    private void advanceGameWithNodes() {
        if (path.isEmpty()) {
            playerIsAlive = false;
            //winningPath = pathCopy;
            return;
        }

        movePlayer();
        moveEnemies();
    }

    private void movePlayer() {
        var to = path.pop();
        var nextMove = pointToMove(lastPos.position, to.position);
        this.lastPos = to;
        player.move(nextMove, game.getLevel());
    }

    private void moveEnemies() {
        enemies.forEach(dot -> dot.update());
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

    private void checkIfDead() {
        for (Dot dot : enemies) {
            if (player.collidesWith(dot.getBounds())) {
                this.playerIsAlive = false;
            }
        }
    }

    private ArrayList<Dot> cloneDots(ArrayList<Dot> dots) {
        ArrayList<Dot> clone = new ArrayList<>();
        dots.forEach(dot -> clone.add(new Dot(dot)));
        return clone;
    }
}
