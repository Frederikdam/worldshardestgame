package net.thedanpage.worldshardestgame;

import kuusisto.tinysound.TinySound;
import net.thedanpage.worldshardestgame.controllers.Controller;
import net.thedanpage.worldshardestgame.controllers.GeneticController;

import java.io.File;
import java.util.ArrayList;

import static net.thedanpage.worldshardestgame.Sound.BACKGROUND;

public class Main {
    public static void main(String[] args) {
        MusicPlayer.play(BACKGROUND);

        var player = new Player();
        var controller = new GeneticController();
        var levels = createLevels();
        var game = new Game(controller, player, levels);

        var frame = new Frame();
        frame.add(game);
        frame.setVisible(true);
    }

    public static boolean levelExists(int levelNumber) {
        String fileUrl = "net/thedanpage/worldshardestgame/resources/maps/level_" + levelNumber + ".txt";
        return ClassLoader.getSystemResource(fileUrl) != null;
    }

    public static ArrayList<GameLevel> createLevels() {
        ArrayList<GameLevel> levels = new ArrayList<>();
        int levelCount = 1;
        while (levelExists(levelCount)) {
            GameLevel level = new GameLevel(levelCount);
            levels.add(level);
            levelCount++;
        }
        return levels;
    }
}
