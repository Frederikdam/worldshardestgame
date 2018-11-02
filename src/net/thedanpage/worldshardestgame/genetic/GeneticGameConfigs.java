package net.thedanpage.worldshardestgame.genetic;

import java.util.function.Function;

public class GeneticGameConfigs {
    public int populationSize;
    public int moveCount;
    public double mutationRate;
    public Function<Integer, Integer> mutationChange;

    public GeneticGameConfigs(int populationSize, int moveCount, double mutationRate, Function<Integer, Integer> mutationChange) {
        this.populationSize = populationSize;
        this.moveCount = moveCount;
        this.mutationRate = mutationRate;
        this.mutationChange = mutationChange;
    }
}
