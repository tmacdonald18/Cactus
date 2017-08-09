import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

/*
 * Magnifier.java
 * Author:	http://codeidol.com/java/swing/Rendering/Create-a-Magnifying-Glass-Component/#part-19
 * Modified By: Tyler MacDonald
 * Purpose:	Display a zoomed in area of the screen to get a better view
 * 			of the map being created
 */
public class Magnifier extends JComponent {
	
	double zoom;
	Point point;
	Dimension mySize;
	Robot robot;
	
	public Magnifier (Dimension size, double zoom) {
	
		
		// flag to say don't draw until we get a MouseMotionEvent
		point = new Point (-1, -1);
		this.mySize = size;
		this.zoom = zoom;
		
		// if we can't get a robot, then we just never
		// paint anything
		try {
	        robot = new Robot();
		} catch (AWTException awte) {
			System.err.println ("Can't get a Robot");
			awte.printStackTrace();
		}
	}
	
	public void paint (Graphics g) {
		if ((robot == null)) {
			g.setColor (Color.blue);
			g.fillRect (0, 0, mySize.width, mySize.height);
			return;
		}
		
		Rectangle grabRect = computeGrabRect();
		BufferedImage grabImg = robot.createScreenCapture (grabRect);
		Image scaleImg = grabImg.getScaledInstance (mySize.width, mySize.height, Image.SCALE_FAST);
		g.drawImage (scaleImg, 0, 0, null);
	
	}
	
	private Rectangle computeGrabRect() {
		
		// width, height are size of this comp / zoom
		int grabWidth = (int) ((double) mySize.width / zoom);	
		int grabHeight = (int) ((double) mySize.height / zoom);

		// upper-left corner is current point
		return new Rectangle (point.x - grabWidth/2, point.y - grabHeight/2, grabWidth, grabHeight);
	}
	
	public Dimension getPreferredSize() { return mySize; }
	public Dimension getMinimumSize() { return mySize; }
	public Dimension getMaximumSize() { return mySize; }
	
	public void incrementZoom(double zoomAdd)
	{
		this.zoom = this.zoom + zoomAdd;
		repaint();
	}
	
	public void updateImage(int width, int height)
	{
		point = MouseInfo.getPointerInfo().getLocation();
		mySize.width = width;
		mySize.height = height;
		repaint();
	}
}
