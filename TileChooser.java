import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class TileChooser extends JPanel {

	private String tilesetPath;
	private GridUI miniGrid;
	
	public TileChooser(String tilesetPath)
	/*
	 * Constructor
	 */
	{
		this.tilesetPath = tilesetPath;
		int numTiles = splitTileset();
		
		miniGrid = new GridUI(numTiles / 3, 5, "mini");
		
		setLayout(new BorderLayout());
		
		add(miniGrid, BorderLayout.CENTER);
		
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

        int rows = image.getHeight() / 32; //You should decide the values for rows and cols variables
        int cols = image.getWidth() / 32;
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
}
