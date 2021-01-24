// TileGameLauncher.java, the main application for TileGame
// Project start date: 10/18/2018

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/*
 	IDEAS/ TODO
 	
 	IMPORTANT
 		- Cursor image works weirdly on Mac, remove
 		- Make an InventoryBar class
 			- Constructor just needs to take in variables for xMargin, yMargin, slotNum, playerInventory HashMap
 			- I can then instantiate this in init() and call an inventoryBar.update() method in the game loop
 				- Would be much cleaner(inventoryBar takes up a lot of space in this file)
 		- Use build____() for method names
 		- See first bug fix: If that fixed performance, does that mean I can simply loop through the inventoryBarTextBoxes
 			array every game loop iteration and set all values to the player.getInventory() values?
 			- Would be simpler + make more sense than how it is now
 		- Take out all StrokeType.INSIDE/OUTSIDEs, might help with performance
 		- Look up performance hacks
 			- Use filled text instead of stroke, people online say better for performance because of rendering differences
 				- Also don't use StrokeTypes
 		- Remove all TODOs/NOTEs, this comment thing
 			- Put this in a txt file
 		- Make a README, format code according to proper conventions
 		- Render a couple columns off the screen to get rid of screen tearing
 		- Wrap images into exe file?
 		- Make inventory show up when player swaps between items, disappear after a certain amount of time(5s?)
 			- Should also implement a keybinding to keep it open
 		- Might want to change HashMap in Tile to a HashTable(HashTable is synchronized)
 		- Make inventory work/ store in file when world is saved
 		- Cave generation
 			- 10% chance of a circle with radius 4 blocks being spawned below ground
 		- Minimum build height(undestroyable bedrock level)
 		- Max build height
 			- should probably use length of the 2D map array for this
 		- Right now, a row of blocks is created where the inventory bar is
 		- Find a way to loop through the map more efficiently or less
 			- Ex. lots of my methods rely on looking through the entire map, maybe make it so I only need to do it once or twice
 		- Player should only be able to jump if they were standing on a block
 		- Block placement by dragging mouse and holding left click(use mouse_pressed event)
 		- Block destruction using left-click, placement using right-click
 			- user should only be able to place blocks in area near them
 				- find a way to get the tile the user is currently on top of(not the tile below)
 			- will probably need to implement a computer coordinate system -> normal system translation function for this
 		- Make player's checkBounds method more efficient(only check tiles in a certain part of the map, etc.)
 		- Make player only able to jump if standing on ground
 		- Make sky higher/ always draw a blue background rectangle
 		- Change cursor based on whether or not build mode is enabled
 		- World/Map storage and saving
 			- This would make the game really cool, player could feel like they are making progress instead of restarting every time
 		- Add a settings file
 			- Then the player can change the dimensions of the display and just restart the game to apply them
 		- A loading screen with a rotating 3D object would be cool
 		- Multiplayer
 			- This seems hard, but would be interesting to learn about + really cool if I got it working
 			- Maybe make it a mode, and if the mode is enabled, create a thread variable for each player?
 			- Watch YouTube videos
 		- Make the grid work everywhere
 			- Maybe draw it depending on the player coordinates?
 		- Improve controls
 			- W or space makes player jump(create a jump() method in the Player class)
 			- Enable build mode with a keyboard button and then can use left click to destroy blocks
 		- Make icon for application
 		- Make a menu screen where the player can set the window dimensions + view controls
 		- Use private variables
 			
 	BUGS/ERRORS TO FIX
 		- Screen tearing/ array looping seems delayed on left side
 			- 2 ways to fix
 				- 1. figure out what is causing the problem and solve it
 					- Try figuring out what column it occurs in(it seems to only happen with 1)
 				- 2. Make array looping happen outside of the player's vision
 					- ex. the screen +- 3 columns
 		- Player can place a block on top of themselves, pushing them up
 		- Don't let window be resized(setResizable(false)?)
 		- Stage/scene width/height seem to not be functioning correctly
 			- See https://stackoverflow.com/questions/20732100/javafx-why-does-stage-setresizablefalse-cause-additional-margins
 				for ideas
 	NOTES/BUGS FIXED
 		- If the player has 28(/certain other amounts) of a certain block type in their inventory, the framerate noticeably decreases
 			- This is not observed when the player has 27 or 29 of the same type
 			- This effect also occurs when the player has 38 or 68 of the same block type
 			- What's causing this?
 				- What's special about 28?
 			- Fixed: setting the textBoxes stroke type to StrokeType.INSIDE was causing this issue at certain numbers
 	IDEAS
 		- Rename game?
 		- TNT block
 		- Use event listeners for inventory bar updating?
 		- Draw a somewhat transparent overlay of the currently selected block instead of yellow overlay
 			- Sort of like Fortnite building
 		- Sapling
 		- Make ores spawn in strains
 			- Maybe increase probability for an ore being spawned if a neighboring block is the same ore
 		- Make diamonds only spawn deep in the ground
 		- Time how long it takes to create world
 		- See if enhanced for loop would help anywhere
 		- Look into using the stage.sizeToScene() method
 		- Music
 		- Make a KeyboardHandler class(like the MouseHandler)
 		- Add a drag-and-drop text file function for loading worlds
 		- Shadows that move with the sun would be cool
 		- Improve cursor image
 		- Make ladder block
 		- GUI
 			- Drop-down menu that the user clicks to open
 			- Should give info about player coordinates(would be useful for exploration)
 			- This would be useful for debugging
 		- A fade-in shade effect would be cool for selecting tiles
 		- Draw rectangle around selected tile, closes in if player holds left button down until block destroyed
 		- Health, hunger, thirst
 		- A bounce effect when the player falls might be cool
 			- Fall damage?
 		- Map file storage(text file, custom format?)
 		- World effects
 			- Sun/Moon + day/night cycle
 			- Clouds
 			- Caves/ Oceans
 			- Water
 		- Combat
 			- Show weapon/item next to player(as if being held by player)
 			- Enemies
 			- Weapons
 				- Bow and arrow
 			- Knockback(from being hit)
 			- Tools
 		- Rename Tile class to Entity?
 		- Lighting(ex. torches)
 			- Glass(light shines through)
 		- Particle effects(ex. lighting and torches)
 		- Crafting
 		- Parkour mode would be cool
 		- Zooming in/out 
 		- Texture system(images loaded instead of colors)
 		- Why doesn't the height work correctly?(try drawing a rectangle at the bottom of the window)
 			- gameScene.getHeight() returns 953 when I use a constructor giving it a height of 1000
 		- Aerial view game(or make a mode to switch between?)
 		- https://www.youtube.com/watch?v=PRamnpPCHKI
 		- Look up "tile based games" -> images
 		
 	BIGGEST GOALS/ NEW CONCEPTS
 		- Multiplayer
 			- https://www.tutorialspoint.com/java/java_networking.htm
 		- Map saving
 		- Particle effects/lighting
 */
public class TileGameLauncher extends Application implements EventHandler<KeyEvent> {

	Scene gameScene;
	Stage theStage;

	// Dimensions TODO: Use doubles to fix weird sizing issue? NOTE I think the sizing issue is actually due to window padding
	int WIDTH = 800;
	int HEIGHT = 720;

	// Useful global variables
	Group root;
	Canvas canvas;
	AnimationTimer animator;
	GraphicsContext g2d;
	private static GameCamera gameCamera;
	
	// Inventory Bar Attribute Variables
	
	int inventoryBarXMargin = 100; // distance from left side of display window to left side of inventory bar, vice
	// versa for right side
	int inventoryBarWidth = getWidth() - (2 * inventoryBarXMargin);
	int inventoryBarHeight = 125;
	
	InventoryBar inventoryBarGood;// = new InventoryBar(inventoryBarXMargin, inventoryBarWidth, inventoryBarHeight, null, player);// TODO Rename
	
	//Group inventoryBar = inventoryBarGood.getGroup(); // new Group(); // TODO: See if group is the right type for this variable // TODO do i need this variable?
	
	//String[] inventoryBarTiles;// = inventoryBarGood.getInventoryBarTiles(); //{ "DIRT", "DIAMOND", "WOOD", "LEAF", "COAL", "IRON" };
	
	//int inventoryBarSlots = inventoryBarGood.getSlotsCount(); //inventoryBarTiles.length;
	
	//int selectedInventoryTileIndex = 0;
	
	//Rectangle[] slotRectangles = inventoryBarGood.getSlotRectangles();
	
	//Text[] inventoryBarTextBoxes = inventoryBarGood.getTextBoxes(); //new Text[inventoryBarSlots]; // this array is used for updating the text values of
	// Group texts = new Group(); // the text boxes
	//int selectedInventoryTileIndex = 0; This should be in the InventoryBar class now
	Rectangle inventoryBarRectangle;
	//Rectangle inventorySlotSelectionIndicator = new Rectangle(); // TODO: Better name

	Map<String, Rectangle> blockIcons;
	
	// Input variables
	String currentKey = "None";
	boolean leftClickPressed = false;
	boolean rightClickPressed = false;
	double mouseX;
	double mouseY;
	MouseHandler mouseHandler = new MouseHandler();
	boolean mouseOnInventoryBar; // TODO remove this if not using
	double scrollDeltaY;

	// Framerate Measuring TODO Remove this
	final long[] frameTimes = new long[100];
	int frameTimeIterator = 0;
	boolean frameTimesArrayFull = false;

	// Tile variables
	int sideLength = 40;

	// Player variables
	Player player;
	int playerSideLength = sideLength - 10;
	boolean buildModeEnabled = true;

	// World variables
	int inventoryHeight = 120; // distance from the top of the inventory bar to the bottom of the display(in
								// theory)
	int minGroundHeight; // the minimum number of tiles in the ground
	World tileWorld;
	Tile[][] map;

	// Settings
	boolean drawInventoryEnabled = false;
	int minGroundDepth = 10;
	int extraColumns = 20; // the number of columns to generate in addition to those required to fill the
	// screen
	int extraRows = 20; // the number of rows to generate in addition to those required to fill the
	// screen
	int numColumns = WIDTH / sideLength + extraColumns;
	int numRows = HEIGHT / sideLength + extraRows;

	// Graphics/Colors TODO: Put these in a settings file
	Color skyColor = Color.SKYBLUE;
	Map<String, Color> tileColors;

	// File I/O variables
	File worldFile;
	File outputFile; // a temporary file
	String worldFileName = "worldFile.txt";
	String outputFileName = "output.txt";
	BufferedReader bufferedReader;
	BufferedWriter bufferedWriter;

	public static void main(String[] args) {
		launch(args);
	}

	public void init() throws Exception {
		// Stuff to do on initialization
		
		setupFileStreams();

		player = new Player(100, HEIGHT - inventoryHeight - 3 * playerSideLength, playerSideLength, Color.RED);
		gameCamera = new GameCamera(this, 0, 0);

		tileWorld = new World(getGameCamera(), WIDTH, HEIGHT, sideLength, inventoryHeight);
		// Set the colors for the game
		tileWorld.setSkyColor(skyColor);

		// Set the terrain generation variables
		tileWorld.setNumColumns(numColumns);
		tileWorld.setNumRows(numRows);
		tileWorld.setMinGroundDepth(minGroundDepth);

		map = tileWorld.getMap();
		tileColors = tileWorld.getColorMap();
		
		inventoryBarGood = new InventoryBar(inventoryBarXMargin, inventoryBarWidth, inventoryBarHeight, null, player, this);
		
		inventoryBarGood.setWindowHeight(HEIGHT);// TODO Put this in the constructor?
		
		//blockIcons = getBlockIcons(); // TODO This should probably go in InventoryBar
		
		//inventoryBarGood.generateBlockIcons();

		
	}

	public void start(Stage gameStage) throws Exception {
		
		setupWindowVariables(gameStage); // this method helps a lot with organization

		animator = new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				// NOTE: Game loop
				// UDPATE
				g2d.clearRect(0, 0, WIDTH, HEIGHT); // clear the screen

				player.tick(currentKey, map);
				gameCamera.centerOn(player);

				tileWorld.loopMapColumns(map);

				if (drawInventoryEnabled) {
					inventoryBarGood.update();
				}
				//updateInventoryBarTextAmounts(); // TODO Better name?

				// RENDER
				
				/*
				 * This part is handled in InventoryBar now (in the drawTileSelector method)
				for (int i = 0; i < inventoryBarGood.getSlotRectangles().length; i++) {
					// if the slot is selected, give its Rectangle a yellow stroke
					if (i == inventoryBarGood.getSelectedInventoryTileIndex()) {
						inventoryBarGood.getSlotRectangles()[i].setStroke(Color.YELLOW);
						inventoryBarGood.getSlotRectangles()[i].toFront(); // keeps the rectangle sides from being drawn over by the
														// neighboring slots
					} else {
						inventoryBarGood.getSlotRectangles()[i].setStroke(Color.GRAY);
					}
				}
				*/

				drawMap();

				drawTile(player);
			}

		};
		animator.start();

		theStage.setScene(gameScene);

		theStage.setResizable(false);
		theStage.show();

	}

	public void stop() {
		System.out.println("Program closed -> close file streams");

		// close file reader/writer (check that they have been declared first)
		System.out.println("File contents: ");
		System.out.println(getFileAsString());

		try {
			// if the reader exists, close it
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			// if the writer exists, close it
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
		} catch (IOException e) {
			System.out.println("Error closing file reader/writer");
		}

		// delete the output file after saving it
		// outputFile.delete();
	}

	public void setupWindowVariables(Stage s) {
		// setup the stage, scene, canvas, etc. for later use
		theStage = s;
		theStage.setTitle("TileGame");

		root = new Group();
		gameScene = new Scene(root, WIDTH, HEIGHT);

		// Create a keyboard node to track keyboard input(it's an invisible box)
		Box keyboard = generateKeyboardNode();
		root.getChildren().add(keyboard);

		// add the mouse event handler
		// the mouse event handler handles the mouse movement and presses
		addMouseHandling(gameScene, mouseHandler);

		// add mouse scroll handling to the gameScene
		gameScene.setOnScroll((ScrollEvent event) -> {
			scrollDeltaY = event.getDeltaY();
			inventoryBarGood.setSelectedInventoryTileIndex(getValidIndex(inventoryBarGood.getSelectedInventoryTileIndex() + (int) scrollDeltaY / 40 , 
					inventoryBarGood.getInventoryBarTiles().length));
			//selectedInventoryTileIndex = getValidIndex(selectedInventoryTileIndex + (int) scrollDeltaY / 40,
					//inventoryBarTiles.length);
			//updateinventorySlotSelectionIndicator(); TODO Remove this
		});

		theStage.setWidth(WIDTH);
		theStage.setHeight(HEIGHT);

		canvas = new Canvas(WIDTH, HEIGHT);

		root.getChildren().add(canvas);
		g2d = canvas.getGraphicsContext2D();

		//inventoryBar = getInventoryBar();
		//root.getChildren().add(inventoryBar);

		root.getChildren().add(inventoryBarGood.getBarObjects());
		
		//Image image = new Image("cursorImage.png");
		//gameScene.setCursor(new ImageCursor(image, image.getWidth() / 2, image.getHeight() / 2));
	}

	public void addMouseHandling(Scene scene, MouseHandler handlerObject) {
		// adds mouseEvent handling to the given scene
		scene.setOnMouseMoved(handlerObject);
		scene.setOnMouseDragged(handlerObject);
		scene.setOnMousePressed(handlerObject);
		scene.setOnMouseClicked(handlerObject);
		scene.setOnMouseReleased(handlerObject);
	}

	public Box generateKeyboardNode() {
		// Create a keyboard node to track keyboard input(it's an invisible box)
		Box keyboardNode = new Box();
		keyboardNode.setFocusTraversable(true);
		keyboardNode.setOnKeyPressed(this);
		keyboardNode.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				StringBuilder modifiableString = new StringBuilder(currentKey);

				String letterPressed = ke.getText().toUpperCase(); // currentKey uses upper case values

				for (int i = 0; i < modifiableString.length(); i++) {
					String currentCharAsString = Character.toString(modifiableString.charAt(i));

					if (currentCharAsString.equals(letterPressed)) {
						modifiableString.deleteCharAt(i);
					}
				}
				currentKey = modifiableString.toString();
			}
		});
		return keyboardNode;
	}

	public void setCurrentKey(String key) {
		if (currentKey.equalsIgnoreCase("None")) {
			currentKey = key; // if no key was pressed, the current key is the one given
		} else if (!(currentKey.contains(key))) {
			// if the currentKey already contained given key, don't keep adding it
			currentKey += key; // if another key is also being pressed, add the new key to currentKey
		}
	}

	public void handle(KeyEvent arg0) {
		// NOTE: handling keyboard input
		// use "current key" method to make the player move more naturally
		// without current key method, keys repeat and player moves too fast(to see
		// example, try holding down a letter while typing)
		if (arg0.getCode() == KeyCode.W) {
			setCurrentKey("W");
		}
		if (arg0.getCode() == KeyCode.A) {
			// If the player presses the 'a' key, rotate left
			setCurrentKey("A");
		}
		if (arg0.getCode() == KeyCode.D) {
			// If the player presses the 'd' key, rotate right
			setCurrentKey("D");
		}
		if (arg0.getCode() == KeyCode.I) {
			// If the player presses the 'i' key, zoom out
		}
		if (arg0.getCode() == KeyCode.N) {
			// If the player presses the 'n' key, zoom out
			inventoryBarGood.setSelectedInventoryTileIndex(inventoryBarGood.getSelectedInventoryTileIndex() + 1);
		}
		if (arg0.getCode() == KeyCode.SPACE) {
			// If the player presses the spacebar,
			// toggle the visibility of the inventory bar
			drawInventoryEnabled = !drawInventoryEnabled;
			inventoryBarGood.getBarObjects().setVisible(drawInventoryEnabled);

		}
		if (arg0.getCode() == KeyCode.T) {
			inventoryBarGood.setSelectedInventoryTileIndex(getValidIndex(inventoryBarGood.getSelectedInventoryTileIndex() + 1, inventoryBarGood.getInventoryBarTiles().length));
			//updateinventorySlotSelectionIndicator(); TODO Remove this
		}
	}

	public void setupFileStreams() {
		// worldFile = new File(worldFileName);
		// maybe put the two below methods into their own method
		buildInputFile(worldFileName);
		createOutputFile(outputFileName);
		try {
			bufferedReader = new BufferedReader(new FileReader(worldFile));
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		}
	}

	public void buildInputFile(String filename) {
		// create an input world txt file with a given filename
		// if the file already exists on the computer,
		// assign it to the worldFile variable.
		// If the file doesn't exist on the computer,
		// create it and assign it to the worldFile
		// variable
		worldFile = new File(filename);
		try {
			if (!worldFile.exists()) {
				// if the file doesn't exist, create it
				System.out.println("Didn't find the input world file, creating a new one");
				worldFile.createNewFile();
			}
		} catch (IOException e) {
			// executes if an error occurs in the creation of the file
			System.out.println("File creation error");
			System.out.println(e.getStackTrace());
		}
	}

	public void createOutputFile(String filename) {
		// the output file is always blank to begin with
		// since we will just write to it and then copy it
		// to whatever text file is being saved
		// and then delete it
		outputFile = new File(filename);
		try {
			// create a new blank output file
			if (outputFile.exists()) {
				outputFile.delete();
			}
			outputFile.createNewFile();
		} catch (IOException e) {
			// executes if an error occurs in the creation of the file
			System.out.println("File creation error");
			System.out.println(e.getStackTrace());
		}
	}

	
	public Map<String, Rectangle> getBlockIcons() {
		// returns a HashMap of block icons(used in the inventory bar)

		// TODO: Fix the icons so that the margins work correctly
		// (to see the error, try changing the yMargin to >25
		// could probably fix this by either using an actualHeight variable for the
		// window height
		// or making sure the scene actually sizes to the HEIGHT variable for its height
		int slotWidth = inventoryBarWidth / inventoryBarGood.getSlotsCount();
		double xMargin = 50;

		double yMargin = 20;

		double iconWidth = slotWidth - 2 * xMargin;
		double iconHeight = iconWidth; // getHeight() - slotHeight - 3 * yMargin;
		Map<String, Rectangle> icons = new HashMap<String, Rectangle>() {
			{
				for (int i = 0; i < inventoryBarGood.getInventoryBarTiles().length; i++) {
					Rectangle icon = new Rectangle(xMargin, getHeight() - inventoryBarHeight + yMargin, iconWidth,
							iconHeight);
					icon.setFill(tileColors.get(inventoryBarGood.getInventoryBarTiles()[i]));
					put(inventoryBarGood.getInventoryBarTiles()[i], icon);
					inventoryBarGood.getBarObjects().getChildren().add(icon);
				}

			}
		};
		return icons;
	}
	

	// TODO This should probably be in the InventoryBar class
	public Group getInventoryBar() {
		// returns a Group object with all the rectangles involved with the inventory
		// bar
		Group bar = new Group();
		// add the base rectangle
		inventoryBarRectangle = createInventoryBarRectangle(inventoryBarXMargin, inventoryBarWidth, inventoryBarHeight);
		bar.getChildren().add(inventoryBarRectangle);

		// can probably take this event handler part out
		// maybe add a "..." slot for expanding the inventory
		bar.setOnMouseEntered(evt -> {
//			if (inventoryBar.isVisible()) {
			if (inventoryBarGood.getBarObjects().isVisible()) {
				mouseOnInventoryBar = true;
			}
		});

		// create the rectangle sub-dividers of the inventory bar and add them to the
		// group
		double slotSideLength = inventoryBarWidth / inventoryBarGood.getSlotsCount();
		for (int i = 0; i < inventoryBarGood.getSlotsCount(); i++) {
			// TODO: Use the slot variables generated here to indicate the selected tile
			// instead of the current system(using inventorySlotSelectionIndicator)
			// ex. add all the rects to an array, highlight the selected rectangle
			// using the array in the game loop
			double x = (inventoryBarXMargin) + (i * slotSideLength);
			Rectangle slot = new Rectangle(x, inventoryBarRectangle.getY(), slotSideLength, inventoryBarHeight);
			slot.setStroke(Color.GRAY);
			slot.setFill(Color.TRANSPARENT);
			slot.setStrokeWidth(4);
			bar.getChildren().add(slot);
			inventoryBarGood.getSlotRectangles()[i] = slot;
		}

		// add the tile quantities
		for (int i = 0; i < inventoryBarGood.getSlotsCount(); i++) {
			Rectangle icon = blockIcons.get(inventoryBarGood.getInventoryBarTiles()[i]);
			icon.setX(inventoryBarRectangle.getX() + (i * slotSideLength) + icon.getX());
			
			bar.getChildren().add(icon);

			String amount = Integer.toString(player.getInventory().get(inventoryBarGood.getInventoryBarTiles()[i]));

			Text tileAmount = new Text(icon.getX() + icon.getWidth() / 2 - 8, icon.getY() + icon.getHeight() / 2 + 8,
					amount);

			// set attributes of the text box
			tileAmount.setFill(Color.WHITE);

			tileAmount.setSmooth(true);
			tileAmount.setFont(Font.font(23));
			tileAmount.setId(inventoryBarGood.getInventoryBarTiles()[i]);

			// add the text box to the text box array
			inventoryBarGood.getTextBoxes()[i] = tileAmount;

			bar.getChildren().add(tileAmount);
		}

		return bar;
	}

	public void updateInventoryBarTextAmounts() {
		// updates the text values of any of the inventory bar text boxes if they are
		// inaccurate compared to the player inventory
		// this is used instead of a constantly using
		// setText(player.getInventory.get(tileType)) because that caused performance
		// issues
		String amount;
		for (int i = 0; i < inventoryBarGood.getTextBoxes().length; i++) {
			// get the amount of the tile type in the player's inventory
			amount = player.getInventory().get(inventoryBarGood.getTextBoxes()[i].getId()).toString();
			// update the text of the text box with the correct amount
			inventoryBarGood.getTextBoxes()[i].setText(amount);
		}
	}
	
	public Rectangle getInventoryBarRectangle() {
		return inventoryBarRectangle;
	}

	public Rectangle createInventoryBarRectangle(double xM, double w, double h) {
		// returns a Rectangle2D object of the inventory bar

		Rectangle bar = new Rectangle(xM, getHeight() - h, w, h);

		// stroke effects, can change these TODO: Make the color of the inventoryBar
		// part of the settings file
		boolean coloredOutside = false;
		if (coloredOutside) {
			bar.setStrokeType(StrokeType.OUTSIDE);
			bar.setStroke(Color.BLUE);
			bar.setStrokeWidth(8);
		}

		return bar;
	}

	public void drawMap() {
		if (buildModeEnabled) {
			highlightSelectedTile(); // set the tile under the cursor as "selected"
		}

		for (Tile[] column : map) {
			for (Tile t : column) {
				if (!buildModeEnabled) {
					t.setSelected(false);
				}
				drawTile(t);
			}
		}
	}

	public void drawTile(Tile tile) {
		// TODO: Make it so this draws an image if they are loaded
		g2d.save();

		// draw the tile
		g2d.setFill(tile.getColor());
		g2d.fillRect(tile.getX() - getGameCamera().getxOffset(), tile.getY() - getGameCamera().getyOffset(),
				tile.getSideLength(), tile.getSideLength());

		// if the tile is selected, draw a somewhat transparent rectangle overlayed on
		// the tile
		if (tile.isSelected()) {
			g2d.setFill(tile.getSelectedColor());
			g2d.setGlobalAlpha(0.5);
			g2d.fillRect(tile.getX() - getGameCamera().getxOffset(), tile.getY() - getGameCamera().getyOffset(),
					tile.getSideLength(), tile.getSideLength());
		}

		g2d.restore();
	}

	public void highlightSelectedTile() {
		// if build mode isn't enabled, make sure all the tiles have their old color
		for (Tile[] column : map) {
			if (column[0].getX() < mouseX && column[0].getX() + column[0].getSideLength() > mouseX) {
				for (Tile t : column) {
					if ((t.getY() < mouseY && t.getY() + t.getSideLength() > mouseY) && buildModeEnabled) {
						t.setSelected(true);
					} else {
						t.setSelected(false);
					}
				}
			} else {
				for (Tile t : column) {
					t.setSelected(false);
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

	public String getFileAsString() {
		String fileContent = "";
		try {
			StringBuilder sb = new StringBuilder();
			String line = bufferedReader.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = bufferedReader.readLine();
			}
			fileContent = sb.toString();
		} catch (IOException e) {
			System.out.println(e);
		}
		return fileContent;
	}

	public String getHeldTileType() {
		// returns the currently selected tile of the inventoryBar
		return inventoryBarGood.getInventoryBarTiles()[inventoryBarGood.getSelectedInventoryTileIndex()];
	}

	public double getFrameRate(long currentNanoTime) {
		// TODO Remove this
		long oldFrameTime, elapsedNanoSecondsPerFrame;
		double frameRate = 0;

		oldFrameTime = frameTimes[frameTimeIterator];
		frameTimes[frameTimeIterator] = currentNanoTime;
		frameTimeIterator = (frameTimeIterator + 1) % frameTimes.length;
		// once frameTimeIterator == the length of the frameTimes array, ^ will return 0
		if (frameTimeIterator == 0) {
			frameTimesArrayFull = true;
		}
		if (frameTimesArrayFull) {
			elapsedNanoSecondsPerFrame = (currentNanoTime - oldFrameTime) / frameTimes.length;
			// convert nanoseconds to seconds
			frameRate = 1_000_000_000.0 / elapsedNanoSecondsPerFrame;
		}
		return frameRate;
	}

	public Map<String, Color> getTileColors() {
		return tileColors;
	}

	public void setTileColors(Map<String, Color> tileColors) {
		this.tileColors = tileColors;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public GameCamera getGameCamera() {
		return gameCamera;
	}

	class MouseHandler implements EventHandler<MouseEvent> {
		// A class for handling mouse input

		MouseHandler() {
		}

		public void handle(MouseEvent arg0) {
			mouseX = arg0.getX() + getGameCamera().getxOffset();
			mouseY = arg0.getY() + getGameCamera().getyOffset();
			if (arg0.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
				
				if (arg0.getButton() == MouseButton.PRIMARY) {
					// if the left click button is pressed
					for (Tile[] column : map) {
						for (Tile t : column) {
							if (t.isSelected()) {
								// TODO: Put the below code in a "destroy" method in tile
								// The method shouldn't destroy the block if it is invincible
								// so maybe call it "attemptDestroy"?
								if (t.getState().equals("SOLID") && !(t.getType().equals("INVINCIBLE"))) {
									// if the block is solid, destroy it and add it to the inventory

									// add the block to the player's inventory
									player.getInventory().put(t.getType(), player.getInventory().get(t.getType()) + 1);
									t.setState("AIR");
									// t.setColor(Color.TRANSPARENT);
									t.setColor(Color.SKYBLUE); // make this a method inside the tile,
									// so that the tile always returns a color based on its type
								} else if (!t.getType().equals("INVINCIBLE")
										&& (player.getInventory().get(getHeldTileType()) > 0)) {
									// if the spot selected isn't solid or invincible(probably air tile), create a
									// tile there and remove 1 of it from the inventory

									player.getInventory().put(getHeldTileType(),
											player.getInventory().get(getHeldTileType()) - 1);
									t.setType(getHeldTileType());
								}
							}
						}
					}
				}
				if (arg0.getButton() == MouseButton.SECONDARY) {
					// if the right click button is pressed
					buildModeEnabled = !buildModeEnabled;
				}

				// leftClickPressed = arg0.isPrimaryButtonDown();
				// rightClickPressed = arg0.isSecondaryButtonDown();
			}
		}

	}
}