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
    private double distanceToStart;
    private double predictedDistance;
    public boolean isGoal = false;
    public boolean isWall = false;

    public Node(Point position) {
        this.position = position;
    }

    public Node(Point position, List<Edge> edges) {
        this.position = position;
        this.edges = edges;
    }

    public Set<Node> adjacentSet;

    public void addEdge(Node node) {
        edges.add(new Edge(this, node));
    }

    public double getDistanceToStart() {
        return distanceToStart;
    }

    public double score() {
        return distanceToStart + predictedDistance;
    }

    public void setDistanceToStart(double distanceToStart) {
        this.distanceToStart = distanceToStart;
    }

    public double getPredictedDistance() {
        return predictedDistance;
    }

    public void setPredictedDistance(double distance) {
        this.predictedDistance = distance;
    }

    public Set<Node> getAdjacents() {
        Set<Node> adjacentSet = new HashSet<>();
        for (Edge e : this.edges) {
            if (e.invalid) continue;
            adjacentSet.add(e.to);
        }
        return adjacentSet;
    }

    public List<Edge> connectedEdgesToNode() {
        var connectedEdgesToNode = new ArrayList<Edge>();
        for(Edge edge : edges) {
            var neighbour = edge.to;
            for(Edge neighbourEdge : new ArrayList<>(neighbour.edges)) {
                if (neighbourEdge.to == this) {
                    connectedEdgesToNode.add(neighbourEdge);
                }
            }
        }
        return connectedEdgesToNode;
    }
}
