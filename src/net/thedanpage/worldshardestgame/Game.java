package net.thedanpage.worldshardestgame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;
import net.thedanpage.worldshardestgame.controllers.Controller;

import static net.thedanpage.worldshardestgame.Sound.COIN;

public abstract class Game extends JPanel implements ActionListener {

    Timer t = new Timer(5, this);
    List<Player> population = new ArrayList<>();

    int generation = 1;
    boolean goalReached = false;
    boolean running = false;

    Controller controller;
    GameLevel level;
    Player winningPlayer = null;

    public Game(Controller controller, GameLevel level) {
        this.controller = controller;
        this.level = level;
        intializePopulation();
    }

    public boolean goalReached() { return goalReached; }
    public void goalReached(boolean goalReached) { this.goalReached = goalReached; }

    public GameLevel getLevel() { return level; }

    public double calculateFitness(Player player) {
        return getLevel().getDistanceToGoal(player);
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
            player.respawn(getLevel());
        }

        running = true;
    }

    private void render(Graphics g) {
        getLevel().drawTiles(g);
        getLevel().drawCoins(g);
        getLevel().drawDots(g);

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
        advanceDots(getLevel());

        for(Player player : population) {
            var nextMove = player.getNextMove();
            advancePlayer(nextMove, getLevel(), player);
            if(player.isDead()) deadPlayerCount++;
        }

        if(deadPlayerCount == populationSize) {
            evaluateGeneration();
        }

        if (goalReached()) {
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
                        goalReached(true);
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
        getLevel().reset();
        for(Player player : population) {
            player.respawn(getLevel());
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
            player.respawn(getLevel());
            population.add(player);
        }
    }
}