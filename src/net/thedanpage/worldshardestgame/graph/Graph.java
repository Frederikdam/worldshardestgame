package net.thedanpage.worldshardestgame.graph;

import net.thedanpage.worldshardestgame.Dot;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Graph {
    public List<Node> nodes;

    //This data structure is provided in order to optimize lookup when building the graph
    public Node[][] nodeArray;

    public Graph() {
        this.nodes = new ArrayList<>();
        this.nodeArray = new Node[1000][1000];
    }

    public Graph(Graph clone) {
        this.nodes = new ArrayList<>(clone.nodes);
        this.nodeArray = clone.nodeArray.clone();
    }

    public void addNode(Node node) {
        nodes.add(node);
        nodeArray[node.position.x][node.position.y] = node;
    }

    public void removeNode(Node node) {
        try {
            nodeArray[node.position.x][node.position.y] = null;
            nodes.remove(node);
        } catch (Exception e) {}
    }

    public Node getNodeFromPosition(Point point) {
        try {
            return nodeArray[point.x][point.y];
        } catch (Exception e) {
            return null;
        }
    }

    public void markNodesAsWallUpDown(Point from, Point to) {
        for (int y = from.y; y < to.y; y++) {
            Node n = nodeArray[from.x][y];
            n.isWall = true;
        }
    }

    public void markNodesAsWallDownUp(Point from, Point to) {
        for (int y = from.y; y > to.y; y--) {
            Node n = nodeArray[from.x][y];
            n.isWall = true;
        }
    }

    public void markNodesAsWallLeftRight(Point from, Point to) {
        for(int x = from.x; x < to.x; x++) {
            Node n = nodeArray[x][from.y];
            n.isWall = true;
        }
    }

    public void markNodesAsWallRightLeft(Point from, Point to) {
        for(int x = from.x; x > to.x; x--) {
            Node n = nodeArray[x][from.y];
            n.isWall = true;
        }
    }
}