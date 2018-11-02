package net.thedanpage.worldshardestgame.genetic;

import net.thedanpage.worldshardestgame.Move;
import net.thedanpage.worldshardestgame.Player;

import java.util.ArrayList;
import java.util.Random;

public class GeneticPlayer extends Player {
    private Random rnd = new Random();
    private ArrayList<Move> moves;

    public GeneticPlayer(int moveCount, ArrayList<Move> moves) {
        this(moveCount);

        for(var i = 0; i < moves.size(); i++) {
            this.moves.set(i, moves.get(i));
        }
    }

    public GeneticPlayer(int moveCount) {
        super();

        this.moves = new ArrayList<>();
        initializeRandomMoves(moveCount);
    }

    public double fitness = 0;

    public void mutate(double mutationRate) {
        for(var i = 0; i < this.getMoves().size(); i++) {
            var randomDouble = rnd.nextDouble();
            var mutated = randomDouble <= mutationRate;
            if(mutated) {
                this.getMoves().set(i, getRandomMove());
            }
        }
    }

    public void initializeRandomMoves(int moveCount) {
        for(var i = 0; i < moveCount; i++) {
            this.moves.add(getRandomMove());
        }
    }

    public ArrayList<Move> getMoves() {
        return this.moves;
    }

    public Move getRandomMove() {
        return Move.values()[this.rnd.nextInt(Move.values().length)];
    }

    public Move getNextMove() {
        if(nextMoveIndex >= this.moves.size()) {
            this.dead = true;
            return Move.NEUTRAL;
        }
        return this.moves.get(nextMoveIndex++);
    }
}
