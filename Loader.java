import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Loader {
	
	private String loadPath;
	private int rows, cols;
	private BufferedImage[] images;
	
	public Loader(String loadPath)
	{
		this.loadPath = loadPath;
	}
	
	public BufferedImage[] loadMap() 
	/*
	 * Splits a tileset into 32x32 tiles and stores them as PNGs within the "current_session" folder
	 * Returns the number of tiles there are
	 */
	{
		
		BufferedImage image = null;
        FileInputStream fis = null;
		
        try {
			fis = new FileInputStream(loadPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			image = ImageIO.read(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //reading the image file

        rows = image.getHeight() / 32; //You should decide the values for rows and cols variables
        cols = image.getWidth() / 32;
        
        System.out.println("this many rows: " + rows + "   this many columns: " + cols);
        
        int chunks = rows * cols;

        int chunkWidth = image.getWidth() / cols; // determines the chunk width and height
        int chunkHeight = image.getHeight() / rows;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                // draws the image chunk
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        System.out.println("Loading done");

        this.images = imgs;
        
        return images;
	}

	public String getLoadPath() {
		return loadPath;
	}

	public void setLoadPath(String loadPath) {
		this.loadPath = loadPath;
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

	public BufferedImage[] getImages() {
		return images;
	}

	public void setImages(BufferedImage[] images) {
		this.images = images;
	}
}
