package net.thedanpage.worldshardestgame.qlearning;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.Move;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Arrays;

public class QTable {

    QLearningGameConfigs gameConfigs;
    Random randomGenerator = new Random();
    HashMap<String, float[]> table = new HashMap<>();
    QLearningGame prevState;
    Move prevAction;

    public QTable(QLearningGameConfigs configs) {
        this.gameConfigs = configs;
    }

    Move getNextAction(QLearningGame game){
        prevState = game;
        if(randomGenerator.nextFloat()<gameConfigs.explorationChance){
            prevAction=explore();
        } else {
            prevAction=getBestAction(game);
        }
        return prevAction;
    }

    Move getBestAction(QLearningGame game){
        float[] qValues = getActionsQValues(game);

        float bestQ = -1;
        Move bestMove = Move.NEUTRAL;

        for (Move move : Move.values()) {
            var qValue = qValues[move.ordinal()];
            if (qValue > bestQ) {
                bestQ = qValue;
                bestMove = move;
            }
        }

        if(bestQ == 0) { bestMove = explore(); }

        return bestMove;
    }

    Move explore(){
        var randomMove = Move.values()[randomGenerator.nextInt(gameConfigs.actionRange)];
        return randomMove;
    }

    void updateQvalue(int reward, QLearningGame game) {
        String prevStateStr = getGameString(prevState);
        float[] qValues = getActionsQValues(prevState);
        float prevActionQ = qValues[prevAction.ordinal()];

        Move move = getBestAction(game);
        String stateStr = getGameString(game);
        float[] qValState = table.get(stateStr);
        float actionQ = qValState[move.ordinal()];
        //System.out.println(Arrays.toString(qValues));
        qValues[prevAction.ordinal()] = (gameConfigs.learningRate * (reward + (gameConfigs.gammaValue * actionQ) - prevActionQ));
        table.put(prevStateStr, qValues);
    }

    String getGameString(QLearningGame game){
        var player = game.population.get(0);
        return player.x + "," + player.y;
    }

    float[] getActionsQValues(QLearningGame game){
        float[] actions = getValues(game);
        if(actions==null){
            float[] initialActions = new float[gameConfigs.actionRange];
            for(int i=0;i<gameConfigs.actionRange;i++) initialActions[i]=0.f;
            table.put(getGameString(game), initialActions);
            return initialActions;
        }
        return actions;
    }

    float[] getValues(QLearningGame game){
        String gameString = getGameString(game);
        return table.get(gameString);
    }
};