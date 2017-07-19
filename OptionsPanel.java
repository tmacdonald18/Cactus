/*
 * OptionsPanel.java
 * Author:	Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	Acts as a container for the TileChooser object
 */

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

public class OptionsPanel extends JPanel {
	
	//The TileChooser object that the user interacts with
	private TileChooser tileChooser;
	
	//The path that the tileset is loaded from
	private String tilesetPath;
	
	//Sends messages from OptionsPanel to MainFrame
	private StringListener selectionListener;
	
	public OptionsPanel(String path)
	/*
	 * Constructor for the OptionsPanel
	 * Parameters:
	 * 		path -- The Absolute path to the tileset to load
	 */
	{
		//Initialize tilesetPath and tileChooser
		this.tilesetPath = path;
		tileChooser = new TileChooser(tilesetPath);
		
		//Set a StringListener to listen for messages outputted from the tileChooser
		//Sends these messages to MainFrame
		tileChooser.setStringListener(new StringListener(){

			@Override
			public void textEmitted(String text) {
				selectionListener.textEmitted(text);
			}
			
		});
		
		setLayout(new BorderLayout());
		add(tileChooser, BorderLayout.CENTER);
		
		//Assign a preferred size to make sure the split pane in MainFrame is accurately sized
		Dimension d = this.getPreferredSize();
		d.width = 300;
		setPreferredSize(d);
	}

	public void setStringListener(StringListener listener) 
	/*
	 * Assigns a StringListener to selectionListener
	 */
	{
		this.selectionListener = listener;
	}
	
	public TileChooser getTileChooser() 
	/*
	 * Returns the TileChooser
	 */
	{
		return this.tileChooser;
	}
	
	public void setTilesetPath(String path) 
	/*
	 * Assigns a String path to the tileset path
	 * Would only potentially be used if loading a new tileset
	 */
	{
		this.tilesetPath = path;
	}
}
