
// TileGameLauncher.java, the main application for TileGame
// Project start date: 10/18/2018
// TileGameV0.5 completed: 11/8/2018

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

/*
 	IDEAS/ TODO

 	IMPORTANT
 		- Max build height
 			- should probably use length of the 2D map array for this
 		- Right now, a row of blocks is created where the inventory bar is
 		- Find a way to loop through the map more efficiently or less
 			- Ex. lots of my methods rely on looking through the entire map, maybe make it so I only need to do it once or twice

 		- Block destruction using left-click, placement using right-click
 			- user should only be able to place blocks in area near them
 				- find a way to get the tile the user is currently on top of(not the tile below)
 			- will probably need to implement a computer coordinate system -> normal system translation function for this
 		- Make player's checkBounds method more efficient(only check tiles in a certain part of the map, etc.)
 		- Make player only able to jump once
 		- World/Map storage and saving
 			- This would make the game really cool, player could feel like they are making progress instead of restarting every time
 		- Add a settings file
 			- Then the player can change the dimensions of the display and just restart the game to apply them
 		- Multiplayer
 			- This seems hard, but would be interesting to learn about + really cool if I got it working
 			- Watch YouTube videos
 		- Make the grid work everywhere
 			- Maybe draw it depending on the player coordinates?
 		- Improve controls
 			- W or space makes player jump(create a jump() method in the Player class)
 			- Enable build mode with a keyboard button and then can use left click to destroy blocks
 		- Make a menu screen where the player can set the window dimensions + view controls
 		- Make a max build height

 	IDEAS

 		- Make a KeyboardHandler class(like the MouseHandler)
 		- Shadows that move with the sun would be cool
 		- Improve cursor image
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
	int WIDTH = 800;
	int HEIGHT = 700;

	// Useful global variables
	Group root;
	Canvas canvas;
	AnimationTimer animator;
	GraphicsContext g2d;
	private static GameCamera gameCamera;

	// Input variables
	String currentKey = "None";
	boolean leftClickPressed = false;
	boolean rightClickPressed = false;
	double mouseX;
	double mouseY;
	MouseHandler mouseHandler = new MouseHandler();

	// Framerate Measuring
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
	World tileWorld = new World(WIDTH, HEIGHT, sideLength, inventoryHeight);
	Tile[][] map;

	// Settings
	boolean gridEnabled = false;

	// Graphics/Colors
	Color skyColor = Color.SKYBLUE;
	Color groundColor = Color.SANDYBROWN;

	// Variables declared for efficiency TODO: Make better name for this section + variables below
	float maxX;
	float minX;

	public static void main(String[] args) {
		launch(args);
	}

	public void init() throws Exception {
		// Set the colors for the game
		tileWorld.setGroundColor(groundColor);
		tileWorld.setSkyColor(skyColor);

		// Set the terrain generation variables

		map = tileWorld.getMap();

		player = new Player(100, HEIGHT - inventoryHeight - 3 * playerSideLength, playerSideLength, Color.RED);
		gameCamera = new GameCamera(this, 0, 0);
	}

	public void start(Stage gameStage) throws Exception {

		setup(gameStage); // this method helps a lot with organization

		animator = new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				// NOTE: Game loop

				// TODO: Further organize the below code into methods
				// UDPATE
				g2d.clearRect(0, 0, WIDTH, HEIGHT); // clear the screen

				player.tick(currentKey, map);
				gameCamera.centerOn(player);

				loopMapElements();

				// RENDER

				// draw the map (TODO: Put this in a method to save some space)
				if (buildModeEnabled) {
					highlightSelectedTile(); // set the tile under the cursor as "selected"
				}

				for (Tile[] column : map) {
					for (Tile t : column) {
						if (!buildModeEnabled) {
							t.setSelected(false); // maybe draw a rectangle around the box instead of using a whole new
													// color(put this in the drawTile method?)
						}
						drawTile(t);
					}
				}

				drawTile(player);

				if (gridEnabled) {
					drawGrid();
				}

				drawInventoryBar();
			}

		};
		animator.start();

		theStage.setScene(gameScene);
		theStage.show();

		// set the custom cursor TODO: Use a try-catch in case the image doesn't load
		//Image image = new Image("cursorImage.png");
		//gameScene.setCursor(new ImageCursor(image, image.getWidth() / 2, image.getHeight() / 2));
	}

	public void loopMapElements() {
		// this method loops map elements to the other side of the screen if they have
		// moved out of the player's vision while moving
		// this makes it unnecessary to continually generate the map, and allows the map
		// to be saved

		for (int i = 0; i < map.length; i++) {

			maxX = map[map.length - 1][0].getX() + map[map.length - 1][0].getSideLength(); // the far right x value for
																							// the next column to be
																							// placed at
			minX = map[0][0].getX(); // the x column of the far left column

			if (isOutOfLeftBoundary(map[i])) {
				// if a column goes off the left side of the screen(player is moving right), put
				// it on the far right
				for (int j = 0; j < map[i].length; j++) {
					map[i][j].setX(maxX + i * map[i][j].getSideLength());
				}
			}

			if (isOutOfRightBoundary(map[i])) {
				// if the column goes off the right side of the screen and the player is moving
				// left, move it to the left side of the screen
				for (int j = 0; j < map[i].length; j++) {
					map[i][j].setX(minX - (map.length - i) * map[i][j].getSideLength());
				}
			}

		}
	}

	public boolean isOutOfLeftBoundary(Tile[] column) {
		if (column[0].getX() - getGameCamera().getxOffset() < -column[0].getSideLength()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOutOfRightBoundary(Tile[] column) {
		if (column[0].getX() - getGameCamera().getxOffset() > WIDTH) {
			return true;
		} else {
			return false;
		}
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

	public void drawInventoryBar() {
		// A temporary solution to the weird window height problem: I made an inventory
		// bar that fills up the bottom of the screen so that I have clean numbers to
		// use when placing tiles.
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
			g2d.strokeLine(0 - getGameCamera().getxOffset(), i * sideLength - getGameCamera().getyOffset(),
					WIDTH - getGameCamera().getxOffset(), i * sideLength - getGameCamera().getyOffset());
		}
		for (int i = 0; i < numVerticalLines; i++) {
			g2d.strokeLine(i * sideLength - getGameCamera().getxOffset(), 0 - getGameCamera().getyOffset(),
					i * sideLength - getGameCamera().getxOffset(), HEIGHT - getGameCamera().getyOffset());
		}
		g2d.restore();
	}

	public void drawTile(Tile tile) {
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

	public void handle(KeyEvent arg0) {
		// handling keyboard input
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
			System.out.println("Mouse coordinates: (" + mouseX + ", " + mouseY + ")");
		}
	}

	public void setup(Stage s) {
		// setup the stage, scene, canvas, etc. for later use
		theStage = s;
		theStage.setTitle("TileGame");

		root = new Group();
		gameScene = new Scene(root, WIDTH, HEIGHT);

		// Create a keyboard node to track keyboard input(it's an invisible box)
		Box keyboard = generateKeyboardNode();
		root.getChildren().add(keyboard);

		// add the mouse event handler
		// the mouse event handler handles the mouse movement and presses(maybe include
		// mouseReleased/click events?)

		gameScene.setOnMouseMoved(mouseHandler);
		gameScene.setOnMouseDragged(mouseHandler);
		gameScene.setOnMousePressed(mouseHandler);
		gameScene.setOnMouseClicked(mouseHandler);
		gameScene.setOnMouseReleased(mouseHandler);

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
		// keyboardNode.requestFocus();
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
		// keyboardNode.setOnMouseEntered(((new MouseHandler())));
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
								if (t.getState().equals("SOLID")) {
									// if the block is solid, destroy it and add it to the inventory
									t.setState("AIR");
									// t.setColor(Color.TRANSPARENT);
									t.setColor(Color.SKYBLUE); // make this a method inside the tile,
									// so that the tile always returns a color based on its type
								}
								else {
									// if the spot selected is blank(air tile), create a tile there
									t.setState("SOLID");
									t.setColor(Color.GREEN);
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