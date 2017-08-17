/*
 * GridUI.java
 * Author:	Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	Used as a container for Tile objects.
 * 			Primarily used to retrieve a specific Tile, or perform mass operations on all of the Tiles.
 * 			Also contains the function for saving.
 */

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GridUI extends JPanel {

	//Tile object matrix
	private Tile[][] tiles;
	
	//Holds the number of rows and columns as defined by the constructor
	private int rows, cols;
	
	//Holds the width of the tiles
	//32 for the regular grid, and 64 for the tile selector
	private int tileWidth;
	
	//The path to export the grid to (for the save functionality)
	private String savePath;
	
	//Type of grid to be created
	private String type;
	
	//Allows for String messages to be intercepted from Tiles and Interpreted by the MainFrame
	private StringListener gridListener;
	private StringListener sl;
	private MouseMotionListener mo;
	
	public GridUI(int rows, int cols, final String type, BufferedImage[][][] images, int layers, int trigger) 
	/*
	 * Constructor for a GridUI
	 * Parameters:
	 * 		rows -- Number of rows the grid will have
	 * 		cols --	Number of columns the grid will have
	 * 		type --	Whether the grid is regular, or tile chooser
	 * 		images -- Only used for load functionality, under the assumption that a new grid will be created when loading
	 */
	{
		//assign parameters to object data
		this.rows = rows;
		this.cols = cols;
		this.type = type;
		
		//create StringListener for transmitting Tile events
		sl = new StringListener(){

			@Override
			public void textEmitted(String text) {
				gridListener.textEmitted(type + "," + text);
			}
			
		};
		
		mo = new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				gridListener.textEmitted("UPDATEIMAGE");
			}
			
		};
		
		//Create and implement a new mouse listener to wait for user clicks
		MouseListener mouseListener = new MouseListener() {
	
			@Override
			public void mouseClicked(MouseEvent e) {
				//Currently do nothing
			}
	
			@Override
			public void mouseEntered(MouseEvent e) {
				handleMouseEnter(e);
			}
	
			@Override
			public void mouseExited(MouseEvent e) {
				handleMouseExit(e);
			}
	
			@Override
			public void mousePressed(MouseEvent e) {
				handleMousePressed(e);
			}
	
			@Override
			public void mouseReleased(MouseEvent e) {
				//Should create the preview tiles if released
				System.out.println("You just released the mouse!");
				handleMouseRelease(e);
			}
			
		};
				
		//Create and implement a new mouse wheel listener to wait for mouse wheel movement
		MouseWheelListener mouseWheelListener = new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				handleMouseWheelMoved(e);
			}
			
		};
		
		//Add mouseListener to the Tile
		addMouseListener(mouseListener);
				
		//Add mouse wheel listener to the Tile
		addMouseWheelListener(mouseWheelListener);
		
		addMouseMotionListener(mo);
		
		if (trigger == 1) { 
			this.tileWidth = 32;
			
			setLayout(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			
			gc.weightx = 1;
			gc.weighty = 1;
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.CENTER;
			
			tiles = new Tile[rows][cols];
			
			System.out.println("Rows are: " + rows + " and columns are: " + cols);
			
			loadLevelGrid(gc, images, layers);
			
		} else if (type == "regular") {
			this.tileWidth = 32;
			
			setLayout(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			
			gc.weightx = 1;
			gc.weighty = 1;
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.CENTER;
			
			//initialize tiles
			tiles = new Tile[rows][cols];
			
			//build the grid
			buildLevelGrid(gc);
			
		}  else if (type == "mini") {
			this.tileWidth = 64;
			
			setLayout(new GridLayout(rows, cols));
			
			//initialize tiles
			tiles = new Tile[rows][cols];
			
			//build the grid
			buildTileChooserGrid();
			
		}
	}
	
	private void buildTileChooserGrid()
	/*
	 * Adds all tiles to the level builder grid using Grid Layout
	 * Post: Grid has been created and added to the JPanel layout
	 * TODO: Change this to work with GridBagLayout
	 */
	{
		//for each matrix position, create a new tile of the appropriate tile width, location, and type
		//then add the Tile to the layout and set a StringListener to it
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				tiles[i][j] = new Tile(this.tileWidth, i, j, type);
				//add(tiles[i][j]);
				tiles[i][j].setStringListener(sl);
			}
		}
	}
	
	private void buildLevelGrid(GridBagConstraints gc)
	/*
	 * Adds all tiles to the level designer grid using a predefined GridBagConstraints and using the GridBagLayout
	 * POST: Grid has been created and added to the JPanel Layout
	 */
	{		
		//for each matrix position, create a new tile of the appropriate tile width, location, and type
		//then add the Tile to the layout and set a StringListener to it
		for (int i = 0; i < rows; i++) {
			gc.gridx = i;
			for (int j = 0; j < cols; j++) {
				gc.gridy = j;
				tiles[i][j] = new Tile(this.tileWidth, i, j, type);
				
				//add(tiles[i][j], gc);
				tiles[i][j].setStringListener(sl);
				//tiles[i][j].addMouseMotionListener(mo);
			}
		}
	}
	
	private void loadLevelGrid(GridBagConstraints gc, BufferedImage[][][] imgs, int layers)
	/*
	 * 
	 */
	{
		
		System.out.println("loading the level grid");
		
		for (int i = 0; i < rows; i++) {
			System.out.println("     on row: " + i);
			gc.gridx = i;
			for (int j = 0; j < cols; j++) {
				System.out.println("          on col " + j);
				gc.gridy = j;
				tiles[i][j] = new Tile(this.tileWidth, i, j, type);
				
				//add(tiles[i][j], gc);
				tiles[i][j].setStringListener(sl);
				//tiles[i][j].addMouseMotionListener(mo);
			}
		}
		
		System.out.println("Adding load level images.");
		
		for (int k = 0; k < layers + 1; k++) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					tiles[i][j].setImage(imgs[k][i][j], k);
				}
			}
		}
	
		System.out.println("Finished loading the level grid");
	}

	public void setStringListener(StringListener listener) 
	/*
	 * Sets the GridUI string listener
	 */
	{
		this.gridListener = listener;
	}
	
	public Tile getTile(int i, int j)
	/*
	 * Retrieves a tile at a certain position
	 */
	{
		return this.tiles[i][j];
	}
	
	public void setSavePath(String path) 
	/*
	 * Sets the save path
	 */
	{		
		this.savePath = path;
	}
	
	public void exportGrid(int numLayers)
	/*
	 * This is the save function which will combine all of the tiles into one giant PNG bitmap image
	 * TODO: Create an XML data storage file as part of the save file, might even be able to combine the two?
	 */
	{
		BufferedImage result = new BufferedImage(cols * 32, rows * 32, BufferedImage.TYPE_INT_ARGB);
		Graphics g = result.getGraphics();
		
		//first make sure the save path does not yet have an extension
		String path = this.savePath;
		if (path.endsWith(".png"))
			path = path.replace(".png", "");
		
		for (int i = 0; i <= numLayers; i++) {
			result = new BufferedImage(cols * 32, rows * 32, BufferedImage.TYPE_INT_ARGB);
			g = result.getGraphics();
			drawLayer(g, i);
			
			//write to file
			try {
				ImageIO.write(result, "png", new File(path + "_" + i + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
	}
	
	public void exportGridAsImage(int numLayers)
	{
		BufferedImage tempResult;
		Graphics g;
		
		BufferedImage mainResult = new BufferedImage(cols * 32, rows * 32, BufferedImage.TYPE_INT_ARGB);
		Graphics gMain = mainResult.getGraphics();
		
		//first make sure the save path does not yet have an extension
		String path = this.savePath;
		if (path.endsWith(".png"))
			path = path.replace(".png", "");
		
		for (int i = 0; i <= numLayers; i++) {
			tempResult = new BufferedImage(cols * 32, rows * 32, BufferedImage.TYPE_INT_ARGB);
			g = tempResult.getGraphics();
			drawLayer(g, i);
			
			//Draw layer to main image
			gMain.drawImage(tempResult, 0, 0, null);
			
		}
		
		//write to file
		try {
			ImageIO.write(mainResult, "png", new File(path + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private void drawLayer(Graphics g, int layer)
	/*
	 * Helped function for drawing each layer to a BufferedImage
	 */
	{
		BufferedImage temp;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				temp = tiles[i][j].getImage(layer);
				g.drawImage(temp, i * this.tileWidth, j * this.tileWidth, null);
			}
		}
	}
	
	public void setAllUnselected()
	/*
	 * At the moment just loops through all of the tiles
	 * TODO: Eventually, it should only loop through the hashmap of selected tiles
	 */
	{
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++)
				tiles[i][j].setSelected(false);
		}
	}
	
	public void toggleGrid(boolean flag)
	{
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				tiles[i][j].setShowGrid(flag);
			}
		}
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	/*
	 * Paints the Tile
	 */
	{
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;

	    g2.drawImage(image, imageX, imageY, this);
		
		//If there is an image set, then paint
		if (image != null) {
			
			//For each Tile layer
			for (int i = 0; i < image.length; i++) {
				g.drawImage(image[i], 0, 0, this);
			}
		} 
		
		if (previewLayer != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2d.drawImage(previewLayer, 0, 0, this);
		}
		
		//If the tile is selected, overlay it with a transparent blue Rectangle
		if (selected) {
			g.setColor(new Color(135, 206, 235, 225));
			g.fillRect(0, 0, width, width);
		}
		
		//Draw a black Rectangle grid square over the tile
		//This should probably be changed to be lines, because some of the lines are getting cut off around the edges
		if (showGrid) {
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, width, width);
		}
	}

	private void handleMouseExit(MouseEvent e)
	/*
	 * Triggered when mouse leaves the tile
	 */
	{
		if (type == "regular") {
		sl.textEmitted(type + "," + e.getY() + "," + e.getX() + ",removePreview");
		}
	}
	
	private void handleMouseRelease(MouseEvent e)
	/*
	 * Triggered when mouse is released
	 */
	{
		//Initialize a BufferedImage array with all nulls to the size of max hashmap rows and cols
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK && type == "mini") {
			sl.textEmitted(type + "," + e.getY() + "," + e.getX() + ",makeMatrix");
		}
		
	}
	
	private void handleMouseEnter(MouseEvent e)
	/*
	 * Triggered when a mouse enters the Tile
	 */
	{
		//If left button is down, send a dragged message
		//Else, if right button is down and it's a regular tile, send a delete message
		//Else, tell MainFrame to display the preview layer
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			sl.textEmitted(type + "," + e.getY() + "," + e.getX() + ",dragged");
		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK && type == "regular") {
			sl.textEmitted(type + "," + e.getY() + "," + e.getX() + ",delete");
		} else if (type == "regular") {
			sl.textEmitted(type + "," + e.getY() + "," + e.getX() + ",showPreview");
		}
	}
	
	private void handleMousePressed(MouseEvent e)
	/*
	 * Triggered when mouse is pressed in the Tile
	 */
	{
		//If left button is pressed, send a clicked message
		//Else, if right button is pressed and it's a regular tile, send a delete message
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			sl.textEmitted(type + "," + e.getY() + "," + e.getX() + ",clicked");
		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK && type == "regular") {
			sl.textEmitted(type + "," + e.getY() + "," + e.getX() + ",delete");
		}
	}
	
	private void handleMouseWheelMoved(MouseWheelEvent e)
	/*
	 * Triggered when the mouse wheel is moved in the Tile
	 */
	{
		//Transmits the row, column, and amount of rotation
		//Note: Notches always seems to be either positive 1 or negative 1
		if (type == "regular") {
		int notches = e.getWheelRotation();
		sl.textEmitted(type + "," + e.getY() + "," + e.getX() + ",rotate~" + notches);
		}
	}
	
	
}
