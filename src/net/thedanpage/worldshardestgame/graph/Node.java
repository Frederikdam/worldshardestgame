package net.thedanpage.worldshardestgame.graph;

import org.tritonus.share.ArraySet;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Node {
    public List<Edge> edges = new ArrayList<>();
    public Point position;
    private int distanceToStart;
    private int predictedDistance;
    public boolean isGoal = false;
    public boolean isWall = false;
    public boolean invalid = false;

    public Node(Point position) {
        this.position = position;
    }

    public Set<Node> adjacentSet;

    public void addEdge(Node node) {
        edges.add(new Edge(this, node, 1));
    }

    public int getDistanceToStart() {
        return distanceToStart;
    }

    public void setDistanceToStart(int distanceToStart) {
        this.distanceToStart = distanceToStart;
    }

    public int getPredictedDistance() {
        return predictedDistance;
    }

    public void setPredictedDistance(int distance) {
        this.predictedDistance = distance;
    }

    public Set<Node> getAdjacents() {
        Set<Node> adjacentSet = new HashSet<>();
        for (Edge e : this.edges) {
            adjacentSet.add(e.to);
        }
        return adjacentSet;
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
        invalid = true;
    }
}
