package net.thedanpage.worldshardestgame;

import net.thedanpage.worldshardestgame.graph.Graph;
import net.thedanpage.worldshardestgame.graph.Node;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameLevel {

	/** Spawn point of the level. */
	private Point spawnPoint;

	private int levelNum;

	/** A list of all of the level's tiles. */
	private ArrayList<Tile> tileMap;

	/** A list of all of the level's dots. */
	public ArrayList<Dot> dots;
	
	/** A list of all of the level's coins. **/
	public ArrayList<Coin> coins;

	public Graph identity;

	public Graph graph;
	
	/** The area of the level, not including background tiles. */
	Area levelArea;
	
	public GameLevel(int levelNumber) {
		this.levelArea = new Area();
		this.tileMap = new ArrayList<Tile>();
		this.dots = new ArrayList<Dot>();
		this.coins = new ArrayList<Coin>();
		this.spawnPoint = new Point(20, 20);
		this.levelNum = levelNumber;

		init();
	}

	public void reset() {
		resetCoins();
	    resetDots();
	}

	public void buildGraph() {
		Graph graph = new Graph();
		List<Node> goalNodes = new ArrayList<>();
		System.out.println("Start building graph");
		var playerSize = 28;
		for (Tile t : this.getTileMap()) {
			if (t.getType() == 1 || t.getType() == 2) {
				for (int x = t.getBounds().x; x < t.getBounds().x + t.getBounds().getWidth(); x++) {
					for (int y = t.getBounds().y; y < t.getBounds().y + t.getBounds().getHeight(); y++) {
						Node node = new Node(new Point(x, y+22));
						graph.addNode(node);
					}
				}
			}
			if (t.getType() == 3) {
				for (int x = t.getX(); x < t.getX() + t.getBounds().getWidth(); x++) {
					for (int y = t.getY(); y < t.getY() + t.getBounds().getHeight(); y++) {
						Node goal = new Node(new Point(x,y+22));
						goalNodes.add(goal);
					}
				}
			}
		}
		for (Node node : graph.nodes) {
			var x = node.position.x;
			var y = node.position.y;
			if(graph.getNodeFromPosition(new Point(x,y-1)) == null) {
				//top
				graph.markNodesAsWallUpDown(node.position, new Point(x, y + (playerSize / 2)));
			}
			if(graph.getNodeFromPosition(new Point(x,y+1)) == null) {
				//bottom
				graph.markNodesAsWallDownUp(node.position, new Point(x, y - (playerSize / 2)));
			}
			if(graph.getNodeFromPosition(new Point(x-1,y)) == null) {
				//left
				graph.markNodesAsWallLeftRight(node.position, new Point(x + (playerSize / 2), y));
			}
			if(graph.getNodeFromPosition(new Point(x+1,y)) == null) {
				//right
				graph.markNodesAsWallRightLeft(node.position, new Point(x - (playerSize / 2), y));
			}
		}
		for(Node node : new ArrayList<>(graph.nodes)) if (node.isWall) graph.removeNode(node);
		for (Node node : graph.nodes) {
			List<Node> adjacentNodes = getAdjacentNodes(node, graph);
			for (Node adjacent : adjacentNodes) {
				node.addEdge(adjacent);
			}
		}
		for (Node goal : goalNodes) {
			if(graph.getNodeFromPosition(new Point(goal.position.x - (playerSize / 2) - 1, goal.position.y)) != null) {
				//goal is right side
				goal.position.x = goal.position.x - (playerSize / 2);
			}
			if(graph.getNodeFromPosition(new Point(goal.position.x + (playerSize / 2) + 1, goal.position.y)) != null) {
				//goal is left side
				goal.position.x = goal.position.x + (playerSize / 2);
			}
			if(graph.getNodeFromPosition(new Point(goal.position.x, goal.position.y - (playerSize / 2) - 1)) != null) {
				//goal is up side
				goal.position.y = goal.position.y - (playerSize / 2);
			}
			if(graph.getNodeFromPosition(new Point(goal.position.x, goal.position.y + (playerSize / 2) + 1)) != null) {
				//goal is down side
				goal.position.y = goal.position.y + (playerSize / 2);
			}
			List<Node> adjacentNodes = getAdjacentNodes(goal, graph);
			for (Node adjacent : adjacentNodes) {
				goal.addEdge(adjacent);
				adjacent.addEdge(goal);
				goal.isGoal = true;
			}
		}
		for (Node goal : goalNodes) {
			if (goal.isGoal) {
				graph.addNode(goal);
			}
		}
		this.graph = graph;
		this.identity = new Graph(graph);
		System.out.println("Finished building graph");
	}

	private List<Node> getAdjacentNodes(Node node, Graph graph) {
		List<Node> nodes = new ArrayList<>();
		var x = node.position.x;
		var y = node.position.y;
		var adj1 = graph.getNodeFromPosition(new Point(x,y+1));
		var adj2 = graph.getNodeFromPosition(new Point(x,y-1));
		var adj3 = graph.getNodeFromPosition(new Point(x+1,y));
		var adj4 = graph.getNodeFromPosition(new Point(x-1,y));
		var adj5 = graph.getNodeFromPosition(new Point(x+1,y+1));
		var adj6 = graph.getNodeFromPosition(new Point(x-1,y-1));
		var adj7 = graph.getNodeFromPosition(new Point(x-1,y+1));
		var adj8 = graph.getNodeFromPosition(new Point(x+1,y-1));
		if (adj1 != null) nodes.add(adj1);
		if (adj2 != null) nodes.add(adj2);
		if (adj3 != null) nodes.add(adj3);
		if (adj4 != null) nodes.add(adj4);
		if (adj5 != null) nodes.add(adj5);
		if (adj6 != null) nodes.add(adj6);
		if (adj7 != null) nodes.add(adj7);
		if (adj8 != null) nodes.add(adj8);
		return nodes;
	}

	public void removeDotsFromGraph() {
		for (Dot dot : dots) {
			var x = (int)dot.getBounds().getX();
			var y = (int)dot.getBounds().getY() + 22;
			var dotSize = dot.getBounds().getWidth();
			var playerSize = 28;

			for (int xPos = x-(playerSize/2); xPos < x+(playerSize/2)+dotSize; xPos++) {
				for(int yPos = y-(playerSize/2); yPos < y+(playerSize/2)+dotSize; yPos++) {
					var node = graph.getNodeFromPosition(new Point(xPos, yPos));
					if (node != null) {
						node.invalidate();
						//graph.removeNode(node);
					}
				}
			}
		}
	}

	public double getDistanceToGoal(Player player) {
		for (Tile t : this.getTileMap()) {

			if (t.getType() == 3) {

				var x1 = t.getBounds().getCenterX();
				var y1 = t.getBounds().getCenterY();

				var x2 = player.getBounds().getCenterX();
				var y2 = player.getBounds().getCenterY();

				return Point2D.distance(x1, y1, x2, y2);
			}
		}
		return -1;
	}

	public void drawGraph(Graphics g) {
		for (Node n : graph.nodes) {
			g.setColor(Color.RED);
			g.fillRect(n.position.x, n.position.y, 1, 1);
		}
	}

	public void updateGraph() {
		this.graph = new Graph(identity);
		removeDotsFromGraph();
		//System.out.println("Before: " + identity.nodes.size() + " After: " + graph.nodes.size());
	}

	public double getDistanceToNextCoin(Player player) {
		for (Coin coin : this.coins) {
			if (!coin.collected) {
				var x1 = coin.getBounds().getCenterX();
				var y1 = coin.getBounds().getCenterY();

				var x2 = player.getBounds().getCenterX();
				var y2 = player.getBounds().getCenterY();

				return Point2D.distance(x1, y1, x2, y2);
			}
		}
		return Double.MAX_VALUE;
	}
	
	/**
	 * @return spawnPoint
	 */
	public Point getSpawnPoint() {
		return this.spawnPoint;
	}

	public ArrayList<Tile> getTileMap() {
		return this.tileMap;
	}

	public void drawTiles(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		
		try {
			/*for (Tile t : this.tileMap) {
				//Background
				if (t.getType() == 0) {
					g.setColor(new Color(180, 181, 254));
					g.fillRect(t.getX(), t.getY(), 40, 40);
				}
			}*/
			
			g.setColor(new Color(180, 181, 254));
			g.fillRect(0, 22, 800, 622);
			
			//Border around level
			g2.setColor(Color.BLACK);
			g2.fill(this.levelArea);
			
			for (Tile t : this.tileMap) {
				
				t.draw(this, g);
				
			}
		} catch (Exception e) {
			System.out.println("File not found.");
		}
	}

	public boolean allCoinsCollected() {
		for(Coin coin : coins) {
			if(!coin.collected) return false;
		}
		return true;
	}

	public void drawDots(Graphics g) {
		for (Dot dot : this.dots) dot.draw(g);
	}

	public void updateDots() {
		if (this.dots != null)
			for (Dot dot : this.dots) dot.update();
	}

	public void resetDots() {
		this.dots = new ArrayList<Dot>();
		try {
			InputStreamReader isr = new InputStreamReader(ClassLoader.getSystemResource("net/thedanpage/worldshardestgame/resources/maps/level_" + this.levelNum + ".txt").openStream());
			Scanner scanner = new Scanner(isr);
			String content = scanner.useDelimiter("\\Z").next();
			String[] lines = content.split("\n");
			scanner.close();
			for (int i = 19; lines[i] != null; i++) {
				String line = lines[i];
				String[] dotData = line.replaceAll(" ", "").split("-");
				this.dots.add(new Dot(
					Integer.parseInt(dotData[0]),
					Integer.parseInt(dotData[1]),
					new Point(Integer.parseInt(dotData[2].split(",")[0]),
							Integer.parseInt(dotData[2].split(",")[1])),
					new Point(Integer.parseInt(dotData[3].split(",")[0]),
							Integer.parseInt(dotData[3].split(",")[1])),
					Double.parseDouble(dotData[4]),
					Boolean.parseBoolean(dotData[5]),
					Boolean.parseBoolean(dotData[6])
				));
			}
		} catch(Exception e){}
	}

	public void resetCoins() {
		this.coins = new ArrayList<Coin>();
		String coinData = null;

		//Retrieves the coin data
		if (PropLoader.loadProperty("coins",
				"net/thedanpage/worldshardestgame/resources/maps/level_"
						+ levelNum + ".properties") != "null") {
			coinData = PropLoader.loadProperty("coins",
					"net/thedanpage/worldshardestgame/resources/maps/level_"
							+ levelNum + ".properties");
		}

		if (coinData != null) {
			coinData = coinData.replaceAll("\\Z", "");

			if (coinData.contains("-")) {

				String[] coins = coinData.split("-");
				for (String s : coins) this.coins.add(new Coin((int) (Double.parseDouble(s.split(",")[0]) * 40),
						(int) (Double.parseDouble(s.split(",")[1]) * 40)));

			} else this.coins.add(new Coin((int) (Double.parseDouble(coinData.split(",")[0]) * 40),
					(int) (Double.parseDouble(coinData.split(",")[1]) * 40)));
		}
	}

	public void drawCoins(Graphics g) {
		if (this.coins != null)
			for (Coin coin : this.coins) coin.draw(g);
	}

	@Override
	public String toString() {
		return "GameLevel [spawnPoint=" + spawnPoint
				+ ", tileMap=" + tileMap
				+ ", dots=" + dots + ", coins=" + coins + ", levelArea="
				+ levelArea + "]";
	}
	
	/**
	 * Load the current level data from
	 * net.thedanpage.worldshardestgame.resources.maps
	 */
	public void init() {

		try {
			this.spawnPoint = new Point(
							Integer.parseInt(PropLoader
									.loadProperty("spawn_point",
											"net/thedanpage/worldshardestgame/resources/maps/level_" + levelNum + ".properties")
									.split(",")[0]) * 40 + 20,
							Integer.parseInt(PropLoader
									.loadProperty("spawn_point",
											"net/thedanpage/worldshardestgame/resources/maps/level_" + levelNum + ".properties")
									.split(",")[1]) * 40 + 20);

			resetCoins();

			//Retrieves the tile data
			InputStreamReader isr = new InputStreamReader(ClassLoader
					.getSystemResource(
							"net/thedanpage/worldshardestgame/resources/maps/level_"
									+ levelNum + ".txt").openStream());
			String content = "";
			Scanner scanner = new Scanner(isr);
			content = scanner.useDelimiter("\\Z").next();
			scanner.close();

			content = content.replaceAll("\n", "");

			for (int i = 0; i < content.length(); i++) {
				if (i > 299)
					break;
				else
					this.tileMap.add(new Tile((i % 20) * 40, (i / 20) * 40,
							Character.getNumericValue(content.charAt(i))));
			}
			this.levelArea = new Area();
			for (Tile t : this.tileMap) {
				if (t.getType() != 0) {
					this.levelArea.add(new Area(
							new Rectangle(t.getX() - 3, t.getY() - 3 + 22, 46, 46)));
				}
			}
		} catch (Exception e) {}
		//Retrieves the dot data
		resetDots();
	}
}