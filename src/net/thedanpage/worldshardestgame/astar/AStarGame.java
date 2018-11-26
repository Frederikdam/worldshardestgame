package net.thedanpage.worldshardestgame.astar;

import net.thedanpage.worldshardestgame.Controller;
import net.thedanpage.worldshardestgame.Game;
import net.thedanpage.worldshardestgame.GameLevel;
import net.thedanpage.worldshardestgame.graph.Graph;
import net.thedanpage.worldshardestgame.graph.Node;
import net.thedanpage.worldshardestgame.qlearning.QLearningPlayer;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AStarGame extends Game<AStarPlayer> {

    GameLevel level;
    public AStarGame(Controller controller, GameLevel level) {
        super(controller, level);
        this.level = level;
        this.level.buildGraph();
        this.level.removeDotsFromGraph();
        this.setup();

        aStarSearch(new Point(population.get(0).x,population.get(0).y), new Point(410,300));
    }

    @Override
    public void populationIsDead() {
        restartLevel();
    }

    @Override
    public void playerWon(AStarPlayer player) {
        restartLevel();
        System.out.println("We won!");
    }

    @Override
    public void playerDiedToEnemy(AStarPlayer player) {}

    @Override
    public List<AStarPlayer> initializePopulation() {
        var players = new ArrayList<AStarPlayer>();
        players.add(new AStarPlayer());
        return players;
    }

    @Override
    public int generationCount() {
        return -1;
    }

    private void restartLevel() {
        getLevel().reset();

        for(AStarPlayer player : this.population) {
            player.respawn(getLevel());
        }
    }

    public List<Point> aStarSearch(Point start, Point goal) {

        Node startNode = level.graph.getNodeFromPosition(start);
        Node endNode = level.graph.getNodeFromPosition(goal);

        // setup for A*
        HashMap<Node,Node> parentMap = new HashMap<Node,Node>();
        HashSet<Node> visited = new HashSet<Node>();
        Map<Node, Integer> distances = initializeAllToInfinity();

        Queue<Node> priorityQueue = initQueue();

        //  enque StartNode, with distance 0
        startNode.setDistanceToStart(0);

        distances.put(startNode, 0);
        priorityQueue.add(startNode);
        Node current = null;

        while (!priorityQueue.isEmpty()) {
            current = priorityQueue.remove();

            if (!visited.contains(current) ){
                visited.add(current);

                // if last element in PQ reached
                if (current.equals(endNode)) {
                    return pathTo(parentMap, endNode, startNode);
                }

                Set<Node> neighbors = current.getAdjacents();
                for (Node neighbor : neighbors) {
                    if (!visited.contains(neighbor) ){

                        // calculate predicted distance to the end node
                        //todo: fix
                        int predictedDistance = endNode.position.x - neighbor.position.x;

                        // 1. calculate distance to neighbor. 2. calculate dist from start node
                        int neighborDistance = neighbor.position.x - current.position.x;

                        //int distanceToStart = current.position.x - new Point(100, 50).x;

                        int totalDistance = current.getDistanceToStart() + neighborDistance + predictedDistance;

                        // check if distance is smaller
                        if(totalDistance < distances.get(neighbor) ){
                            // update n's distance
                            distances.put(neighbor, totalDistance);
                            // used for PriorityQueue
                            neighbor.setDistanceToStart(totalDistance);
                            neighbor.setPredictedDistance(predictedDistance);
                            // set parent
                            parentMap.put(neighbor, current);
                            // enqueue
                            priorityQueue.add(neighbor);
                        }
                    }
                }
            }
        }
        return null;
    }

    private Map<Node, Integer> initializeAllToInfinity() {
        Map<Node, Integer> distances = new HashMap<>();

        Iterator<Node> iter = level.graph.nodes.iterator();
        while (iter.hasNext()) {
            Node node = iter.next();
            distances.put(node, Integer.MAX_VALUE);
        }
        return distances;
    }

    private PriorityQueue<Node> initQueue() {
        return new PriorityQueue<>(10, new Comparator<Node>() {
            public int compare(Node x, Node y) {
                if (x.getDistanceToStart() < y.getDistanceToStart()) {
                    return -1;
                }
                if (x.getDistanceToStart() > y.getDistanceToStart()) {
                    return 1;
                }
                return 0;
            };
        });
    }

    public List<Point> pathTo(HashMap<Node,Node> parentMap, Node endNode, Node startNode) {

        Stack<Point> path = new Stack<>();
        Node currentNode = endNode;

        while(currentNode != startNode) {
            currentNode = parentMap.get(currentNode);
            path.push(currentNode.position);
        }
        return path;
    }
}
