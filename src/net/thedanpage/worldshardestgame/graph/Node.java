package net.thedanpage.worldshardestgame.graph;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Node {
    public List<Edge> edges = new ArrayList<>();
    public Point position;
    public boolean isGoal = false;

    public Node(Point position) {
        this.position = position;
    }

    public void addEdge(Node node) {
        edges.add(new Edge(this, node));
    }
}
