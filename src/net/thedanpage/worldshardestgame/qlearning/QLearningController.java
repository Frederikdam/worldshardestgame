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

        double fitness;
        if (game.getLevel().allCoinsCollected()) {
            fitness = 10000 - game.getLevel().getDistanceToGoal(player);
        } else {
            fitness = 5000 - game.getLevel().getDistanceToNextCoin(player);
        }

        if (player.deadByDot) {
            fitness = 0;
            qTable.setMovesN(0);
            qTable.setMoveRate(0);
        }
        qTable.setMovesN(qTable.getMovesN()+1);
        if (qTable.getMovesN() % 20 == 0) {
            qTable.setMoveRate(qTable.getMoveRate()+1);
        }
        qTable.updateQvalue((int)fitness, game);
    }
}
