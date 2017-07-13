import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TileChooser extends JPanel {

	private String tilesetPath;
	private GridUI miniGrid;
	private BufferedImage[] images;
	private StringListener choiceListener;
	
	private int numTiles, rows, cols;
	
	public TileChooser(String tilesetPath)
	/*
	 * Constructor
	 */
	{
		this.tilesetPath = tilesetPath;
		this.numTiles = splitTileset();
		
		miniGrid = new GridUI(rows, cols, "mini");
		miniGrid.setStringListener(new StringListener(){

			@Override
			public void textEmitted(String text) {
				choiceListener.textEmitted(text);
			}
			
		});
		
		int counter = 0;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				miniGrid.getTile(i, j).setImage(scale(images[counter], BufferedImage.TYPE_INT_ARGB, 64, 64, 2.0, 2.0));
				counter++;
			}
		}
		
		setLayout(new BorderLayout());
		
		add(new JScrollPane(miniGrid), BorderLayout.CENTER);
		
	}
	
	private BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
		BufferedImage dbi = null;
		if (sbi != null) {
			dbi = new BufferedImage(dWidth, dHeight, imageType);
			Graphics2D g = dbi.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
			g.drawRenderedImage(sbi, at);
		}
		return dbi;
	}
	
	private int splitTileset() 
	/*
	 * Splits a tileset into 32x32 tiles and stores them as PNGs within the "current_session" folder
	 * Returns the number of tiles there are
	 */
	{
		
		BufferedImage image = null;
        FileInputStream fis = null;
		
        try {
			fis = new FileInputStream(tilesetPath);
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
        System.out.println("Splitting done");

        this.images = imgs;
        
        //writing mini images into image files
        for (int i = 0; i < imgs.length; i++) {
            try {
            	//File saveFile = new File("C:" + File.separator + "Users" + File.separator + "Tyler" + File.separator + "Documents" + File.separator + "EclipseProjects" + File.separator + "Cactus" + File.separator + "src" + File.separator + "current_session" + File.separator + i + ".png");
				ImageIO.write(imgs[i], "png", new File("src" + File.separator + "current_session" + File.separator + i + ".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        System.out.println("Mini images created");
        
        return imgs.length;
	}
	
	public BufferedImage getSpecficImage(int row, int col) 
	/*
	 * Given a tile row and column should return the associated BufferedImage from images
	 */
	{
		int counter = 0;
		int selection = 0;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (i == row && j == col)
					selection = counter;
				counter++;
			}
		}
		
		return images[selection];
	}
	
	public void setStringListener(StringListener listener) {
		this.choiceListener = listener;
	}
}
