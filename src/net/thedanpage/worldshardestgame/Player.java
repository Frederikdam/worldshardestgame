package net.thedanpage.worldshardestgame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Random;

public class Player {

	/** The X coordinate of the player. */
	private int x;

	/** The Y coordinate of the player. */
	private int y;

	private int moveFactor = 3;

	/** True if the player is colliding with a tile above them. */
	private boolean collidingUp;

	/** True if the player is colliding with a tile below them. */
	private boolean collidingDown;

	/** True if the player is colliding with a tile to their left. */
	private boolean collidingLeft;

	/** True if the player is colliding with a tile to their right. */
	private boolean collidingRight;

	/** True if the player has been hit and is not allowed to move. */
	private boolean dead;

	/** The opacity of the player. */
	public double opacity;

	private ArrayList<Move> moves;

	public int nextMoveIndex = 0;

	public double fitness = 0;

	private Random rnd = new Random();

	public Player(int moveCount, ArrayList<Move> moves) {
		this(moveCount);
		for(var i = 0; i < moves.size(); i++) {
			this.moves.set(i, moves.get(i));
		}
	}

	public Player() {
		this.x = 400;
		this.y = 300;
		this.collidingUp = false;
		this.collidingDown = false;
		this.collidingLeft = false;
		this.collidingRight = false;
		this.dead = false;
		this.opacity = 255;
	}

	public Player(int moveCount) {
		this.x = 400;
		this.y = 300;
		this.collidingUp = false;
		this.collidingDown = false;
		this.collidingLeft = false;
		this.collidingRight = false;
		this.dead = false;
		this.opacity = 255;

		this.moves = new ArrayList<>();
		initializeRandomMoves(moveCount);
	}

	public void initializeRandomMoves(int moveCount) {
		for(var i = 0; i < moveCount; i++) {
			this.moves.add(getRandomMove());
		}
	}

	public ArrayList<Move> getMoves() {
		return this.moves;
	}
	public void setMoves(ArrayList<Move> moves) {
		this.moves = moves;
	}

	public Move getRandomMove() {
		return Move.values()[this.rnd.nextInt(Move.values().length)];
	}

	public void mutate() {
		for(var i = 0; i < moves.size(); i++) {
			var randomDouble = rnd.nextDouble();
			var mutated = randomDouble <= game.;
			if(mutated) {
				this.moves.set(i, getRandomMove());
			}
		}
	}

	public Move getNextMove() {
		if(nextMoveIndex >= this.moves.size()) {
			this.dead = true;
			return Move.NEUTRAL;
		}
		return this.moves.get(nextMoveIndex++);
	}



	public void draw(Graphics g) {
		g.setColor(new Color(0, 0, 0, (int) opacity));
		g.fillRect(x - 15, y - 15 + 22, 28, 28);
		g.setColor(new Color(255, 0, 0, (int) opacity));
		g.fillRect(x-12, y-12 + 22,
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





	Tile getTile(GameLevel level) {
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
		return new Rectangle(this.x - 15, this.y - 15, 28, 28);
	}

	void respawn(GameLevel level) {
		this.nextMoveIndex = 0;
		this.dead = false;
		this.opacity = 255;
		this.x = level.getSpawnPoint().x;
		this.y = level.getSpawnPoint().y;
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

	boolean collidesWith(Shape other) {
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

	public int getX() {
		return this.x;
	}



	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return (int) this.getBounds().getWidth();
	}



	public int getHeight() {
		return (int) this.getBounds().getHeight();
	}



	public boolean isCollidingLeft() {
		return this.collidingLeft;
	}



	public boolean isCollidingRight() {
		return this.collidingRight;
	}



	public boolean isCollidingUp() {
		return this.collidingUp;
	}



	public boolean isCollidingDown() {
		return this.collidingDown;
	}

	public boolean isDead() {
		return this.dead;
	}



	public void setDead(boolean dead) {
		this.dead = dead;
	}



	public double getOpacity() {
		return this.opacity;
	}


	@Override
	public String toString() {
		return "Player [x=" + x + ", y=" + y + ", collidingUp=" + collidingUp + ", collidingDown="
				+ collidingDown + ", collidingLeft=" + collidingLeft
				+ ", collidingRight=" + collidingRight + ", dead=" + dead + "]";
	}
}
