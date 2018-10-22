package net.thedanpage.worldshardestgame;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    public Frame() {
        super();
        setupFrame();
    }

    public void setupFrame() {
        this.setTitle("World's Hardest Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(800, 622));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(ClassLoader.getSystemResource("net/thedanpage/worldshardestgame/resources/favicon.png")).getImage());
    }
}
