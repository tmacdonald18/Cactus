import java.awt.Color;
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

	private Tile[][] tiles;
	private int rows, cols, tileWidth;
	private String selectedPath;
	private StringListener gridListener;
	private String savePath;
	private BufferedImage[] images;
	
	public GridUI(int rows, int cols, final String type, BufferedImage[] images) 
	/*
	 * Constructor
	 */
	{
		if (type == "regular") {
			this.rows = rows;
			this.cols = cols;
			this.tileWidth = 32;
			this.savePath = "default.png";
			this.images = images;
			
			setLayout(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			
			gc.weightx = 1;
			gc.weighty = 1;
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.CENTER;
			
			tiles = new Tile[rows][cols];
			
			int counter = 0;
			
			System.out.println(images.length);
			
			//initialize tiles
			for (int i = 0; i < rows; i++) {
				gc.gridx = i;
				for (int j = 0; j < cols; j++) {
					gc.gridy = j;
					tiles[i][j] = new Tile(i * this.tileWidth, j * this.tileWidth, this.tileWidth, i, j);
					
					if (images != null) {
						tiles[i][j].setImage(images[counter]);
						System.out.println("setting image");
					}
					
					add(tiles[i][j], gc);
					counter++;
					tiles[i][j].setStringListener(new StringListener(){
	
						@Override
						public void textEmitted(String text) {
							gridListener.textEmitted(type + "," + text);
						}
						
					});
				}
			}
			
			//setPreferredSize(new Dimension(rows * 32, cols * 32));
		} else if (type == "mini") {
			this.rows = rows;
			this.cols = cols;
			this.tileWidth = 64;
			
			setLayout(new GridLayout(rows, cols));
			
			tiles = new Tile[rows][cols];
			
			//initialize tiles
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					tiles[i][j] = new Tile(i * this.tileWidth, j * this.tileWidth, this.tileWidth, i, j);
					add(tiles[i][j]);
					tiles[i][j].setStringListener(new StringListener(){
	
						@Override
						public void textEmitted(String text) {
							gridListener.textEmitted(type + "," + text);
						}
						
					});
				}
			}
		}
	}
	
	public String getSelectedPath() {
		return selectedPath;
	}

	public void setSelectedPath(String selectedPath) {
		this.selectedPath = selectedPath;
	}

	public void setStringListener(StringListener listener) {
		this.gridListener = listener;
	}
	
	public Tile getTile(int i, int j)
	/*
	 * Retrieves a tile at a certain position
	 */
	{
		return this.tiles[i][j];
	}
	
	public void setSavePath(String path) {
		if (!path.endsWith(".png"))
			path = path + ".png";
		
		this.savePath = path;
	}
	
	public void saveGrid()
	/*
	 * This is the save function which will combine all of the tiles into one giant PNG bitmap image
	 */
	{
		BufferedImage result = new BufferedImage(cols * 32, rows * 32, BufferedImage.TYPE_INT_ARGB);
		Graphics g = result.getGraphics();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				BufferedImage temp = tiles[i][j].getImage();
				g.drawImage(temp, i * this.tileWidth, j * this.tileWidth, null);
			}
		}
		
		try {
			ImageIO.write(result, "png", new File(this.savePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
