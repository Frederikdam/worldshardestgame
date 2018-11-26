package net.thedanpage.worldshardestgame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

import static net.thedanpage.worldshardestgame.Sound.COIN;

public abstract class Game<T extends Player> extends JPanel implements ActionListener {

    Timer t = new Timer(5, this);
    public List<T> population;

    protected boolean goalReached = false;
    boolean running = false;

    public Controller controller;
    GameLevel level;

    public Game(Controller controller, GameLevel level) {
        this.controller = controller;
        this.level = level;
    }

    public abstract int generationCount();
    public abstract void populationIsDead();
    public abstract void playerDiedToEnemy(T player);
    public abstract List<T> initializePopulation();
    public abstract void playerWon(T player);

    public GameLevel getLevel() { return level; }

    public void setup() {
        population = initializePopulation();
    }

    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        advanceGame();

        render(g);

        t.start();

        Toolkit.getDefaultToolkit().sync();
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
        var count = generationCount();
        drawRightJustifiedString(count > 0 ? "Generation: " + count : "", 750, 17, g);
        g.dispose();
    }

    public void advanceGame() {
        if (!running) {
            for (var player : population) {
                player.respawn(getLevel());
            }
            running = true;
        }

        var deadPlayerCount = 0;
        advanceDots(getLevel());

        for(T player : population) {
            var nextMove = controller.getMove(this, player);
            advancePlayer(nextMove, getLevel(), player);
            controller.didMove(this, player);
            if(player.isDead()) {
                deadPlayerCount++;
            }
        }

        if(deadPlayerCount == population.size() && !goalReached) {
            populationIsDead();
        }
    }

    public void advanceDots(GameLevel level) {
        level.updateDots();
    }

    private void checkIfCoinCollected(GameLevel level, T player) {
        if (level.coins != null) {
            for (Coin coin : level.coins) {
                if (player.collidesWith(coin.getBounds()) && !coin.collected) {
                    coin.collected = true;
                    MusicPlayer.play(COIN);
                }
            }
        }
    }

    private void checkIfGoalReached(GameLevel level, T player) {
        if (level.getTileMap() != new ArrayList<Tile>()) {
            if (level.allCoinsCollected()) {
                for (Tile t : level.getTileMap()) {
                    if (t.getType() == 3 && player.collidesWith(t.getBounds())) {
                        goalReached = true;
                        playerWon(player);
                    }
                }
            }
        }
    }

    private void checkIfDead(GameLevel level, T player) {
        for (Dot dot : level.dots) {
            if (player.collidesWith(dot.getBounds())) {
                player.setDead(true);
                player.deadByDot = true;
                playerDiedToEnemy(player);
            }
        }
    }

    public void advancePlayer(Move move, GameLevel level, T player) {
        if (!player.isDead()) {
            player.move(move, level);
            checkIfCoinCollected(level, player);
            checkIfGoalReached(level, player);
            checkIfDead(level, player);
        }
    }

    public void actionPerformed(ActionEvent arg0) {
        repaint();
    }

    private void drawRightJustifiedString(String s, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s));
        g.drawString(s, x, h);
    }
}