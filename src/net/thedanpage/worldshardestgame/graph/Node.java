package net.thedanpage.worldshardestgame.graph;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Node {
    public List<Edge> edges = new ArrayList<>();
    public Point position;
    public boolean isGoal = false;
    public boolean isWall = false;

    public Node(Point position) {
        this.position = position;
    }

    public void addEdge(Node node) {
        edges.add(new Edge(this, node, 1));
    }

    public void invalidate() {
        for(Edge edge : edges) {
            var neighbour = edge.to;
            for(Edge neighbourEdge : new ArrayList<>(neighbour.edges)) {
                if (neighbourEdge.to == this) {
                    neighbour.edges.remove(neighbourEdge);
                }
            }
        }
    }
}
