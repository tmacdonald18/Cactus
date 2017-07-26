/*
 * TileChooser.java
 * Author:	Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	Retrieves the tileset image and splits it into the proper format.
 * 			Scales each new image to fit the expanded grid tiles.
 * 			Initializes and creates the tile chooser grid full of tiles.
 */

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TileChooser extends JPanel {

	//Stores the absolute path to the currently loaded tileset
	private String tilesetPath;
	
	//Stores the newly created tile chooser grid
	private GridUI miniGrid;
	
	//An array full of each image extracted from the tileset
	private BufferedImage[] images;
	
	//Sends messages from TileChooser to OptionsPanel
	private StringListener choiceListener;
	
	//Contains the minigrid
	private JScrollPane scroll;
	
	//Stores the amount of rows in columns in the tileset
	private int rows, cols;

	//Stores the total number of tiles
	private int numTiles;
	
	public TileChooser(String tilesetPath)
	/*
	 * Constructor for TileChooser
	 * Parameters:
	 * 		tilesetPath -- The absolute path to the tileset to be loaded
	 */
	{
		this.tilesetPath = tilesetPath;	
		
		//Loads and splits up the tileset into tiles
		//Sets the values of rows and cols
		//Stores all tiles in images
		//Returns the total number of tiles
		this.numTiles = splitTileset();
		
		//Creates a new minigrid which will allow for tile selection
		miniGrid = new GridUI(rows, cols, "mini", null, 0);
		
		//Creates a new StringListener to check if miniGrid is sending messages
		miniGrid.setStringListener(new StringListener(){

			@Override
			public void textEmitted(String text) {
				choiceListener.textEmitted(text);
			}
			
		});
		
		//Counter to keep track of which image is being loaded into the miniGrid
		int counter = 0;
		
		//For each tile in the grid, set the image as a scaled version of the associated tile extracted from the tileset
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				miniGrid.getTile(i, j).setImage(scale(images[counter], BufferedImage.TYPE_INT_ARGB, 64, 64, 2.0, 2.0), 0);
				counter++;
			}
		}
		
		setLayout(new BorderLayout());
		
		JButton btn = new JButton("Clear Selection");
		btn.setMnemonic(KeyEvent.VK_A);
		
		//Listens for button clicks
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				choiceListener.textEmitted("clear");
			}
			
		});
		
		scroll = new JScrollPane(miniGrid);
		
		add(btn, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
	}
	
	private BufferedImage scale(BufferedImage imageToScale, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) 
	/*
	 * Scales a BufferedImage and returns the scaled version of the BufferedImage
	 * Parameters:
	 * 		imageToScale -- The BufferedImage that is being scaled
	 * 		imageType -- What type of BufferedImage is being loaded (ie BufferedImage.TYPE_INT_ARGB)
	 * 		dWidth -- The desired pixel width you want the image to be scaled to
	 * 		dHeight -- The desired pixel height you want the image to be scaled to
	 * 		fWidth -- The factor by which the width is being scaled
	 * 		fHeight -- The factor by which the height is being scaled
	 */
	{
		BufferedImage scaledImage = null;
		if (imageToScale != null) {
			scaledImage = new BufferedImage(dWidth, dHeight, imageType);
			Graphics2D g2d = scaledImage.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
			g2d.drawRenderedImage(imageToScale, at);
		}
		return scaledImage;
	}
	
	private int splitTileset() 
	/*
	 * Splits a tileset into 32x32 tiles and stores them as PNGs within the "current_session" folder
	 * Returns the number of tiles there are
	 * This should implement a progress bar
	 */
	{
		
		BufferedImage image = null;
        FileInputStream fis = null;
		
        //Attempt to load tileset as a file
        try {
			fis = new FileInputStream(tilesetPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
        //Try to read the file as a BufferedImage
		try {
			image = ImageIO.read(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Get the number of rows and columns if split into 32x32 tiles
        rows = image.getHeight() / 32;
        cols = image.getWidth() / 32;
        
        //Determine the number of tiles
        int chunks = rows * cols;

        //Determines each tile's width and height
        int chunkWidth = image.getWidth() / cols; 
        int chunkHeight = image.getHeight() / rows;
        
        //Stores the current image position
        int count = 0;
        
        //BufferedImage array to hold each tile image
        BufferedImage imgs[] = new BufferedImage[chunks];
        
        //For each Tile, create and store an image in the array
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, BufferedImage.TYPE_INT_ARGB);

                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        System.out.println("Tileset Created");

        this.images = imgs;
        
        //Saves each tile as a png file
        for (int i = 0; i < imgs.length; i++) {
            try {
				ImageIO.write(imgs[i], "png", new File("src" + File.separator + "current_session" + File.separator + i + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        System.out.println("Tileset Image Files Created");
        
        return imgs.length;
	}
	
	public BufferedImage getSpecficImage(int row, int col) 
	/*
	 * Given a tile row and column should return the associated BufferedImage from images
	 */
	{		
		int counter = 0;
		int selection = 0;
		
		//Loops through each row and column, incrementing counter until it's the correct row and column then stores that number
		//TODO: Figure out a calculation to determine this instead of looping through everything
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (i == row && j == col)
					selection = counter;
				counter++;
			}
		}
		
		System.out.println("The row was " + row + " and the column was " + col + " and the selection was " + selection);
		
		miniGrid.getTile(row, col).setSelected(true);
				
		return images[selection];
	}
	
	public void setStringListener(StringListener listener) 
	/*
	 * Sets the choiceListener to a specified StringListener
	 */
	{
		this.choiceListener = listener;
	}

	public GridUI getGrid() 
	/*
	 * Returns the GridUI
	 */
	{
		return this.miniGrid;
	}
	
	public void setAdjustmentListeners(AdjustmentListener al)
	/*
	 * Sets the adjustment listeners on the scroll bars
	 */
	{
		scroll.getHorizontalScrollBar().addAdjustmentListener(al);
		scroll.getVerticalScrollBar().addAdjustmentListener(al);
	}
}
