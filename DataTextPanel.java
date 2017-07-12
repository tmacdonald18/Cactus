import javax.swing.JTextArea;

class DataTextPanel extends JTextArea {
	
	public DataTextPanel()
	/*
	 * This is a constructor
	 */
	{
		super();
		setEditable(false);
	}
	
	public void appendTileData(Tile tile, int i, int j)
	/*
	 * Takes the toString component of a tile and appends it to the Text Area
	 */
	{
		this.append(tile.toString());
	}
}