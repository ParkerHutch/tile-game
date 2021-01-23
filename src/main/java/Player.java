import javafx.scene.paint.Color;

// Player.java, a class for the player object in TileGame

public class Player extends Tile {
	final float gravity = 1.0f; // play around with this and the setyVelocity(#) in applyInput to fine tune
								// jumping

	float xVelocity = 0;
	float yVelocity = 0;

	boolean falling = false;
	boolean jumping = true;
	boolean onGround = false;

	Player() {
	}

	Player(float xCoord, float yCoord) {
		super(xCoord, yCoord);
	}

	Player(float xCoord, float yCoord, int sL, Color c) {
		super(xCoord, yCoord, sL, c);
	}

	public void tick(String key, Tile[][] map) {
		applyInput(key);
		applyForces();
		resolveCollisions(map);
	}

	public void applyInput(String key) {
		// use the currentKey(from the main application) to move the player
		if (key.contains("A")) {
			// pressing A moves the player left
			x -= 5;
		}
		if (key.contains("D")) {
			// pressing D moves the player right
			x += 5;
		}
		if (key.contains("W")) {
			// if the user presses W, make the character jump
			if (!isJumping()) {
				// the player can only jump if they weren't already jumping
				setyVelocity(-20);
				setJumping(true);
			}
		}
	}

	public void applyForces() {
		// apply forces like gravity and velocity
		x += getxVelocity();
		if ((isFalling() || isJumping())) {
			yVelocity += gravity;
		}
		y += getyVelocity(); // put this before gravity?

	}

	public float getxVelocity() {
		return xVelocity;
	}

	public void setxVelocity(float xVelocity) {
		this.xVelocity = xVelocity;
	}

	public float getyVelocity() {
		return yVelocity;
	}

	public void setyVelocity(float yVelocity) {
		this.yVelocity = yVelocity;
	}

	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean falling) {
		this.falling = falling;
	}

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}
	
	public void resolveCollisions(Tile[][] map) {
		// TODO: Clean this up
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j].getState().equals("SOLID")) {
					
					if (getBottomBounds().intersects(map[i][j].getBounds())) {
						// if the player is on top of(or fell into) a tile
						//map[i][j].setColor(Color.BLUE);
						resolveGroundCollision(map, map[i][j]);
						setFalling(false);
						setOnGround(true);
					} else {
						setFalling(true); // if this is removed, the player can hover between blocks
						//setOnGround(true);
						setOnGround(false);
						//map[i][j].setColor(Color.PURPLE);
					}
					
					if (getTopBounds().intersects(map[i][j].getBounds())) {
						setyVelocity(0);
						setY(map[i][j].getY() + map[i][j].getSideLength());
					}
					
					if (getRightBounds().intersects(map[i][j].getBounds())) {
						resolveRightCollision(map, map[i][j]);
					}
					if (getLeftBounds().intersects(map[i][j].getBounds())) {
						resolveLeftCollision(map, map[i][j]);
					}
					
				}
			}
		}
	}

	public void resolveGroundCollision(Tile[][] map, Tile tile) {
		yVelocity = 0;
		setyVelocity(0); // if the player should bounce, remove this and use yVelocity = -yVelocity
		setY(tile.getY() - this.getSideLength() + 1);
		// the +1 in the line above gets rid of the "player sinking" effect, but also makes it so the player is always colliding with a block
		setFalling(false);
		setJumping(false);
		setOnGround(true);
	}
	
	// TODO: make resolveTopCollision method
	
	public void resolveRightCollision(Tile[][] map, Tile tile) {
		// set xVelocity = 0?
		// maybe use if block.getState().equals("WEAPON") -> setxVelocity(block.getxVelocity()
		setX(tile.getX() - this.getSideLength());
	}

	public void resolveLeftCollision(Tile[][] map, Tile tile) {
		// set xVelocity = 0?
		// maybe use if block.getState().equals("WEAPON") -> setxVelocity(block.getxVelocity()
		setX(tile.getX() + tile.getSideLength()); // should fix this
	}
}
