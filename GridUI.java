/*
 * GridUI.java
 * Author:	Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	Used as a container for Tile objects.
 * 			Primarily used to retrieve a specific Tile, or perform mass operations on all of the Tiles.
 * 			Also contains the function for saving.
 */

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
	
	public GridUI(int rows, int cols, final String type, BufferedImage[][][] images, int layers) 
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
		
		if (type == "regular") {
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
			
		} else if (type == "loading") { 
			this.tileWidth = 32;
			
			setLayout(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			
			gc.weightx = 1;
			gc.weighty = 1;
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.CENTER;
			
			tiles = new Tile[rows][cols];
			
			loadLevelGrid(gc, images, layers);
			
			this.type = "regular";
			
		} else if (type == "mini") {
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
				add(tiles[i][j]);
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
				
				add(tiles[i][j], gc);
				tiles[i][j].setStringListener(sl);
			}
		}
	}
	
	private void loadLevelGrid(GridBagConstraints gc, BufferedImage[][][] imgs, int layers)
	/*
	 * 
	 */
	{
		for (int k = 0; k < layers; k++) {
			for (int i = 0; i < rows; i++) {
				gc.gridx = i;
				for (int j = 0; j < cols; j++) {
					gc.gridy = j;
					tiles[i][j] = new Tile(this.tileWidth, i, j, type);
					tiles[i][j].setImage(imgs[k][i][j], k);
					
					add(tiles[i][j], gc);
					tiles[i][j].setStringListener(sl);
				}
			}
		}
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
	
	public void saveGrid(int numLayers)
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
		
		for (int i = 0; i < numLayers; i++) {
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
	
	private void drawLayer(Graphics g, int layer)
	/*
	 * Helped function for drawing each layer to a BufferedImage
	 */
	{
		BufferedImage temp;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				temp = tiles[i][j].getImage(i);
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
	
	
}
