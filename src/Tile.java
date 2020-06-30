// Tile.java, a class for the blocks found in the TileGame
// Parker Hutchinson
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile {
	
	float x;
	float y;
	int sideLength = 25; // Tiles are squares
	Color color = Color.RED; // default color is red
	
	Tile() {}
	
	// creates a tile with top left corner at (xCoord, yCoord) and the default side length
	Tile(float xCoord, float yCoord) {
		x = xCoord;
		y = yCoord;
	}
	
	// creates a tile with top left corner at (xCoord, yCoord) and a custom side length(sL) and color(c)
	Tile(float xCoord, float yCoord, int sL, Color c) {
		x = xCoord;
		y = yCoord;
		sideLength = sL;
		color = c;
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getSideLength() {
		return sideLength;
	}

	public void setSideLength(int sideLength) {
		this.sideLength = sideLength;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Rectangle2D getBounds() {
		// returns a rectangle representing the boundaries of the tile
		return new Rectangle2D(x, y, sideLength, sideLength);
	}
	
	
	
}
