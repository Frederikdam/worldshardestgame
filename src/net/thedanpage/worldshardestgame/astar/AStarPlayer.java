package net.thedanpage.worldshardestgame.astar;

import net.thedanpage.worldshardestgame.Player;

public class AStarPlayer extends Player {
    public AStarPlayer clone() {
        var clone = new AStarPlayer();
        clone.x = this.x;
        clone.y = this.y;
        return clone;
    }
}
