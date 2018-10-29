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
import static net.thedanpage.worldshardestgame.Sound.COIN;

public class Game extends JPanel implements ActionListener {

    public Controller controller;

    public Timer t = new Timer(5, this);

    public List<Player> population = new ArrayList<>();

    public int populationSize = 100;

    public Player winningPlayer = null;

    private int playerMoveCount = 10;

    private int generation = 1;

    public boolean goalReached = false;

    boolean running = false;

    public GameLevel currentLevel;

    public Game(Controller controller, GameLevel level) {
        this.controller = controller;
        this.currentLevel = level;
        intializePopulation();
    }

    public double calculateFitness(Player player) {
        return currentLevel.getDistanceToGoal(player);
    }

    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        update(g);
        advanceGame();
        render(g);

        t.start();

        Toolkit.getDefaultToolkit().sync();
    }

    public void update(Graphics g) {
        if(running) return;

        for (var player : population) {
            player.respawn(currentLevel);
        }

        running = true;
    }

    private void render(Graphics g) {
        currentLevel.drawTiles(g);
        currentLevel.drawCoins(g);
        currentLevel.drawDots(g);
        for (var player : population) player.draw(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 22);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Tahoma", Font.BOLD, 18));
        drawRightJustifiedString("Generation: " + this.generation, 750, 17, g);
        g.dispose();
    }

    public void advanceGame() {
        var deadPlayerCount = 0;
        advanceDots(currentLevel);
        for(Player player : population) {
            var nextMove = controller.getMove(this, player);
            advancePlayer(nextMove, currentLevel, player);
            if(player.isDead()) deadPlayerCount++;
        }

        if(deadPlayerCount == populationSize) {
            evaluateGeneration();
        }

        if (goalReached) {
            System.out.println("Goal Reached!");
            System.exit(0);
        }
    }

    public void advanceDots(GameLevel level) {
        level.updateDots();
    }

    private void checkIfCoinCollected(GameLevel level, Player player) {
        if (level.coins != null) {
            for (Coin coin : level.coins) {
                if (player.collidesWith(coin.getBounds()) && !coin.collected) {
                    coin.collected = true;

                    //Coin sound
                    MusicPlayer.play(COIN);
                }
            }
        }
    }

    private void checkIfGoalReached(GameLevel level, Player player) {
        if (level.getTileMap() != new ArrayList<Tile>()) {
            if (level.allCoinsCollected()) {
                for (Tile t : level.getTileMap()) {
                    if (t.getType() == 3 && player.collidesWith(t.getBounds())) {
                        goalReached = true;
                        winningPlayer = player;
                    }
                }
            }
        }
    }

    private void checkCollisions(GameLevel level, Player player) {
        player.checkCollisionUp(level);
        player.checkCollisionDown(level);
        player.checkCollisionLeft(level);
        player.checkCollisionRight(level);
    }

    private void checkIfDead(GameLevel level, Player player) {
        if (!player.isDead()) {
            for (Dot dot : level.dots) {
                if (player.collidesWith(dot.getBounds())) {
                    player.setDead(true);
                    player.opacity = 0;
                }
            }
        }
        if (player.isDead()) {
            player.fitness = calculateFitness(player);
            player.opacity = 0;
        }
    }

    public void advancePlayer(Move move, GameLevel level, Player player) {
        player.move(move, level);

        checkIfCoinCollected(level, player);
        checkIfGoalReached(level, player);
        checkCollisions(level, player);
        checkIfDead(level, player);
    }

    public void evaluateGeneration() {
        this.generation++;
        if(this.generation % 5 == 0) {
            if (this.playerMoveCount < 5000) this.playerMoveCount *= 1.2;
        }

        var bestCandidates = selection();
        var newPopulation = mutate(bestCandidates);
        population = newPopulation;
        currentLevel.reset();
        for(Player player : population) {
            player.respawn(currentLevel);
        }

        System.out.println("Fitness: " + bestCandidates.get(0).fitness + " MoveCount: " + bestCandidates.get(0).getMoves().size());
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

    private void drawRightJustifiedString(String s, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s));
        g.drawString(s, x, h);
    }

    private void intializePopulation() {
        for (var i = 0; i < populationSize; i++) {
            var player = new Player(playerMoveCount);
            player.respawn(currentLevel);
            population.add(player);
        }
    }
}