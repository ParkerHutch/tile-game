import java.util.ArrayList;

import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// Player.java, a class for the player object in TileGame
// Parker Hutchinson 2018

public class Player extends Tile {
	final float gravity = 1.0f; // play around with this and the setyVelocity(#) in applyInput to fine tune jumping
	
	float xVelocity = 0;
	float yVelocity = 0;
	
	boolean falling = false;
	boolean jumping = true;
	boolean onGround = false;
	Player() {}

	Player(float xCoord, float yCoord) {
		super(xCoord, yCoord);
	}

	Player(float xCoord, float yCoord, int sL, Color c) {
		super(xCoord, yCoord, sL, c);
	}

	public void tick(String key, ArrayList<Tile> map) {
		applyInput(key);
		//checkGroundBelow(map);
		applyForces();
		resolveCollisions(map);
		
	}

	public void applyInput(String key) {
		// use the currentKey(from the main application) to move the player
		if (key.contains("A")) {
			x -= 5;
		}
		if (key.contains("D")) {
			x += 5;
		}
		if (key.contains("W")) {
			// if the user presses W, make the character jump
			if (!isJumping()) {
				// the player can only jump if they weren't already jumping
				setyVelocity(-10);
				//y-= 0.01f;// remove this and only apply gravity if the player is jumping/falling
				setJumping(true);
			}
		}
		if (key.contains("S")) {
			//y += 5;
		}
	}

	public void applyForces() {
		// apply forces like gravity and velocity
		x += getxVelocity();
		if (isFalling() || isJumping()) {
			yVelocity += gravity;
		}
		y += getyVelocity();
	}

	// I think this method can be deleted
	public void checkGroundBelow(ArrayList<Tile> map) {
		float playerLeftCornerX = getX();
		float playerRightCornerX = getX() + getSideLength();
		for (int i = 0; i < map.size(); i++) {
			// check if either of the player's corners are inside the corners of a tile below it
			if (
					(playerLeftCornerX > map.get(i).getX() && playerLeftCornerX < map.get(i).getX() + map.get(i).getSideLength()) ||
					(playerRightCornerX > map.get(i).getX() && playerRightCornerX < map.get(i).getX() + map.get(i).getSideLength()) ) {
						// after finding the x coordinates of the tile below, check if the player is on top or inside of the tile
						if (getY() + getSideLength() > map.get(i).getY()) {
							setY(map.get(i).getY() - getSideLength());
							map.get(i).setColor(Color.RED);
						}
						else {
							map.get(i).setColor(Color.PURPLE);
						}
					}
			else {
				map.get(i).setColor(Color.PURPLE);
			}
		}
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

	public Rectangle2D getTopBounds() {
		Rectangle2D topBounds = new Rectangle2D (
				//getX() + getSideLength() / 8, getY(), (6 * getSideLength()) / 8, getSideLength() / 8
				getX() + getSideLength() / 4, getY(), getSideLength() / 2, getSideLength() / 8
				); // removing this variable might help with efficiency
		return topBounds;
	}
	public Rectangle2D getRightBounds() {
		Rectangle2D topBounds = new Rectangle2D (
				getX() + (7 * getSideLength()) / 8, getY() + getSideLength() / 8, getSideLength() / 8, (6 * getSideLength() / 8)
				); // removing this variable might help with efficiency
		return topBounds;
	}
	public Rectangle2D getLeftBounds() {
		Rectangle2D topBounds = new Rectangle2D (
				getX(), getY() + getSideLength() / 8, getSideLength() / 8, (6 * getSideLength() / 8)
				); // removing this variable might help with efficiency
		return topBounds;
	}
	public Rectangle2D getBottomBounds() {
		Rectangle2D topBounds = new Rectangle2D (
				getX() + getSideLength() / 4, getY() + (7 * getSideLength() / 8), getSideLength() / 2, getSideLength() / 8
				); // removing this variable might help with efficiency
		return topBounds;
	}
	
	public void resolveCollisions(ArrayList<Tile> map) {
		for (int i = 0; i < map.size(); i++) {
			if (getBottomBounds().intersects(map.get(i).getBounds())) {
				// if the player is on top of(or fell into) a tile
				resolveGroundCollision(map, i);
			}
			if (getRightBounds().intersects(map.get(i).getBounds())) {
				resolveRightCollision(map, i);
			} 
			if (getLeftBounds().intersects(map.get(i).getBounds())) {
				resolveLeftCollision(map, i);
			}

			else {
				setFalling(true);
			}
		}
	}
	
	public void resolveGroundCollision(ArrayList<Tile> map, int mapIndex) {
		setyVelocity(0);; // if the player should bounce, remove this and use yVelocity = -yVelocity
		setFalling(false);
		setJumping(false);
		setOnGround(true);
		setY(map.get(mapIndex).getY() - this.getSideLength());
	}
	
	public void resolveRightCollision(ArrayList<Tile> map, int mapIndex) {
		// set xVelocity = 0?
		setX(map.get(mapIndex).getX() - this.getSideLength());
	}
	
	public void resolveLeftCollision(ArrayList<Tile> map, int mapIndex) {
		// set xVelocity = 0?
		setX(map.get(mapIndex).getX() + map.get(mapIndex).getSideLength()); // should fix this 
	}
}
