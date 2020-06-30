import javafx.scene.paint.Color;

// World.java, a class for storing the map for TileGame
// Parker Hutchinson

public class World {
	int displayWidth;
	int displayHeight;

	int inventoryHeight = 150;
	int sideLength; // side length for each tile

	int numRows;
	int numColumns;

	int minGroundHeight = 20; // the minimum number of ground tiles from the top of the column to the last
								// tile in the column

	// Variables for map colors along with their default settings
	Color groundColor = Color.BROWN;
	Color skyColor = Color.LIGHTBLUE;

	Tile[][] map;

	World() {
	}

	// creates a world object for a given display width and height as well as the
	// side length for each tile and the height of the inventory bar
	World(int dispWidth, int dispHeight, int tileSide, int invHeight) {
		displayWidth = dispWidth;
		displayHeight = dispHeight;
		sideLength = tileSide;
		inventoryHeight = invHeight;

		// the default values for the number of columns and rows are enough to fill the
		// screen with tiles and a little bit of wiggle room
		numColumns = (displayWidth / sideLength) + 1;
		numRows = (displayHeight / sideLength) + minGroundHeight;
	}

	public void generateMap() {
		System.out.println("Creating a world with " + numColumns + " columns and " + numRows + " rows.");
		
		map = new Tile[numColumns][numRows];
		
		for (int columnIterator = 0; columnIterator < numColumns; columnIterator++) {

			map[columnIterator] = new Tile[numRows]; // each column in map will have -numRows- number of tiles

			// determine the ground height of the current row
			int localGroundHeight = (int) (Math.random() * 3) + minGroundHeight;

			// instantiate the tiles in the column
			for (int rowIterator = 0; rowIterator < map[columnIterator].length; rowIterator++) {

				// set the x/y coordinates of each tile as well as the side length
				map[columnIterator][rowIterator] = new Tile(columnIterator * sideLength,
						(displayHeight - inventoryHeight) - sideLength * (rowIterator + 1), sideLength);

				// give all the blocks at or below the row's local ground height a solid state
				// and appropriate color
				if (rowIterator <= localGroundHeight) {
					map[columnIterator][rowIterator].setState("SOLID");
					map[columnIterator][rowIterator].setColor(groundColor);
				} else {
					map[columnIterator][rowIterator].setState("AIR");
					map[columnIterator][rowIterator].setColor(skyColor);
				}
			}
		}
	}

	public Tile[][] getMap() {
		generateMap();
		return map;
	}

	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		// check the parameter given for the number of rows to create the world with;
		// if it's too small, warn the user
		if (numRows < (displayWidth / sideLength) + 1) {
			System.out.println("NOTE: World will be created with " + numRows + " rows.");
			System.out.println("This may not be enough to fill the screen with tiles vertically.");
		}
		this.numRows = numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public void setNumColumns(int numColumns) {
		// check the parameter given for the number of columns to create the world with;
		// if it's too small, warn the user
		if (numColumns < (displayWidth / sideLength) + 1) {
			System.out.println("NOTE: World will be created with " + numColumns + " columns.");
			System.out.println("This may not be enough to fill the screen with tiles horizontally.");
		}
		this.numColumns = numColumns;
	}

	public int getMinGroundHeight() {
		return minGroundHeight;
	}

	public void setMinGroundHeight(int minGroundHeight) {
		this.minGroundHeight = minGroundHeight;
	}

	public Color getGroundColor() {
		return groundColor;
	}

	public void setGroundColor(Color groundColor) {
		this.groundColor = groundColor;
	}

	public Color getSkyColor() {
		return skyColor;
	}

	public void setSkyColor(Color skyColor) {
		this.skyColor = skyColor;
	}
}
