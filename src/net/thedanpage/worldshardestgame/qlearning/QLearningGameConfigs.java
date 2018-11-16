package net.thedanpage.worldshardestgame.qlearning;

public class QLearningGameConfigs {
    int actionRange;
    float explorationChance;
    float gammaValue;
    float learningRate;

    public QLearningGameConfigs(int actionRange, float explorationChance, float gammaValue, float learningRate) {
        this.actionRange = actionRange;
        this.explorationChance = explorationChance;
        this.gammaValue = gammaValue;
        this.learningRate = learningRate;
    }
}
