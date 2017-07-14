import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

class Tile extends JPanel implements MouseListener {
	
	private int xMin, yMin, width, layer, row, col;
	private boolean collision;
	
	private String imagePath;
	private BufferedImage image;
	
	private StringListener listener;
	
	public Tile(int x, int y, int width, int row, int col)
	/*
	 * Default Constructor for a tile
	 */
	{
		this.imagePath = "";
		this.image = null;
		this.xMin = x;
		this.yMin = y;
		this.width = width;
		this.collision = false;
		this.layer = 0;	
		this.row = row;
		this.col = col;
		
		addMouseListener(this);
		
		setPreferredSize(new Dimension(width, width));
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	
	}

	public void setStringListener(StringListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, this);
		} else {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, width);
		}
		
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width, width);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK && this.listener != null) {
			listener.textEmitted(this.row + "," + this.col);
		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			this.image = null;
		}
		
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {		
		if (e.getButton() == MouseEvent.BUTTON1 && this.listener != null) {
			listener.textEmitted(this.row + "," + this.col);
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			this.image = null;
		}
		
		repaint();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "Tile [xMin=" + xMin + ", yMin=" + yMin + ", width=" + width
				+ ", layer=" + layer + ", collision=" + collision + "]";
	}
	
	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}

	public int getxMin() {
		return xMin;
	}

	public void setxMin(int xMin) {
		this.xMin = xMin;
	}

	public int getyMin() {
		return yMin;
	}

	public void setyMin(int yMin) {
		this.yMin = yMin;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	
}