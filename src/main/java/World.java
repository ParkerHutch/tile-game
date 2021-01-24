import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

// World.java, a class for storing the map for TileGame

public class World {
	
	GameCamera gameCamera;
	int displayWidth;
	int displayHeight;

	int inventoryHeight = 0;
	int sideLength; // side length for each tile

	int numRows;
	int numColumns;

	int minGroundDepth = 20; // the minimum number of ground tiles from the top of the column to the last
								// tile in the column

	int treeTrunkHeight = 4;
	int treeSpacing = 3; // the minimum distance(in columns) between each tree trunk

	// Probability variables that determine how likely(out of 100) a certain block
	// type is to be placed in the ground
	// TODO: Use a hashmap (ex. {"Diamond": 2.5} to store likelihoods
	double coalProbability = 25;
	double ironProbability = 12.5;
	double diamondProbability = 2.5;

	// Variables for map colors along with their default settings
	Map<String, Color> colorMap = new HashMap<String, Color>() {
		{
			put("DIRT", Color.SANDYBROWN);
			put("COAL", Color.DARKSLATEGRAY);
			put("IRON", Color.SILVER);
			put("DIAMOND", Color.DARKBLUE);
			put("WOOD", Color.BROWN);
			put("LEAF", Color.OLIVE);
			put("INVINCIBLE", Color.SADDLEBROWN);
		}
	};
	
	Color skyColor;// = Color.LIGHTBLUE;

	Tile[][] map;

	World() {
	}

	// creates a world object for a given display width and height as well as the
	// side length for each tile and the height of the inventory bar
	World(GameCamera gc, int dispWidth, int dispHeight, int tileSide, int invHeight) {
		gameCamera = gc;
		displayWidth = dispWidth;
		displayHeight = dispHeight;
		sideLength = tileSide;
		inventoryHeight = invHeight;

		// the default values for the number of columns and rows are enough to fill the
		// screen with tiles and a little bit of wiggle room
		numColumns = (displayWidth / sideLength) + 1;
		numRows = (displayHeight / sideLength) + minGroundDepth;
	}

	public void generateMap() {
		// TODO See if this method can be simplified/split into smaller ones
		System.out.println("Creating a world with " + numColumns + " columns and " + numRows + " rows.");

		map = new Tile[numColumns][numRows];

		// set up the tiles and ores
		for (int columnIterator = 0; columnIterator < numColumns; columnIterator++) {

			map[columnIterator] = new Tile[numRows]; // each column in map will have -numRows- number of tiles

			// determine the ground height of the current row
			// NOTE: the Math.sin() is there just to create mountainlike features
			// and should be replaced if a better terrain generation function
			// is implemented
			int localGroundHeight = (int) (Math.sin(columnIterator) * (Math.random() * 3) + minGroundDepth);

			// instantiate the tiles in the column
			for (int rowIterator = 0; rowIterator < map[columnIterator].length; rowIterator++) {

				// set the x/y coordinates of each tile as well as the side length
				// TODO Fix the below code so I don't have to use + sideLength / 8 in the y
				// value
				map[columnIterator][rowIterator] = new Tile(columnIterator * sideLength,
						displayHeight - sideLength - (rowIterator + 1) * sideLength + sideLength / 8, sideLength);

				// give all the blocks at or below the row's local ground height a solid state
				// and appropriate color
				if (rowIterator <= localGroundHeight) {
					map[columnIterator][rowIterator].setType("DIRT");
				} else {
					map[columnIterator][rowIterator].setState("AIR");
					map[columnIterator][rowIterator].setColor(skyColor);
				}

				// top block of the column is a dirt block, all other blocks below it
				// have a chance of being an ore(diamond, coal, etc.)
				if (rowIterator <= localGroundHeight - 1) {
					spawnOre(columnIterator, rowIterator);
				}
			}
			map[columnIterator][0].setType("INVINCIBLE"); // bottom block can't be destroyed by player
		}

		spawnTrees();
	}
	
	public void loopMapColumns(Tile[][] worldMap) {
		// this method loops map elements to the other side of the screen if they have
		// moved out of the player's vision while moving
		// this makes it unnecessary to continually generate the map, and allows the map
		// to be saved
		float maxX;
		float minX;

		for (int i = 0; i < worldMap.length; i++) {

			maxX = worldMap[worldMap.length - 1][0].getX() + worldMap[worldMap.length - 1][0].getSideLength(); // the far right x value for
																							// the next column to be
																							// placed at
			minX = worldMap[0][0].getX(); // the x column of the far left column
			
			
			if (isOutOfLeftBoundary(worldMap[i])) {
				// if a column goes off the left side of the screen(player is moving right), put
				// it on the far right
				for (int j = 0; j < worldMap[i].length; j++) {
					worldMap[i][j].setX(maxX + i * worldMap[i][j].getSideLength());
				}
			}

			if (isOutOfRightBoundary(worldMap[i])) {
				// if the column goes off the right side of the screen and the player is moving
				// left, move it to the left side of the screen
				for (int j = 0; j < worldMap[i].length; j++) {
					worldMap[i][j].setX(minX - (worldMap.length - i) * worldMap[i][j].getSideLength());
				}
			}

		}
	}

	public boolean isOutOfLeftBoundary(Tile[] column) {
		if (column[0].getX() - gameCamera.getxOffset() < -column[0].getSideLength()) {
			return true;
		} else {
			return false;
		}
		
	}

	public boolean isOutOfRightBoundary(Tile[] column) {
		// I changed WIDTH to displayWidth, might be causing an issue TODO Remove this comment
		if (column[0].getX() - gameCamera.getxOffset() > displayWidth) {
			return true;
		} else {
			return false;
		}
	}
	
	public double getHighestY(Tile[] column) {
		// gets the y value for the highest solid block in the given column
		return (double) (column[getTopGroundIndex(column)].getY());
	}

	public int getTopGroundIndex(Tile[] column) {
		// gets the index(row) of the highest solid block in the given column by
		// searching from the top of the bottom to the bottom

		// default return value is -1 so that an error will occur somewhere if this
		// function can't find the correct block
		int index = -1;
		for (int i = column.length - 1; i >= 0; i--) {
			// the first dirt block reached searching from the top of the column downwards
			// will be registered as the top ground block
			if (column[i].getState().equals("SOLID")) {
				index = i;
				break;
			}
		}
		return index;
	}

	public int getIndexDistance(int index1, int index2, int arrayLength) {
		// returns the distance(in indexes) to elements in an array are from each other

		int largerIndex = Math.max(index1, index2);
		int smallerIndex = Math.min(index1, index2);

		// compute simple distance(number line)
		int distance1 = largerIndex - smallerIndex;

		// compute other distance(loop back to beginning of array if index is close to
		// length of array)
		int distance2 = (arrayLength - largerIndex) + smallerIndex + 1;

		return Math.min(distance1, distance2);
	}

	public void spawnTrees() {
		// TODO: Clean up this method
		int lastTreeIndex = 0;
		int randomNum;
		double leftColumnY, middleColumnY, rightColumnY;

		for (int columnIterator = 0; columnIterator < map.length; columnIterator++) {
			// if the highest y value of a column is equal to the 2 columns next to it,
			// there's a 50% chance of a tree being spawned on top of that column
			// TODO: Make sure the tree can't exceed build height?

			// if there's enough space(columns) between this column and the last tree,
			// test if the ground is level for a tree to be spawned
			if (getIndexDistance(lastTreeIndex, columnIterator, map.length) > treeSpacing) {

				// calculate the ground height of the current column and the columns to its left
				// and right
				middleColumnY = getHighestY(map[columnIterator]);
				leftColumnY = getHighestY(map[getValidIndex(columnIterator - 1, map.length)]);
				rightColumnY = getHighestY(map[getValidIndex(columnIterator + 1, map.length)]);

				// check if the ground is level(all 3 columns have same y value for top solid
				// block)
				if (middleColumnY == leftColumnY && middleColumnY == rightColumnY) {
					randomNum = (int) (Math.random() * 2); // will end up as 0 or 1
					if (randomNum == 0) {
						// spawn the tree
						createTree(columnIterator);
						lastTreeIndex = columnIterator;
					}
				}
			}
		}
	}

	public int getValidIndex(int requestedIndex, int arrayLength) {
		// converts a requested index to one that is in the bounds of an array with a
		// given length
		// ex. getValidIndex(-1, 6) returns 5

		int validIndex;
		int finalIndex = arrayLength - 1;
		if (requestedIndex < 0) {
			validIndex = arrayLength + requestedIndex;
		} else if (requestedIndex > finalIndex) {
			validIndex = requestedIndex - arrayLength;
		} else {
			validIndex = requestedIndex;
		}

		return validIndex;
	}

	public void createTree(int columnIndex) {
		// creates a tree at the top of the given column in the map array
		// the tree will have a trunk of height treeTrunkHeight
		int topGroundIndex = getTopGroundIndex(map[columnIndex]);

		// make tree trunk(out of wood)
		for (int i = 0; i < treeTrunkHeight; i++) {
			map[columnIndex][topGroundIndex + 1 + i].setState("SOLID");
			map[columnIndex][topGroundIndex + 1 + i].setType("WOOD");
		}

		// add top leaf
		map[columnIndex][topGroundIndex + treeTrunkHeight + 1].setType("LEAF");

		// add left leaves
		int leftLeafColumnIndex = getValidIndex(columnIndex - 1, map.length);
		map[leftLeafColumnIndex][topGroundIndex + treeTrunkHeight].setType("LEAF");
		map[leftLeafColumnIndex][topGroundIndex + treeTrunkHeight - 1].setType("LEAF");

		// add right leaves
		int rightLeafColumnIndex = getValidIndex(columnIndex + 1, map.length);
		map[rightLeafColumnIndex][topGroundIndex + treeTrunkHeight].setType("LEAF");
		map[rightLeafColumnIndex][topGroundIndex + treeTrunkHeight - 1].setType("LEAF");
	}

	public void spawnOre(int columnIterator, int rowIterator) {
		// spawns an ore at map[columnIterator][rowIterator] if the random number is
		// less than the probability(out of 100) for the ore type

		// the order of the following if statements matters to the ore probabilities
		double randomNum = Math.random() * 100;
		if (randomNum <= coalProbability) {
			map[columnIterator][rowIterator].setType("COAL");
		}
		if (randomNum <= ironProbability) {
			map[columnIterator][rowIterator].setType("IRON");
		}
		if (randomNum <= diamondProbability) {
			map[columnIterator][rowIterator].setType("DIAMOND");
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

	public int getMinGroundDepth() {
		return minGroundDepth;
	}

	public void setMinGroundDepth(int minGroundDepth) {
		this.minGroundDepth = minGroundDepth;
	}

	public Map<String, Color> getColorMap() {
		return colorMap;
	}

	public void setColorMap(Map<String, Color> colorMap) {
		this.colorMap = colorMap;
	}

	public Color getSkyColor() {
		return skyColor;
	}

	public void setSkyColor(Color skyColor) {
		this.skyColor = skyColor;
	}
}
