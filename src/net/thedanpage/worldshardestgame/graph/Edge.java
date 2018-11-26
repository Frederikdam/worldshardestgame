package net.thedanpage.worldshardestgame.graph;

public class Edge {
    public Node from;
    public Node to;
    public boolean invalid = false;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public void reset() { invalid = false; }

    public void invalidate() { invalid = true; }
}
