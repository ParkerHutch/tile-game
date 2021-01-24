// Tile.java, a class for the blocks found in the TileGame
import java.util.Map;

import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

public class Tile {

	float x;
	float y;
	int sideLength = 25; // Tiles are squares, so only 1 side length needed
	World world = new World();
	// Color variables
	Color color = Color.RED; // default color is red
	Color selectedColor = Color.YELLOW; // the color when the mouse is on the tile
	Map<String, Color> colorMap = world.getColorMap();

	boolean selected = false;

	String state; // as in state of matter, 3 states are SOLID, AIR, and LIQUID
	String type; // type of block, including DIAMOND, COAL, etc.

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

	public Map<String, Color> getColorMap() {
		return colorMap;
	}

	public void setColorMap(Map<String, Color> colorMap) {
		this.colorMap = colorMap;
	}

	public Color getColor() {

		if (colorMap.containsKey(getType())) {
			color = colorMap.get(getType());
		}
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

	public String getType() {
		// TODO: Figure out why state is null in some of the blocks(try removing below
		// line to see why)
		if (state != null) {
			if (state.equals("AIR")) {
				type = "AIR";
			}
		}
		return type;
	}

	public void setType(String type) {
		// if the type isn't air or water, set the state to solid
		// since the block is a solid block
		if (!(type.equals("AIR") || type.equals("WATER"))) {
			setState("SOLID");
		}
		this.type = type;
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
