import java.util.HashMap;
import java.util.Map;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class InventoryBar {

	int xMargin;
	int width;
	int height;
	
	int windowHeight;
	int windowWidth;
	
	int slotsCount;
	
	Group barObjects = new Group(); // Just put this in, might have to incorporate better
	Player player;
	
	Map<String, Integer> inventory = new HashMap<String, Integer>(); // TODO do i really need this instantiation?
	Map<String, Rectangle> icons;// = new HashMap<String, Rectangle>();
	
	int selectedInventoryTileIndex = 0;
	
	Rectangle[] slotRectangles;
	
	TileGameLauncher game;
	
	/**
	 * @return the selectedInventoryTileIndex
	 */
	public int getSelectedInventoryTileIndex() {
		return selectedInventoryTileIndex;
	}


	/**
	 * @param selectedInventoryTileIndex the selectedInventoryTileIndex to set
	 */
	public void setSelectedInventoryTileIndex(int selectedInventoryTileIndex) {
		this.selectedInventoryTileIndex = selectedInventoryTileIndex;
	}



	Text[] textBoxes; 
	String[] inventoryBarTiles = { "DIRT", "DIAMOND", "WOOD", "LEAF", "COAL", "IRON" };
	
	Group group = new Group();
	
	// TODO Can i take out the inventory parameter if player holds it internally?
	public InventoryBar(int xMargin, int width, int height, Map<String, Integer> inventory, Player player, TileGameLauncher game) {
		this.xMargin = xMargin;
		this.width = width;
		this.height = height;
		
		this.slotsCount = inventoryBarTiles.length;
		this.inventory = inventory;
		
		slotRectangles = new Rectangle[slotsCount];
		textBoxes = new Text[slotsCount];
		
		this.player = player;
		
		this.game = game;
		
		generateBlockIcons();
		
		System.out.println(getIcons().get(0));
		
	}
	
	public void update() {
		
		createSlotRectangles();
		drawTileSelector();
		updateQuantityLabels();
		
	}
	
	public void drawTileSelector() {
		
		for (int i = 0; i < getSlotRectangles().length; i++) {
			if (i == getSelectedInventoryTileIndex()) {
				getSlotRectangles()[i].setStroke(Color.YELLOW);
				getSlotRectangles()[i].toFront(); // keeps the rectangle sides from being drawn over by the
				// neighboring slots
			} else {
				getSlotRectangles()[i].setStroke(Color.GRAY);
			}
		}
	}
	
	
	/**
	 * Creates the gray outline boxes for each of the slots in the inventory bar
	 */
	public void createSlotRectangles() {
		
		double slotSideLength = width / getSlotsCount();
		
		for (int i = 0; i < getSlotsCount(); i++) {

			// TODO: Use the slot variables generated here to indicate the selected tile
			// instead of the current system(using inventorySlotSelectionIndicator)
			// ex. add all the rects to an array, highlight the selected rectangle
			// using the array in the game loop
			double x = (xMargin) + (i * slotSideLength);
			
			Rectangle slot = new Rectangle(x, getBaseRectangle().getY(), slotSideLength, height);
			slot.setStroke(Color.GRAY);
			slot.setFill(Color.TRANSPARENT);
			slot.setStrokeWidth(4);
			getBarObjects().getChildren().add(slot);
			getSlotRectangles()[i] = slot;
			
		}
		
	}

	
	/**
	 * Gets the bottom rectangle(aka the rectangle that contains everything in the inventorybar)
	 * @return the container rectangle
	 */
	public Rectangle getBaseRectangle() {
		
		// returns a Rectangle2D object of the inventory bar

		Rectangle baseRectangle = new Rectangle(xMargin, getWindowHeight() - height, width, height);

		// stroke effects, can change these TODO: Make the color of the inventoryBar
		// part of the settings file
		boolean coloredOutside = false;
		if (coloredOutside) {
			baseRectangle.setStrokeType(StrokeType.OUTSIDE);
			baseRectangle.setStroke(Color.BLUE);
			baseRectangle.setStrokeWidth(8);
		}

		return baseRectangle;
		
	}
	
	public void updateQuantityLabels() {
		
		// NOTE: If I get a bunch of errors, check that variables were sent over from the game class correctly:
		// I took variables like width and height of the window from the other class, which may have not been
		// implemented correctly below, causing drawing issues
		
		int slotWidth = width / getSlotsCount();
		double xMargin = 50;

		double yMargin = 20;

		double iconWidth = slotWidth - 2 * xMargin;
		double iconHeight = iconWidth; // icons are squares
		
		// add the tile icons and quantities
		int counter = 0; // TODO Clean up this for loop, remove this temp variable
		for (String tileName : inventoryBarTiles) {
			
			Rectangle icon = getIcons().get(tileName);
			icon.setX(getBaseRectangle().getX() + (counter * slotWidth) + icon.getX());
			
			//getIcons().get(0).setX(200);
			/*
			Rectangle icon = new Rectangle(xMargin, getWindowHeight() - height + yMargin, iconWidth,
					iconHeight);
					*/
			
			//
			String tileQuantity = Integer.toString(player.getInventory().get(tileName));
			
			Text iconTextBox = new Text(icon.getX() + icon.getWidth() / 2 - 8, icon.getY() + icon.getHeight() / 2 + 8,
					tileQuantity);
			
			// icon box attributes
			iconTextBox.setFill(Color.WHITE);

			iconTextBox.setSmooth(true);
			iconTextBox.setFont(Font.font(23));
			iconTextBox.setId(inventoryBarTiles[counter]);

			// add the text box to the text box array
			getTextBoxes()[counter] = iconTextBox;

			getBarObjects().getChildren().add(iconTextBox);
			
			counter++;
			
		}
	}
	
	public void generateBlockIcons() {

		// TODO: Fix the icons so that the margins work correctly
		// (to see the error, try changing the yMargin to >25
		// could probably fix this by either using an actualHeight variable for the
		// window height
		// or making sure the scene actually sizes to the HEIGHT variable for its height
		int slotWidth = width / getSlotsCount();
		
		double xMargin = 50;

		double yMargin = 20;

		double iconWidth = slotWidth - 2 * xMargin;
		double iconHeight = iconWidth; 


		icons = new HashMap<String, Rectangle>() {
			{
				for (int i = 0; i < inventoryBarTiles.length; i++) {

					Rectangle icon = new Rectangle(-200, getWindowHeight() - height + yMargin, iconWidth,
							iconHeight);
					icon.setFill(game.getTileColors().get(inventoryBarTiles[i]));
					put(inventoryBarTiles[i], icon);
					getBarObjects().getChildren().add(icon);


				}

			}
		};
		
		System.out.println("done generating");
				
		
	}
	
	/*
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
				for (int i = 0; i < inventoryBarTiles.length; i++) {
					Rectangle icon = new Rectangle(xMargin, getHeight() - inventoryBarHeight + yMargin, iconWidth,
							iconHeight);
					icon.setFill(tileColors.get(inventoryBarTiles[i]));
					put(inventoryBarTiles[i], icon);
					inventoryBar.getChildren().add(icon);
				}

			}
		};
		return icons;
	}
	*/
	
	
	/**
	 * @return the icons
	 */
	public Map<String, Rectangle> getIcons() {
		return icons;
	}


	/**
	 * @param icons the icons to set
	 */
	public void setIcons(Map<String, Rectangle> icons) {
		this.icons = icons;
	}


	/**
	 * @return the barObjects
	 */
	public Group getBarObjects() {
		return barObjects;
	}


	/**
	 * @param barObjects the barObjects to set
	 */
	public void setBarObjects(Group barObjects) {
		this.barObjects = barObjects;
	}


	public Rectangle[] getSlotRectangles() {
		
		 return slotRectangles;
		
	}

	/**
	 * @return the textBoxes
	 */
	public Text[] getTextBoxes() {
		return textBoxes;
	}


	/**
	 * @param textBoxes the textBoxes to set
	 */
	public void setTextBoxes(Text[] textBoxes) {
		this.textBoxes = textBoxes;
	}


	public int getxMargin() {
		return xMargin;
	}

	public void setxMargin(int xMargin) {
		this.xMargin = xMargin;
	}

	public int getyMargin() {
		return height;
	}

	public void setyMargin(int yMargin) {
		this.height = yMargin;
	}

	public int getSlotsCount() {
		return slotsCount;
	}

	public void setSlots(int slotsCount) {
		this.slotsCount = slotsCount;
	}

	/**
	 * @return the windowHeight
	 */
	public int getWindowHeight() {
		return windowHeight;
	}


	/**
	 * @param windowHeight the windowHeight to set
	 */
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}


	public Map<String, Integer> getInventory() {
		return inventory;
	}

	public void setInventory(Map<String, Integer> inventory) {
		this.inventory = inventory;
	}

	public String[] getInventoryBarTiles() {
		return inventoryBarTiles;
	}



	public void setInventoryBarTiles(String[] inventoryBarTiles) {
		this.inventoryBarTiles = inventoryBarTiles;
	}



	public Group getGroup() {
		return group;
	}



	public void setGroup(Group group) {
		this.group = group;
	}
	
	
	
	
	
	
}
