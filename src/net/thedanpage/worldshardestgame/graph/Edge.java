package net.thedanpage.worldshardestgame.graph;

public class Edge {
    public Node from;
    public Node to;
    public double weight;

    public Edge(Node from, Node to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
}
