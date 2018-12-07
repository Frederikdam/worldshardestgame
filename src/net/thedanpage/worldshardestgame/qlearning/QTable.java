package net.thedanpage.worldshardestgame.qlearning;
import net.thedanpage.worldshardestgame.Dot;
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
    String prevState;
    Move prevAction;
    String lastPosition = "";
    int movesN = 0;
    int moveRate = 0;
    public int getMovesN() {
        return movesN;
    }
    public void setMovesN(int movesN) {
        this.movesN = movesN;
    }
    public int getMoveRate() {
        return moveRate;
    }
    public void setMoveRate(int moveRate) {
        this.moveRate = moveRate;
    }


    public QTable(QLearningGameConfigs configs) {
        this.gameConfigs = configs;
    }

    Move getNextAction(QLearningGame game){
        String gameString = getGameString(game);
        prevState = gameString;
        if(randomGenerator.nextFloat()<gameConfigs.explorationChance){
            prevAction=explore();
        } else {
            prevAction=getBestAction(gameString);
        }
        lastPosition = getPlayerPosition(game);
        return prevAction;
    }

    String getPlayerPosition(QLearningGame game) {
        return game.population.get(0).x + ":" + game.population.get(0).y;
    }

    String getEnemyPositions(QLearningGame game) {
        String result = "";
        for(Dot dot : game.getLevel().dots) {
            result += dot.getBounds().getCenterX() + ":" + dot.getBounds().getCenterY() + ",";
        }
        return result;
    }

    Move getBestAction(String gameString){
        float[] qValues = getActionsQValues(gameString);

        float bestQ = -1;
        Move bestMove = Move.NEUTRAL;

        for (Move move : Move.values()) {
            var qValue = qValues[move.ordinal()];
            if (qValue > bestQ) {
                bestQ = qValue;
                bestMove = move;
            }
        }

        if(bestQ == 0) return Move.RIGHT;

        return bestMove;
    }

    Move explore(){
        var randomMove = Move.values()[randomGenerator.nextInt(gameConfigs.actionRange)];
        return randomMove;
    }

    void updateQvalue(int reward, QLearningGame game) {
        float[] qValues = getActionsQValues(prevState);
        float prevActionQ = qValues[prevAction.ordinal()];

        String stateStr = getGameString(game);
        Move move = getBestAction(stateStr);
        float[] qValState = table.get(stateStr);
        float actionQ = qValState[move.ordinal()];
        //System.out.println(Arrays.toString(qValues));
        if (lastPosition.equals(getPlayerPosition(game)) && prevAction != Move.NEUTRAL) {
            qValues[prevAction.ordinal()] = -1;
        } else {
            qValues[prevAction.ordinal()] = gameConfigs.learningRate * (reward + (gameConfigs.gammaValue * actionQ) - prevActionQ);
        }
        table.put(prevState, qValues);
    }

    String getGameString(QLearningGame game){
        var gameString = "";
        gameString += getPlayerPosition(game);// + ", " + getMoveRate();
        //System.out.println(gameString);
        //gameString += getEnemyPositions(game);
        return gameString;
    }

    float[] getActionsQValues(String gameString){
        float[] actions = getValues(gameString);
        if(actions==null){
            float[] initialActions = new float[gameConfigs.actionRange];
            for(int i=0;i<gameConfigs.actionRange;i++) initialActions[i]=0.f;
            table.put(gameString, initialActions);
            return initialActions;
        }
        return actions;
    }

    float[] getValues(String gameString){
        return table.get(gameString);
    }
};