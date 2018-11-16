package net.thedanpage.worldshardestgame.qlearning;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.Move;

public class QLearningController extends Controller<QLearningPlayer, QLearningGame> {

    QTable qTable;

    public QLearningController(QTable qTable) {
        this.qTable = qTable;
    }

    @Override
    public Move getMove(QLearningGame game, QLearningPlayer player) {
        return qTable.getNextAction(game);
    }

    @Override
    public void didMove(QLearningGame game, QLearningPlayer player) {
        int distanceToGoal = (int)game.getLevel().getDistanceToGoal(player);
        int reward = distanceToGoal;
        if (player.deadByDot) reward = Integer.MAX_VALUE;
        qTable.updateQvalue(reward, game);
    }
}
