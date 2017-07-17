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

class Tile extends JPanel implements MouseListener {
	
	private int xMin, yMin, width, row, col;
	private boolean collision;
	private boolean selected = false;
	
	private String type;
	
	private String imagePath;
	private BufferedImage[] image;
	
	private StringListener listener;
	
	private int layer;
	
	private boolean rotate;
	
	private AffineTransform at = new AffineTransform();
	
	public Tile(int x, int y, int width, final int row, final int col, String type)
	/*
	 * Default Constructor for a tile
	 */
	{
		this.imagePath = "";
		this.image = new BufferedImage[10];
		this.xMin = x;
		this.yMin = y;
		this.width = width;
		this.collision = false;
		this.row = row;
		this.col = col;
		this.type = type;
		this.layer = 0;
		this.rotate = false;
		
		addMouseListener(this);
		
		this.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				int notches = arg0.getWheelRotation();
				System.out.println(row + "," + col + ",rotate~" + notches);
				listener.textEmitted(row + "," + col + ",rotate~" + notches);
			}
			
		});
		
		setPreferredSize(new Dimension(width, width));
	}
	
	public void rotation(int layer, boolean positive) {
		this.layer = layer;
		
		at.translate(getWidth() / 2, getHeight() / 2);
		
		if (positive) {
			at.rotate(Math.PI / 2);
		} else {
			at.rotate(-Math.PI / 2);
		}
		
		rotate = true;
		repaint();
	}
	
	public BufferedImage getImage(int layer) 
	{
		return image[layer];
	}

	public void setImage(BufferedImage image, int layer) {
		this.layer = layer;
		this.image[layer] = image;
		repaint();
	}

	public void setStringListener(StringListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			for (int i = 0; i < image.length; i++) {
				System.out.println(i + " compared with " + layer);
				if (rotate && i == layer) {
					System.out.println("rotating in tile!!");
					at.translate(-image[i].getWidth() / 2, -image[i].getHeight() / 2);
					Graphics2D g2d = (Graphics2D) g;
					g2d.drawImage(image[i], at, null);
					rotate = false;
				} else {
					g.drawImage(image[i], 0, 0, this);
				}
			}
		} 
		
		if (selected) {
			g.setColor(new Color(135, 206, 235, 225));
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
			listener.textEmitted(this.row + "," + this.col + ",dragged");
		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK && this.type == "regular") {
			listener.textEmitted(this.row + "," + this.col + ",delete");
			//this.image[this.layer] = null;
		}
		
		//repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {		
		if (e.getButton() == MouseEvent.BUTTON1 && this.listener != null) {
			listener.textEmitted(this.row + "," + this.col + ",clicked");
		} else if (e.getButton() == MouseEvent.BUTTON3 && this.type == "regular") {
			listener.textEmitted(this.row + "," + this.col + ",delete");
			//this.image[this.layer] = null;
		}
		
		//repaint();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "Tile [xMin=" + xMin + ", yMin=" + yMin + ", width=" + width
				+ ", collision=" + collision + "]";
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		repaint();
	}
	
	
}