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

    public Stack<Node> aStarSearch(Point start, List<Point> goals) {

        if(goals.size() == 0) return null;

        var goal = goals.get(0);

        Node startNode = level.graph.getNodeFromPosition(start);
        Node endNode = level.graph.getNodeFromPosition(goal);

        // setup for A*
        HashMap<Node,Node> parentMap = new HashMap<Node,Node>();
        HashSet<Node> visited = new HashSet<Node>();
        Map<Node, Double> distances = initializeAllToInfinity();

        Queue<Node> priorityQueue = initQueue();

        //  enque StartNode, with distance 0
        startNode.setDistanceToStart(0);

        distances.put(startNode, 0.0);
        priorityQueue.add(startNode);
        Node current = null;

        while (!priorityQueue.isEmpty()) {
            current = priorityQueue.remove();

            if (!visited.contains(current) ){
                visited.add(current);

                // if last element in PQ reached
                if (current.equals(endNode)) {
                    return pathTo(parentMap, current, startNode);
                }

                Set<Node> neighbors = current.getAdjacents();
                for (Node neighbor : neighbors) {
                    if (!visited.contains(neighbor) ){

                        // calculate predicted distance to the end node
                        double predictedDistance = Point.distance(neighbor.position.x, neighbor.position.y, endNode.position.x, endNode.position.y);

                        // 1. calculate distance to neighbor. 2. calculate dist from start node
                        double neighborDistance = Point.distance(current.position.x, current.position.y, neighbor.position.x, neighbor.position.y);

                        double totalDistance = current.getDistanceToStart() + neighborDistance + predictedDistance;

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
        goals.remove(0);
        return aStarSearch(start, goals);
    }

    private Map<Node, Double> initializeAllToInfinity() {
        Map<Node, Double> distances = new HashMap<>();

        Iterator<Node> iter = level.graph.nodes.iterator();
        while (iter.hasNext()) {
            Node node = iter.next();
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        return distances;
    }

    public void drawPath(Stack<Node> path) {

    }

    private PriorityQueue<Node> initQueue() {
        return new PriorityQueue<>(10, new Comparator<Node>() {
            public int compare(Node x, Node y) {
                if (x.score() > y.score()) {
                    return 1;
                }
                if (x.score() < y.score()) {
                    return -1;
                }
                return 0;
            };
        });
    }

    public Stack<Node> pathTo(HashMap<Node,Node> parentMap, Node current, Node startNode) {
        Stack<Node> path = new Stack<>();
        path.push(current);

        while(current != startNode) {
            current = parentMap.get(current);
            path.push(current);
        }

        return path;
    }
}
