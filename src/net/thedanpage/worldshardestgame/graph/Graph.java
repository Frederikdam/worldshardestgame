package net.thedanpage.worldshardestgame.graph;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Graph {
    public List<Node> nodes = new ArrayList<>();

    //This data structure is provided in order to optimize lookup when building the graph
    public Node[][] nodeArray = new Node[1000][1000];

    public void addNode(Node node) {
        nodes.add(node);
        nodeArray[node.position.x][node.position.y] = node;
    }

    public Node getNodeFromPosition(Point point) {
        return nodeArray[point.x][point.y];
    }
}
