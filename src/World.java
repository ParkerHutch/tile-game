import java.util.ArrayList;

import javafx.scene.paint.Color;

// World.java, a class for storing the map for TileGame
// Parker Hutchinson


public class World {
	static int displayWidth;
	static int displayHeight;
	static int inventoryHeight = 150;
	static int sideLength; // side length for each tile
	
	static ArrayList<Tile> map = new ArrayList<Tile>();
	
	World() {}
	
	// creates a world object for a given display width and height as well as the side length for each tile and the height of the inventory bar
	World(int width, int height, int tileSide, int invHeight) {
		displayWidth = width;
		displayHeight = height;
		sideLength = tileSide;
		inventoryHeight = invHeight;
	}
	
	public static void generateMap() {
		int numTiles = displayWidth / sideLength; // the number of tiles needed to fill the screen horizontally
		for (int i = 0; i < numTiles; i++) {
			map.add(i, new Tile(i * sideLength, (displayHeight - inventoryHeight) - sideLength, sideLength, Color.PURPLE));
		}
		for (int i = 0; i < numTiles; i++) {
			// for the row above ground level, place a tile about 50% of the time
			if ((int)(Math.random() * 2) == 1) {
				map.add(new Tile(i * sideLength, (displayHeight - inventoryHeight) - (2 * sideLength), sideLength, Color.PURPLE));
			}
		}
	}
	
	public static ArrayList<Tile> getMap() {
		generateMap();
		return map;
	}
}
