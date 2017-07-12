import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class GridUI extends JPanel {

	private Tile[][] tiles;
	private int rows, cols, tileWidth;
	
	private String selectedPath;
	private StringListener gridListener;
	
	public GridUI(int rows, int cols, String type) 
	/*
	 * Constructor
	 */
	{
		if (type == "regular") {
			this.rows = rows;
			this.cols = cols;
			this.tileWidth = 32;
			
			setLayout(new GridLayout(rows, cols));
			
			tiles = new Tile[rows][cols];
			
			//initialize tiles
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					tiles[i][j] = new Tile(i * this.tileWidth, j * this.tileWidth, this.tileWidth);
					add(tiles[i][j]);
					tiles[i][j].setStringListener(new StringListener(){
	
						@Override
						public void textEmitted(String text) {
							gridListener.textEmitted(text);
						}
						
					});
				}
			}
			
			setPreferredSize(new Dimension(rows * 32, cols * 32));
		} else if (type == "mini") {
			this.rows = rows;
			this.cols = cols;
			this.tileWidth = 64;
			
			setLayout(new GridLayout(rows, cols));
			
			tiles = new Tile[rows][cols];
			
			//initialize tiles
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					tiles[i][j] = new Tile(i * this.tileWidth, j * this.tileWidth, this.tileWidth);
					add(tiles[i][j]);
					tiles[i][j].setStringListener(new StringListener(){
	
						@Override
						public void textEmitted(String text) {
							gridListener.textEmitted(text);
						}
						
					});
				}
			}
		}
	}
	
	private void setAllPaths() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				tiles[i][j].setImagePath(this.selectedPath);
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
	

}
