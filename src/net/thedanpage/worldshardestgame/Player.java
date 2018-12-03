package net.thedanpage.worldshardestgame;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public abstract class Player {

	/** The X coordinate of the player. */
	public int x;

	/** The Y coordinate of the player. */
	public int y;

	private int moveFactor = 1;

	/** True if the player has been hit and is not allowed to move. */
	public boolean dead;
	public boolean deadByDot = false;

	/** The opacity of the player. */
	public double opacity;

	public int nextMoveIndex = 0;

	public Player() {
		this.x = 400;
		this.y = 300;
		this.dead = false;
		this.opacity = 255;
	}

	public boolean isDead() {
		return this.dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
		this.opacity = 0;
	}

	public double distanceTo(Player player) {
		var x1 = player.x;
		var y1 = player.y;
		var x2 = this.x;
		var y2 = this.y;
		return Point2D.distance(x1, y1, x2, y2);
	}

	public void draw(Graphics g) {
		g.setColor(new Color(0, 0, 0, (int) opacity));
		g.fillRect(x - 14, y - 14 + 22, 28, 28);
		g.setColor(new Color(255, 0, 0, (int) opacity));
		g.fillRect(x-11, y-11 + 22,
				   22, 22);
	}

	Tile getRelativeTile(GameLevel level, int x1, int y1, int xOff, int yOff) {
		for (Tile t : level.getTileMap()) {
			if (x1/40 + xOff == t.getSnapX() && y1/40 + yOff == t.getSnapY()) {
				return t;
			}
		}
		return null;
	}

	public Point getPosition() {
		return new Point(x, y+22);
	}

	public Tile getTile(GameLevel level) {
		for (Tile t : level.getTileMap()) {
			if (this.x/40 == t.getSnapX() && this.y/40 == t.getSnapY()) {
				return t;
			}
		}
		return null;
	}

	boolean doesIntersect(Rectangle a, Rectangle b) {
		return (a.x + a.width < b.x || a.x > b.x + b.width
				|| a.y + a.height < b.y || a.y > b.y + b.height);
	}

	public Rectangle getBounds() {
		return new Rectangle(this.x - 14, this.y - 14, 28, 28);
	}

	public void respawn(GameLevel level) {
		this.nextMoveIndex = 0;
		this.dead = false;
		this.deadByDot = false;
		this.opacity = 255;
		this.x = level.getSpawnPoint().x;
		this.y = level.getSpawnPoint().y+22;
		if (level.coins != null) {
			for (Coin coin : level.coins) coin.collected = false;
		}
	}

	boolean checkCollisionUp(GameLevel level) {
		if (getRelativeTile(level, this.x - 14, this.y + 24, 0, -1) != null &&
				getRelativeTile(level, this.x - 14, this.y + 24, 0, -1).getType() == 0 ||
				getRelativeTile(level, this.x + 15, this.y + 24, 0, -1) != null &&
						getRelativeTile(level, this.x + 15, this.y + 24, 0, -1).getType() == 0) {
			return true;
		}
		return false;
	}

	boolean checkCollisionDown(GameLevel level) {
		if (getRelativeTile(level, this.x - 14, this.y - 27, 0, 1) != null &&
				getRelativeTile(level, this.x - 14, this.y - 27, 0, 1).getType() == 0 ||
				getRelativeTile(level, this.x + 15, this.y - 27, 0, 1) != null &&
						getRelativeTile(level, this.x + 15, this.y - 27, 0, 1).getType() == 0) {
			return true;
		}
		return false;
	}

	boolean checkCollisionLeft(GameLevel level) {
		if (getRelativeTile(level, this.x + 24, this.y - 15, -1, 0) != null &&
				getRelativeTile(level, this.x + 24, this.y - 15, -1, 0).getType() == 0 ||
				getRelativeTile(level, this.x + 24, this.y + 14, -1, 0) != null &&
						getRelativeTile(level, this.x + 24, this.y + 14, -1, 0).getType() == 0) {
			return true;
		}
		return false;
	}

	boolean checkCollisionRight(GameLevel level) {
		if (getRelativeTile(level, this.x - 27, this.y - 15, 1, 0) != null &&
				getRelativeTile(level, this.x - 27, this.y - 15, 1, 0).getType() == 0 ||
				getRelativeTile(level, this.x - 27, this.y + 15, 1, 0) != null &&
						getRelativeTile(level, this.x - 27, this.y + 15, 1, 0).getType() == 0) {
			return true;
		}
		return false;
	}

	public boolean collidesWith(Shape other) {
		return this.getBounds().getBounds2D().intersects(other.getBounds2D());
	}

	public void moveLeft(GameLevel level) {
		if(!checkCollisionLeft(level)) this.x-=moveFactor;
	}

	public void moveRight(GameLevel level) {
		if(!checkCollisionRight(level)) this.x+=moveFactor;
	}

	public void moveDown(GameLevel level) {
		if(!checkCollisionDown(level)) this.y+=moveFactor;
	}

	public void moveUp(GameLevel level) {
		if(!checkCollisionUp(level)) this.y-=moveFactor;
	}

	public void move(Move move, GameLevel level) {
		switch (move) {
			case LEFT:
				moveLeft(level);
				break;
			case RIGHT:
				moveRight(level);
				break;
			case DOWN:
				moveDown(level);
				break;
			case UP:
				moveUp(level);
				break;
			case LEFTUP:
				moveLeft(level);
				moveUp(level);
				break;
			case LEFTDOWN:
				moveLeft(level);
				moveDown(level);
				break;
			case RIGHTUP:
				moveRight(level);
				moveUp(level);
				break;
			case RIGHTDOWN:
				moveRight(level);
				moveDown(level);
				break;
			case NEUTRAL:
				break;
		}
	}
}
