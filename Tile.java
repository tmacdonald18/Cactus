/*
 * Tile.java
 * Author:	Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	Store a tile image.
 * 			Listen for user mouse actions.
 */

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

class Tile extends JPanel {
	
	//Store dimensions for each Rectangle/BufferedImage for drawing
	private int width;
	
	//The row and col the tile belongs to
	private int row, col;
	
	//Whether this tile is set as a collision
	private boolean collision;
	
	//Whether or not this tile is selected
	private boolean selected = false;
	
	//The type of grid this tile belongs to
	private String type;
		
	//Holds the images for this tile, each slot is a different layer
	private BufferedImage[] image;
	
	//If not null, draw this over anything else drawn on the tile
	//Upon exiting the cell, set value to null
	private BufferedImage previewLayer;
	
	//Sends messages from Tile to GridUI
	private StringListener listener;
	
	//Interprets mouse clicks and performs actions based on a click
	private MouseListener mouseListener;
	
	//Interprets mouse wheel changes
	private MouseWheelListener mouseWheelListener;
	
	//The currently selected layer
	private int layer;
	
	//Whether or not the image should be rotated
	//This will probably eventually be removed once matrix rotation is implemented
	private boolean rotate;
	
	//Allows for the image rotation, will probably eventually be removed once matrix rotation is implemented
	private AffineTransform at = new AffineTransform();
	
	public Tile(int width, final int row, final int col, final String type)
	/*
	 * Default Constructor for a Tile object
	 * Parameters:
	 * 		width -- The tile size (width == height)
	 * 		row -- The row the tile belongs to 
	 * 		col -- The col the tile belongs to
	 * 		type -- The type of GridUI the tile belongs to
	 */
	{
		//At the moment, just initialize the image array to be size 10, because there shouldn't be more than 10 layers
		//Eventually should change this so that it's dynamically sized
		this.image = new BufferedImage[10];
		
		this.width = width;
		this.collision = false;
		this.row = row;
		this.col = col;
		this.type = type;
		this.layer = 0;
		this.rotate = false;
		this.previewLayer = null;
		
		//Create and implement a new mouse listener to wait for user clicks
		mouseListener = new MouseListener() {

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
		mouseWheelListener = new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				handleMouseWheelMoved(e);
			}
			
		};
		
		//Add mouseListener to the Tile
		addMouseListener(mouseListener);
		
		//Add mouse wheel listener to the Tile
		addMouseWheelListener(mouseWheelListener);
		
		//Not sure if this is necessary?
		setPreferredSize(new Dimension(width, width));
	}
	
	private void handleMouseExit(MouseEvent e)
	/*
	 * Triggered when mouse leaves the tile
	 */
	{
		if (type == "regular") {
		listener.textEmitted(row + "," + col +",removePreview");
		}
	}
	
	private void handleMouseRelease(MouseEvent e)
	/*
	 * Triggered when mouse is released
	 */
	{
		//Initialize a BufferedImage array with all nulls to the size of max hashmap rows and cols
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK && type == "mini") {
			listener.textEmitted(row + "," + col + ",makeMatrix");
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
			listener.textEmitted(row + "," + col + ",dragged");
		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK && type == "regular") {
			listener.textEmitted(row + "," + col + ",delete");
		} else if (type == "regular") {
			listener.textEmitted(row + "," + col + ",showPreview");
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
			listener.textEmitted(row + "," + col + ",clicked");
		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK && type == "regular") {
			listener.textEmitted(row + "," + col + ",delete");
		}
	}
	
	private void handleMouseWheelMoved(MouseWheelEvent e)
	/*
	 * Triggered when the mouse wheel is moved in the Tile
	 */
	{
		//Transmits the row, column, and amount of rotation
		//Note: Notches always seems to be either positve 1 or negative 1
		if (type == "regular") {
		int notches = e.getWheelRotation();
		listener.textEmitted(row + "," + col + ",rotate~" + notches);
		}
	}
	
	public void rotation(int layer, boolean positive) 
	/*
	 * Rotates a tile, then repaints the tile
	 * Parameters:
	 * 		layer -- The currently selected layer (sent from MainFrame to keep current Tile layer updated)
	 * 		positive -- Which direction the mouse wheel was moved, used to determine clock wise or counter clock wise rotation
	 */
	{
		this.layer = layer;
		
		//First translate the image to the center of the Tile
		at.translate(getWidth() / 2, getHeight() / 2);
		
		//Determine if clockwise or counterclockwise rotation
		if (positive) {
			at.rotate(Math.PI / 2);
		} else {
			at.rotate(-Math.PI / 2);
		}
		
		//Set rotate as true then repaint
		rotate = true;
		repaint();
	}
	
	public BufferedImage getImage(int layer)
	/*
	 * Returns the Tile image for a given Tile layer
	 */
	{
		return image[layer];
	}

	public void setImage(BufferedImage image, int layer)
	/*
	 * Sets a given image to the Tile for a specific layer, then repaints the Tile
	 */
	{
		this.layer = layer;
		this.image[layer] = image;
		repaint();
	}

	public void setStringListener(StringListener listener) 
	/*
	 * Sets this Tile's StringListener
	 */
	{
		this.listener = listener;
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	/*
	 * Paints the Tile
	 */
	{
		super.paintComponent(g);
		
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
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width, width);
	}

	public boolean isCollision()
	/*
	 * Returns true if this Tile was set to be a collision
	 */
	{
		return collision;
	}

	public void setCollision(boolean collision) 
	/*
	 * Sets Tile to either collision or not collision
	 */
	{
		this.collision = collision;
	}

	public boolean isSelected() 
	/*
	 * Returns true if the Tile is selected, false if it is not
	 */
	{
		return selected;
	}

	public void setSelected(boolean selected) 
	/*
	 * Sets the Tile to be selected or not selected
	 */
	{
		this.selected = selected;
		repaint();
	}
	
	public void setPreviewLayer(BufferedImage img)
	/*
	 * Sets the preview layer to a certain image
	 */
	{
		this.previewLayer = img;
		repaint();
	}
}