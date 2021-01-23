
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

// Tile.java, a class for the blocks found in the TileGame
public class Tile {

	float x;
	float y;
	int sideLength = 25; // Tiles are squares

	Color color = Color.RED; // default color is red
	Color selectedColor = Color.YELLOW; // the color when the mouse is on the tile

	boolean selected = false;

	String state; // as in state of matter, 3 states are SOLID, AIR, and LIQUID

	Tile() {
	}

	// creates a tile with a top left corner at (xCoord, yCoord) and the default
	// side length
	Tile(float xCoord, float yCoord) {
		x = xCoord;
		y = yCoord;
	}

	// creates a tile of state s with a top left corner at (xCoord, yCoord), a given
	// state, and the default side length
	Tile(float xCoord, float yCoord, String s) {
		x = xCoord;
		y = yCoord;
		state = s;
	}

	// creates a tile of state s with a top left corner at (xCoord, yCoord) and a
	// custom side length(sL)
	Tile(float xCoord, float yCoord, int sL) {
		x = xCoord;
		y = yCoord;
		sideLength = sL;
	}

	// creates a tile with top left corner at (xCoord, yCoord) and a custom side
	// length(sL), color(c), and state(s)
	Tile(float xCoord, float yCoord, int sL, Color c) {
		x = xCoord;
		y = yCoord;
		sideLength = sL;
		color = c;
	}

	// creates a tile with top left corner at (xCoord, yCoord) and a custom side
	// length(sL), color(c), and state(s)
	Tile(float xCoord, float yCoord, int sL, Color c, String s) {
		x = xCoord;
		y = yCoord;
		sideLength = sL;
		color = c;
		state = s;
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

	public float getCenterX() {
		// this method only works if the player is a square( if not, use a width
		// variable instead of sidelength)
		return getX() + (getSideLength() / 2);
	}

	public float getCenterY() {
		// this method only works if the player is a square( if not, use a height
		// variable instead of sidelength)
		return getY() + (getSideLength() / 2);
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

	public Color getSelectedColor() {
		if (getState() == "SOLID") {
			selectedColor = Color.RED;
		}
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Rectangle2D getBounds() {
		// returns a rectangle representing the boundaries of the tile
		return new Rectangle2D(x, y, sideLength, sideLength);
	}

	public Rectangle2D getTopBounds() {
		return new Rectangle2D(getX() + getSideLength() / 4, getY(), getSideLength() / 2, getSideLength() / 8);
	}

	public Rectangle2D getRightBounds() {
		return new Rectangle2D(getX() + (7 * getSideLength()) / 8, getY() + getSideLength() / 8, getSideLength() / 8,
				(6 * getSideLength() / 8));
	}

	public Rectangle2D getLeftBounds() {
		return new Rectangle2D(getX(), getY() + getSideLength() / 8, getSideLength() / 8, (6 * getSideLength() / 8));
	}

	public Rectangle2D getBottomBounds() {
		return new Rectangle2D(getX() + getSideLength() / 4, getY() + (7 * getSideLength() / 8), getSideLength() / 2,
				(getSideLength() / 8) + 0);
		// adding a number to the height gets rid of the constant sinking effect, and
		// also makes it so that all the blocks the player is making contact with
		// are detected (can override this method in the Player class for that)
	}

}
