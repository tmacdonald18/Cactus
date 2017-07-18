/*
 * MainFrame.java
 * Author:	Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	Controls the entire program functionality.
 * 			Prompts user for all setup inputs.
 * 			Creates all components and place them within the JFrame layout.
 * 			Acts as hub for incoming messages from the different components, and acts accordingly after translating these messages.	
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class MainFrame extends JFrame {

	//The amounts of rows and columns to be passed into the grid constructor
	public int ROW_COUNT, COLUMN_COUNT;
	
	//The GridUI that handles the user drawing the tiles
	private GridUI levelBuilderGrid;
	
	//The options panel that allows for the user to select the tiles to use
	private OptionsPanel tileChooserContainer;
	
	//Holds the levelBuilderGrid
	private JScrollPane scroll;
	
	//The path to the tileset currently chosen to be used
	//Initialized as "continue" in order to maintain a user error check while loop used later
	private String tilesetPath = "continue";
	
	//Holds the total number of layers being used
	private int totalLayers = 0;
	
	//Holds the currently selected layer
	private int selectedLayer = 0;
	
	//This hashmap should contain all of the currently selected tile images based on row and column
	private HashMap<Integer, HashMap<Integer, BufferedImage>> selectedRows = new HashMap<Integer, HashMap<Integer, BufferedImage>>();
	
	public MainFrame()
	/*
	 * Constructor for the main program frame
	 * Contains the overarching JPanel containers, and includes a JMenuBar and JSplitPane
	 */
	{
		super("Cactus");
		
		//set look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Prompts user for setup inputs
		setUp();
		
		//Create a container to hold the grid
		JPanel gridContainer = new JPanel();
		gridContainer.setLayout(new GridBagLayout());
		
		//Create the level builder grid by creating a new GridUI with the user inputed dimensions
		levelBuilderGrid = new GridUI(ROW_COUNT, COLUMN_COUNT, "regular", null);
		
		//Establish the layout for the gridContainer and add the levelBuilderGrid to it
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridx = 0;
		gc.gridy = 0;
		gridContainer.add(levelBuilderGrid, gc);
		gridContainer.setBorder(new TitledBorder("Level Designer"));
		
		//Create a new container for the tilechooser using the user inputed tilesetPath
		tileChooserContainer = new OptionsPanel(tilesetPath);
		tileChooserContainer.setBorder(new TitledBorder("Tile Selector"));
		
		//StringListener used to manage the decisions incoming from the child components
		StringListener dataDecider = new StringListener(){
			@Override
			public void textEmitted(String text) {
				if (text.contains("layerdecision")) {
					layerDecision(text);
				} else {
					dataDecision(text);
				}
			}
		};
		
		//Set the String Listeners
		tileChooserContainer.setStringListener(dataDecider);
		levelBuilderGrid.setStringListener(dataDecider);
		
		//Initialize the level builder container inside of a scroll pane
		scroll = new JScrollPane(gridContainer);
		
		//Create a Settings Component
		Settings settings = new Settings();
		settings.setStringListener(dataDecider);
		
		
		//Create a tabbed pane to hold Panels other than the level builder
		JTabbedPane jt = new JTabbedPane();
		jt.addTab("Tileset", tileChooserContainer);
		jt.addTab("Settings", settings);
		
		//Create the split pane which will allow the user to control component sizes
		JSplitPane jp = new JSplitPane();
		jp.setLeftComponent(jt);
		jp.setRightComponent(scroll);
		jp.getLeftComponent().setMinimumSize(tileChooserContainer.getPreferredSize());
		
		//Put everything onto the JFrame content pane
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(jp, BorderLayout.CENTER);
		this.getContentPane().setBackground(new Color(128, 255, 128));
		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        handleWindowClose();
		    }
		});
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//setLocationRelativeTo(null);
		setSize(1000, 1000);
		//pack();
		setVisible(true);
		
	}
	
	private void handleWindowClose()
	/*
	 * Handles the user attempting to close the application
	 */
	{	
    	//Ask user if they would like to save before quitting
    	Object[] options = {"Yes", "No", "Cancel"};
		int answer = JOptionPane.CLOSED_OPTION;
    	
		//Keep asking until they choose an option, not just close the window
		while(answer == JOptionPane.CLOSED_OPTION)
			answer = JOptionPane.showOptionDialog(null, "Would you like to save?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
		
		switch(answer) {
			case JOptionPane.YES_OPTION:
				
				//User has chosen to save
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showSaveDialog(null);
				
				//Sets the levelBuilderGrids save path to the user selection
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					levelBuilderGrid.setSavePath(fc.getSelectedFile().getAbsolutePath());
				}
				
				//Exports the grid as a .png file
				levelBuilderGrid.saveGrid(totalLayers);
	
			case JOptionPane.NO_OPTION:
				//This happens if the user hits yes or no
				//Deletes all files out of current_session cache
				File cur = new File("src" + File.separator + "current_session");
				
				for(File file : cur.listFiles())
					if (!file.isDirectory())
						file.delete();
				
				//Exit application
				System.exit(0);

			case JOptionPane.CANCEL_OPTION:
				//User selects cancel, does not close out application
				break;
			}
	}
	
	private void setUp()
	/*
	 * Handles all initial user prompts and responses
	 */
	{
		//Need to choose to load a level or create a new one
		Object[] choices = {"Create New", "Load Level"};
		int n = JOptionPane.showOptionDialog(null, "Are you creating a new level or loading one?", "Start up", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[1]);
		
		JFileChooser fc = new JFileChooser();
		
		switch(n) {
		case JOptionPane.NO_OPTION:
			//TODO: Fix the Load Option
			System.exit(0);
			break;
		case JOptionPane.YES_OPTION:
			//If User has chosen to create a new level
			
			//Prompt to enter rows and columns, then interpret the input
			String s = (String) JOptionPane.showInputDialog("Enter rows,columns (example: 32,32)");
			this.ROW_COUNT = Integer.parseInt(s.split(",")[0].replace(" ", ""));
			this.COLUMN_COUNT = Integer.parseInt(s.split(",")[1].replace(" ", ""));
			
			//Prompt user to select their tileset, and keep prompting until they do
			while (tilesetPath == "continue")
				tilesetPath = userInputTileset(fc);
			
			break;
		case JOptionPane.CLOSED_OPTION:
			System.exit(0);
			break;
		}
	}
	
	
	private String userInputTileset(JFileChooser fc)
		/*
		 * Prompts the user to select a tileset
		 * Returns the absolute path to the tileset, or "continue" if the user didn't select anything
		 */
		{
			//Notify user that they must select a tileset filepath
			JOptionPane.showMessageDialog(null, "Now you need to select the tileset filepath.");
			int result2 = fc.showOpenDialog(null);
			
			if (result2 == JFileChooser.APPROVE_OPTION) {
				return fc.getSelectedFile().getAbsolutePath();
			}
			
			return "continue";
		}
	
	
	private void layerDecision(String text) 
	/*
	 * Called if the MainFrame decides there is a layer selection being made
	 * POST: Sets the selected layer to the selected layer
	 */
	{
		this.selectedLayer = Integer.parseInt(text.split(",")[1]);
	}

	private void dataDecision(String text) 
	/*
	 * This is the bulk of the decision making
	 * Handles any incoming data in the form of a String from a StringListener, then decides what to do
	 */
	{
		if (text.contains("clear")) {
			clearOutSelection();			
		
		} else {
		
			//Split the text at commas, since each data segment is separated by a comma
			String[] data = text.split(",");
			
			//The first segment should be the grid type that is sending the message
			String type = data[0];
			
			//The second segment should be the row sending the message
			//The third segment should be the column sending the message
			//These are used to identify which tile is transmitting
			int row = Integer.parseInt(data[1]);
			int col = Integer.parseInt(data[2]);
			
			//The fourth segment contains a miscellaneous tag used to make decisions
			String tag = data[3];
			
			if (tag.contains("delete")) {
				//Set the transmitting tile's image to null
				levelBuilderGrid.getTile(row, col).setImage(null, selectedLayer);
			
			} else {
			
				if (type.contains("mini")) {
					//If the tileChooser is transmitting
					
					//Check if hashmap is empty
					boolean empty = selectedRows.isEmpty();
					
					if (!empty && tag.contains("clicked")) {
						//Clear out the selection before adding to it
						clearOutSelection();
					}
					
					//Add to the hashmap
					addToHashmapSelection(row, col);
					
				} else if (type.contains("regular")) {
					//If the levelBuilder is transmitting
					
					if (tag.contains("rotate")) {
						//If the builder wants to rotate a selection
						int notches = Integer.parseInt(tag.split("~")[1]);
						handleSelectionRotation(notches, row, col);						
						
					} else if (!selectedRows.isEmpty()) {
						//If the hashmap is not empty
						handleSelectionPaste(row, col);
						
					} else
						System.out.println("Looks like there is no selected image");
				}
			}
		}
	}//end
	
	private void handleSelectionPaste(int row, int col)
	/*
	 * Takes the current selection and pastes it onto the levelBuilder
	 */
	{
		//Idea:
		//Hashmap should contain null values for every column not used but that exists
		//Essentially should be in the form of a matrix
		Object[] rowKeys = selectedRows.keySet().toArray();
		Arrays.sort(rowKeys);
		
		int rowOffset = 0, colOffset = 0;
		
		for (int i = 0; i < rowKeys.length; i++) {
			//System.out.println("This row is: " + rowKeys[i] + " and has the following columns selected: ");
			Object[] colKeys = selectedRows.get(rowKeys[i]).keySet().toArray();
			Arrays.sort(colKeys);
			
			//calculate row offset
			if (i != 0) {
				colOffset = (int) rowKeys[i] - (int) rowKeys[0];
				System.out.println("Changing the column offset to: " + colOffset);
			}
			
			for (int j = 0; j < colKeys.length; j++) {
				
				if (j != 0) {
					rowOffset = (int) colKeys[j] - (int) colKeys[0];
					System.out.println("Changing the row offset to: " + rowOffset);
				}
				
				System.out.println("Setting Tile at: " + (row + rowOffset) + " and " + (col + colOffset));
				
				try {
					levelBuilderGrid.getTile(row+rowOffset, col+colOffset).setImage(selectedRows.get(rowKeys[i]).get(colKeys[j]), selectedLayer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
			rowOffset = 0;
			colOffset = 0;
		}
	}
	
	private void handleSelectionRotation(int notches, int row, int col)
	/*
	 * Rotates a selection by 90 degrees
	 * TODO: Use matrix rotation to first find the transpose of the selection, then reverse the columns
	 */
	{
		if (notches > 0) {
			levelBuilderGrid.getTile(row, col).rotation(selectedLayer, true);
		} else {
			levelBuilderGrid.getTile(row, col).rotation(selectedLayer, false);
		}
	}
	
	private void addToHashmapSelection(int row, int col)
	/*
	 * Adds to the hashmap
	 * Determines if a new row and a new column needs to be added, or just a new column
	 */
	{
		//If the row is not found, then create a new key and supplementary hashmap
		if (!selectedRows.containsKey(row)) {
			HashMap<Integer, BufferedImage> selectedColumns = new HashMap<Integer, BufferedImage>();
			selectedRows.put(row, selectedColumns);
		} 
		
		//Store the BufferedImage
		selectedRows.get(row).put(col, tileChooserContainer.getTileChooser().getSpecficImage(row, col));
	}
	
	private void clearOutSelection()
	/*
	 * Clears out all selected tiles and hashmap
	 */
	{
		selectedRows.clear();
		tileChooserContainer.getTileChooser().getGrid().setAllUnselected();
	}
	
}
