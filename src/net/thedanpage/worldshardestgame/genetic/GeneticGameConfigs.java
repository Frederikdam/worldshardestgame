package net.thedanpage.worldshardestgame.genetic;

import java.util.function.Function;

public class GeneticGameConfigs {
    public int populationSize;
    public int initialMoveCount;
    public double mutationRate = 0.05;
    public Function<Double, Double> mutationChange;

    public GeneticGameConfigs(int populationSize, int initialMoveCount, double mutationRate, Function<Double, Double> mutationChange) {
        this.populationSize = populationSize;
        this.initialMoveCount = initialMoveCount;
        this.mutationRate = mutationRate;
        this.mutationChange = mutationChange;
    }
}
