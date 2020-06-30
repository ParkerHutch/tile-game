// TileGameLauncher.java, the main application for TileGame
// Parker Hutchinson 2018
// Project start date: 10/18/2018
// TileGameV1 completed 10/24/2018

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

/*
 	IDEAS/ TODO

 	IMPORTANT
 		- Big idea for map looping/placement:
 			Adding tiles to the arrayList works for now, but if I try to loop the map it'll get confusing(would be trying to loop tiles based on their x position)
 			It'll be easier if I can just move a whole column from the beginning of the arrayList to the end or vice versa
 			So, I can create a big two dimensional array/arrayList that stores a sub array(/List) for each column
 			That would make it easier to cycle through the map, and I can do all the map generation required before runtime(save memory)
 				- Note: arrays are more efficient than arrayLists, and since I can determine a world "size"(in this case, how many columns the world
 					will have) before running I could make use of them
 				- This might mean I need to redo the player's isOnTile method, not sure


 		- Tile classifications(ex. ground, air, water(player sinks through), dirt, metal...)
 		- Player class
 			- gravity, keyboard input, health
 		- Game camera
 		- Make map loop by cycling arraylist positions(no continuous generation)
 		- Block destruction using left-click, placement using right-click
 			- user should only be able to place blocks in area near them
 				- find a way to get the tile the user is currently on top of(not the tile below)
 			- will probably need to implement a computer coordinate system -> normal system translation function for this
 		- Make player's checkBounds method more efficient(only check tiles in a certain part of the map, etc.)
 		- World/Map storage and saving
 			- This would make the game really cool, player could feel like they are making progress instead of restarting every time
 		- Multiplayer
 			- This seems hard, but would be interesting to learn about + really cool if I got it working
 			- Watch YouTube videos


 	IDEAS
 		- GUI
 			- Drop-down menu that the user clicks to open
 			- Should give info about player coordinates(would be useful for exploration)
 			- This would be useful for debugging
 		- A fade-in shade effect would be cool for selecting tiles
 		- Health, hunger, thirst
 		- A bounce effect when the player falls might be cool
 			- Fall damage?
 		- Map file storage(text file, custom format?)
 		- Sun/Moon (day and night cycle)
 		- Sky
 		- Caves
 		- Bow and arrow
 		- Lighting(ex. torches)
 		- Particle effects(ex. lighting and torches)
 		- Enemies
 		- Crafting
 		- Zooming in/out
 		- Texture system(images loaded instead of colors)
 		- Why doesn't the height work correctly?(try drawing a rectangle at the bottom of the window)
 			- gameScene.getHeight() returns 953 when I use a constructor giving it a height of 1000
 		- Aerial view game(or make a mode to switch between?)
 		- https://www.youtube.com/watch?v=PRamnpPCHKI
 		- Look up "tile based games" -> images

 	BIGGEST GOALS/ NEW CONCEPTS
 		- Multiplayer
 		- Map saving
 		- Particle effects/lighting
 */
public class TileGameLauncher extends Application implements EventHandler<KeyEvent> {
	Scene gameScene;
	Stage theStage;

	// Dimensions
	int WIDTH = 1000;
	int HEIGHT = 1000;

	// Useful global variables
	Group root;
	Canvas canvas;

	AnimationTimer animator;

	GraphicsContext g2d;

	// currentKey, used for keyboard input
	String currentKey = "None";

	// Framerate Measuring
	final long[] frameTimes = new long[100];
	int frameTimeIterator = 0;
	boolean frameTimesArrayFull = false;

	// Tile variables
	int sideLength = 40;

	// Player variables
	Player player;
	int playerSideLength = sideLength + 10;

	// World variables
	int inventoryHeight = 120; // distance from the top of the inventory bar to the bottom of the display(in theory)
	World tileWorld = new World(WIDTH, HEIGHT, sideLength, inventoryHeight);
	ArrayList<Tile> map;

	// Settings
	boolean gridEnabled = true;

	public static void main(String[] args) {
		launch(args);
	}

	public void init() throws Exception {
		map = World.getMap();
		player = new Player(100, HEIGHT - inventoryHeight - 3 * playerSideLength, playerSideLength, Color.RED);
	}

	public void start(Stage gameStage) throws Exception {

		setup(gameStage); // this method helps a lot with organization

		animator = new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				// NOTE: Game loop

				g2d.clearRect(0, 0, WIDTH, HEIGHT); // clear the screen

				// UDPATE
				player.tick(currentKey, map); // put this in a player.tick() method
				// RENDER

				for (Tile t : map) {
					drawTile(t);
				}

				drawTile(player);
				if (gridEnabled) {
					drawGrid();
				}
				drawInventoryBar();

				simpleDrawRect2D(player.getTopBounds());
				simpleDrawRect2D(player.getRightBounds());
				simpleDrawRect2D(player.getLeftBounds());
				simpleDrawRect2D(player.getBottomBounds());

			}
		};
		animator.start();

		theStage.setScene(gameScene);
		theStage.show();
	}

	// this method should be removed once project is completed, just makes rectangle drawing simpler
	public void simpleDrawRect2D(Rectangle2D rect) {
		g2d.strokeRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
	}

	public void drawInventoryBar() {
		// A temporary solution to the weird window height problem: I made an inventory bar that fills up the
		// bottom of the screen so that I have clean numbers to use when placing tiles.
		g2d.setFill(Color.BLACK);
		g2d.fillRect(0, HEIGHT - inventoryHeight, WIDTH, HEIGHT);
		g2d.setFill(Color.WHITE);
		g2d.fillText("Inventory", WIDTH / 2 - 25, HEIGHT - 80);
	}

	public void drawGrid() {
		g2d.save();
		g2d.setStroke(Color.STEELBLUE);

		int numHorizontalLines = WIDTH / sideLength;
		int numVerticalLines = HEIGHT / sideLength;

		for (int i = 0; i < numHorizontalLines; i++) {
			g2d.strokeLine(0, i * sideLength, WIDTH, i * sideLength);
		}
		for (int i = 0; i < numVerticalLines; i++) {
			g2d.strokeLine(i * sideLength, 0, i * sideLength, HEIGHT);
		}
		g2d.restore();
	}

	public void drawTile(Tile tile) {
		g2d.save();
		g2d.setFill(tile.getColor());
		g2d.fillRect(tile.getX(), tile.getY(), tile.getSideLength(), tile.getSideLength());
		g2d.restore();
	}

	public void handle(KeyEvent arg0) {
		// handling keyboard input
		// use "current key" method to make the player move more naturally
		// without current key method, keys repeat and player moves too fast(to see example, try holding down a letter while typing)
		if (arg0.getCode() == KeyCode.W) {
			setCurrentKey("W");
		}
		if (arg0.getCode() == KeyCode.A) {
			// If the player presses the 'a' key, rotate left
			setCurrentKey("A");
		}
		if (arg0.getCode() == KeyCode.S) {
			setCurrentKey("S");
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
		}
		if (arg0.getCode() == KeyCode.SPACE) {
			// space toggles the grid on the screen
			gridEnabled = !gridEnabled;
		}
	}

	public void setup(Stage s) {
		// setup the stage, scene, canvas, etc. for later use
		theStage = s;
		theStage.setTitle("TileGame");

		root = new Group();
		gameScene = new Scene(root, WIDTH, 200);

		// Create a keyboard node to track keyboard input(it's an invisible box)
		Box keyboard = generateKeyboardNode();
		root.getChildren().add(keyboard);

		theStage.setWidth(WIDTH);
		theStage.setHeight(HEIGHT);

		canvas = new Canvas(WIDTH, HEIGHT);

		root.getChildren().add(canvas);
		g2d = canvas.getGraphicsContext2D();

	}

	public Box generateKeyboardNode() {
		// Create a keyboard node to track keyboard input(it's an invisible box)
		Box keyboardNode = new Box();
		keyboardNode.setFocusTraversable(true);
		keyboardNode.requestFocus();
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

	public double getFrameRate(long currentNanoTime) {
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
			// long elapsedNanoSecondsPerFrame = elapsedNanos / frameTimes.length;
			// convert nanoseconds to seconds
			frameRate = 1_000_000_000.0 / elapsedNanoSecondsPerFrame;
		}
		return frameRate;
	}

	public int getWindowWidth() {
		return WIDTH;
	}

	public int getWindowHeight() {
		return HEIGHT;
	}

}