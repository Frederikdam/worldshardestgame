package net.thedanpage.worldshardestgame;

import kuusisto.tinysound.TinySound;
import net.thedanpage.worldshardestgame.controllers.Controller;
import net.thedanpage.worldshardestgame.controllers.GeneticController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import static net.thedanpage.worldshardestgame.Sound.BACKGROUND;

public class Main {
    public static void main(String[] args) {
        run(1, false, false, true);
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

    public static void run(int levelNumber, boolean visual, boolean muted, boolean replay) {
        if (!muted) MusicPlayer.play(BACKGROUND);

        var controller = new GeneticController();
        var levels = createLevels();
        var level = levels.get(levelNumber-1);
        var game = new Game(controller, level);

        if (visual) {
            var frame = new Frame();
            frame.add(game);
            frame.setVisible(true);
        } else {
            while (true) {
                game.advanceGame();
            }
        }
    }
}
