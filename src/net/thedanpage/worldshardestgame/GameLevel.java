package net.thedanpage.worldshardestgame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

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
		System.out.println("Done initializing game level!");
	}

	public void reset() {
		resetDots();
	}

	public double getDistanceToGoal(Player player) {
		for (Tile t : this.getTileMap()) {

			if (t.getType() == 3) {
				var tileX = t.getBounds().getCenterX();
				var tileY = t.getBounds().getCenterY();

				var playerX = player.getBounds().getCenterX();
				var playerY = player.getBounds().getCenterY();

				return Math.abs(tileX-playerX);
			}
		}
		return -1;
	}
	
	/**
	 * @return spawnPoint
	 */
	public Point getSpawnPoint() {
		return this.spawnPoint;
	}

	public int getLevelNum() { return levelNum; }

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
		try {
			InputStreamReader isr = new InputStreamReader(ClassLoader
					.getSystemResource(
							"net/thedanpage/worldshardestgame/resources/maps/level_"
									+ this.levelNum + ".txt").openStream());
			Scanner scanner = new Scanner(isr);
			String content = scanner.useDelimiter("\\Z").next();
			String[] lines = content.split("\n");
			scanner.close();
			for (int i=19; lines[i] != null; i++) {
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
		} catch (Exception e) {}
	}
}