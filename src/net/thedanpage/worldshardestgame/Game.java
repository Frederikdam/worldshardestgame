package net.thedanpage.worldshardestgame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;
import net.thedanpage.worldshardestgame.controllers.Controller;
import net.thedanpage.worldshardestgame.controllers.ExampleController;
import net.thedanpage.worldshardestgame.controllers.GeneticController;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class Game extends JPanel implements ActionListener {

    public Controller controller;

    private boolean replay = true;

    private Timer t = new Timer(5, this);

    private int replayLevel = 1;

    public List<Player> population = new ArrayList<>();

    public int populationSize = 50;

    private int playerMoveCount = 500;

    private int generation = 1;

    boolean running = false;

    private Player player;

    private ArrayList<GameLevel> levels;

    private int currentLevelIndex = 0;

    private GameLevel currentLevel;

    public Game(Controller controller, Player player, ArrayList<GameLevel> levels) {
        this.controller = controller;
        this.player = player;
        this.levels = levels;
        this.currentLevel = levels.get(currentLevelIndex);
        intializePopulation();
    }

    public void updateFitness() {
        for(var player : population) {
            player.fitness = calculateFitness(player);
        }
    }

    private double calculateFitness(Player player) {
        return currentLevel.getDistanceToGoal(player);
    }

    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        update(g);
        render(g);

        t.start();

        Toolkit.getDefaultToolkit().sync();
    }

    public void goalReached(Player winnerPlayer) {
        if (!replay) {
            advanceToNextLevel();
        } else {
            advanceToReplay(winnerPlayer);
        }
    }

    private void advanceToNextLevel() {
        currentLevel = levels.get(currentLevelIndex++);
        for (var player : population) {
            player.reset();
        }
        replay = true;
    }

    private void advanceToReplay(Player winnerPlayer) {
        if (replayLevel != currentLevel.getLevelNum()) {
            replayLevel = currentLevel.getLevelNum();
            for (var player : population) {
                player.goalReached = false;
                player.setDead(true);
            }
            return;
        }
        Player replayPlayer = winnerPlayer;
        replayPlayer.reset();
        replayPlayer.respawn(currentLevel);
        replayPlayer.setMoves(winnerPlayer.getMoves());
        replayPlayer.goalReached = false;
        replayPlayer.nextMoveIndex = 0;

        replay = false;
    }



    /** Update the game.
     *
     * @param g
     * */
    public void update(Graphics g) {
        if(running) return;

        for (var player : population) {
            player.reset();
            player.respawn(currentLevel);
        }

        running = true;
    }

    /** Draw the game's graphics.
     *
     * @param g
     */
    private void render(Graphics g) {
        var deadPlayerCount = 0;
        var isGoalReached = false;
        Player winnerPlayer = null;

        currentLevel.drawTiles(g);
        currentLevel.drawCoins(g);
        currentLevel.drawDots(g);
        currentLevel.updateDots();

        for (var player : population) {
            if(player.isDead()) deadPlayerCount++;
            player.draw(g);
            player.update(this, controller);

            if (player.goalReached) {
                winnerPlayer = player;
                isGoalReached = true;
                break;
            }
        }

        if(deadPlayerCount == populationSize) {
            evaluateGeneration();
        }

        if (isGoalReached) {
            goalReached(winnerPlayer);
        }

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 22);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 18));
        drawRightJustifiedString("Generation: " + this.generation, 750, 17, g);
        drawCenteredString(currentLevel.getLevelNum() + "/" + levels.size(), 400, 17, g);
        g.dispose();
    }

    public void evaluateGeneration() {
        this.generation++;
        updateFitness();
        if(this.generation % 5 == 0) {
            this.playerMoveCount += 50;
        }

        var bestCandidates = selection();
        var newPopulation = mutate(bestCandidates);
        population = newPopulation;
        currentLevel.reset();
        for(Player player : population) {
            player.respawn(currentLevel);
        }

        System.out.println("Fitness: " + bestCandidates.get(0).fitness + " MoveCount: " + bestCandidates.get(0).getMoves().length);
    }

    public List<Player> selection() {
        Player bestPlayer = null;
        double bestFitness = Double.MAX_VALUE;
        for(var player : population) {
            if(player.fitness < bestFitness) {
                bestPlayer = player;
                bestFitness = player.fitness;
            }
        }
        return Arrays.asList(bestPlayer);
    }

    public List<Player> mutate(List<Player> candidates) {
        List<Player> children = new ArrayList<>();
        var childrenCountPerCandidate = populationSize / candidates.size();
        for(var candidate : candidates) {
            for(var i = 0; i < childrenCountPerCandidate; i++) {
                var child = new Player(this.playerMoveCount, candidate.getMoves());
                child.mutate();
                children.add(child);
            }
        }
        return children;
    }

    public GameLevel getLevel() { return currentLevel; }

    public void actionPerformed(ActionEvent arg0) {
        repaint();
    }

    private void drawCenteredString(String s, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w*2 - fm.stringWidth(s)) / 2;
        g.drawString(s, x, h);
    }

    private void drawRightJustifiedString(String s, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s));
        g.drawString(s, x, h);
    }

    /**
     * Convert an exception to a String with full stack trace
     *
     * @param ex
     *            the exception
     * @return A string with the full stacktrace error text
     */
    public static String getStringFromStackTrace(Throwable ex) {
        if (ex == null) {
            return "";
        }
        StringWriter str = new StringWriter();
        PrintWriter writer = new PrintWriter(str);
        try {
            ex.printStackTrace(writer);
            return str.getBuffer().toString();
        } finally {
            try {
                str.close();
                writer.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private void intializePopulation() {
        for (var i = 0; i < populationSize; i++) {
            population.add(new Player(playerMoveCount));
        }
    }
}