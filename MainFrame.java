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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class MainFrame extends JFrame {

	private Container contentPane;
	
	//The current working directory of the application
	public String current_directory;
	
	//The current path of the current_session folder
	public String current_session_path;
	
	//The amounts of rows and columns to be passed into the grid constructor
	public int ROW_COUNT, COLUMN_COUNT;
	
	//The GridUI that handles the user drawing the tiles
	private GridUI levelBuilderGrid;
	
	//The options panel that allows for the user to select the tiles to use
	private OptionsPanel tileChooserContainer;
	
	//Holds the levelBuilderGrid
	private JScrollPane levelScroll;
	
	private boolean showGrid = true, showZoom = false;
	
	private JFileChooser fc;
	
	//Holds the menu bar
	private JMenuBar menuBar;
	
	//Listens to menu clicks
	private ActionListener menuListener;
	
	//True if user is scrolling, false if user is not scrolling
	//Should only be able to paint on UI if scrolling is false
	private boolean scrolling = false;
	
	//The path to the tileset currently chosen to be used
	//Initialized as "continue" in order to maintain a user error check while loop used later
	private String tilesetPath = "continue";
	
	//Holds the total number of layers being used
	private int totalLayers = 0;
	
	//Holds the currently selected layer
	private int selectedLayer = 0;
	
	private Magnifier mag;
	private JFrame magFrame;
	
	//This hashmap should contain all of the currently selected tile images based on row and column
	private HashMap<Integer, HashMap<Integer, BufferedImage>> selectedRows = new HashMap<Integer, HashMap<Integer, BufferedImage>>();
	
	//This object is used to display the selection onto the level building grid, without actually permanently setting the tile images
	private ImageMatrix previewMatrix = new ImageMatrix();
	
	private BufferedImage[][][] loadImages = null;
	
	public MainFrame()
	/*
	 * Constructor for the main program frame
	 * Contains the overarching JPanel containers, and includes a JMenuBar and JSplitPane
	 */
	{
		super("Cactus");
		
		//set look and feel
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					System.out.println("Got Nimbus look");
					break;
				}
			}
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		try {
			//if current_directory does not exist, then create it
			current_directory = System.getProperty("user.dir");
			current_session_path = current_directory + File.separator + "current_session" + File.separator;
			File path = new File(current_session_path);
			
			if (!path.exists()) {
				path.mkdir();
				System.out.println("Created Directory in " + path.getAbsolutePath());
			} else {
				System.out.println("Could not create directory");
			}
			
		} catch (Exception e) {
			System.out.println("Error when attempting to create directory");
		}
		
		setUp();
		startUp();		
		
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
				System.out.println("Handle Save");
				XMLHandler xml = new XMLHandler();
				xml.saveToFile("hey", totalLayers, levelBuilderGrid, tileChooserContainer.getTileChooser().getGrid());
			case JOptionPane.NO_OPTION:
				//This happens if the user hits yes or no
				//Deletes all files out of current_session cache
				File cur = new File("current_session");
				
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
			
			int result5 = fc.showOpenDialog(null);
			
			String tempLoad = "";
				
			if (result5 == JFileChooser.APPROVE_OPTION) {
				tempLoad = fc.getSelectedFile().getAbsolutePath();
			}
			
			handleLoad(tempLoad);
			
			while (tilesetPath == "continue")
				tilesetPath = userInputTileset(fc);
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
	
	private void hitNew() {
		JFileChooser fc = new JFileChooser();
		
		String s = (String) JOptionPane.showInputDialog("Enter rows,columns (example: 32,32)");
		this.ROW_COUNT = Integer.parseInt(s.split(",")[0].replace(" ", ""));
		this.COLUMN_COUNT = Integer.parseInt(s.split(",")[1].replace(" ", ""));
		
		//Prompt user to select their tileset, and keep prompting until they do
		while (tilesetPath == "continue")
			tilesetPath = userInputTileset(fc);
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
		System.out.println("Here is the text: ");
		System.out.println(text);
		
		if (text.contains("clear")) {
			clearOutSelection();			
		
		} else if (text.contains("UPDATEIMAGE")) {
			if (showZoom)
				mag.updateImage(magFrame.getWidth(), magFrame.getHeight());
		} else if (!scrolling) {
		
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
			
				//If it's the tilechooser and it wants to make a matrix
				if (type.contains("mini") && tag.contains("makeMatrix")) {
					previewMatrix = new ImageMatrix(selectedRows);
					previewMatrix.printThisMatrix();
				} else if (type.contains("mini")) {
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
						
					} else if (tag.contains("showPreview")) {
						try {
							handleShowPreview(row, col);
						} catch (Exception e) {
							
						}
					} else if (tag.contains("removePreview")) {
						try {
							handleRemovePreview(row, col);
						} catch (Exception e) {
							
						}
						
					} else if (!selectedRows.isEmpty()) {
						//If the hashmap is not empty
						handleSelectionPaste(row, col);
						
					} else
						System.out.println("Looks like there is no selected image");
					
					if (showZoom)
						mag.updateImage(magFrame.getWidth(), magFrame.getHeight());
					
				}
			}
		}
	}//end
	
	private void handleRemovePreview(int row, int col)
	/*
	 * Removes the previous previewed selection
	 */
	{
		int width = previewMatrix.getCols();
		int height = previewMatrix.getRows();
		
		int centerColOffset = width / 2;
		int centerRowOffset = height / 2;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				try {
					levelBuilderGrid.getTile(row + j - centerColOffset, col + i - centerRowOffset).setPreviewLayer(null);
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	private void handleShowPreview(int row, int col)
	/*
	 * Takes the current selection and pastes it onto the levelBuilder preview
	 */
	{
		int width = previewMatrix.getCols();
		int height = previewMatrix.getRows();
		
		int centerColOffset = width / 2;
		int centerRowOffset = height / 2;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++)
				try {
					levelBuilderGrid.getTile(row + j - centerColOffset, col + i - centerRowOffset).setPreviewLayer(previewMatrix.getTile(i, j));
				} catch (Exception e) {
					
				}
			}
	}
	
	private void handleSelectionPaste(int row, int col)
	/*
	 * Takes the current selection (the previewMatrix) and pastes it onto the levelBuilder
	 */
	{	
		int width = previewMatrix.getCols();
		int height = previewMatrix.getRows();
		
		int centerColOffset = width / 2;
		int centerRowOffset = height / 2;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				try {
					if (previewMatrix.getTile(i, j) != null)
						levelBuilderGrid.getTile(row + j - centerColOffset, col + i - centerRowOffset).setImage(previewMatrix.getTile(i, j), selectedLayer);
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	private void handleSelectionRotation(int notches, int row, int col)
	/*
	 * Rotates a selection by 90 degrees
	 * TODO: Use matrix rotation to first find the transpose of the selection, then reverse the columns
	 */
	{
		handleRemovePreview(row, col);
		
		if (notches > 0) {
			previewMatrix.rotateRight();
		} else {
			previewMatrix.rotateLeft();
		}
		
		handleShowPreview(row, col);
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
		previewMatrix.clearPreview();
	}
	
	private void startUp() 
	/*
	 * Starting up the MainFrame
	 * Handles component placements
	 */
	{		
		//Create a container to hold the grid
		JPanel gridContainer = new JPanel();
		gridContainer.setLayout(new GridBagLayout());
		
		//Create the level builder grid by creating a new GridUI with the user inputed dimensions
		if (loadImages == null)
			levelBuilderGrid = new GridUI(ROW_COUNT, COLUMN_COUNT, "regular", null, 1, 0);
		else {
			System.out.println("total layers are " + totalLayers);
			levelBuilderGrid = new GridUI(ROW_COUNT, COLUMN_COUNT, "regular", loadImages, totalLayers, 1);
			loadImages = null;
		}
		
		//Establish the layout for the gridContainer and add the levelBuilderGrid to it
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridx = 0;
		gc.gridy = 0;
		
		levelBuilderGrid.setBorder(new LineBorder(Color.BLACK, 1, true));
		
		gridContainer.add(levelBuilderGrid, gc);
		
		gridContainer.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new TitledBorder("Level Designer")));
		
		//Create a new container for the tilechooser using the user inputed tilesetPath
		tileChooserContainer = new OptionsPanel(tilesetPath);
		tileChooserContainer.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new TitledBorder("Tile Selector")));
		
		//StringListener used to manage the decisions incoming from the child components
		StringListener dataDecider = new StringListener(){
			@Override
			public void textEmitted(String text) {
				if (text.contains("layerdecision")) {
					layerDecision(text);
				} else if (text.contains("addLayer")) {
					totalLayers++;
				} else {
					dataDecision(text);
				}
			}
		};
		
		//Set the String Listeners
		tileChooserContainer.setStringListener(dataDecider);
		levelBuilderGrid.setStringListener(dataDecider);
		
		//Initialize the level builder container inside of a scroll pane
		levelScroll = new JScrollPane(gridContainer);
		
		//Create an adjustment listener to keep track of when the user is scrolling either pane
		AdjustmentListener scrollListener = new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent event) {
				scrolling = event.getValueIsAdjusting();
			}
			
		};
		
		//Assign scrollListener to both scroll bars
		levelScroll.getHorizontalScrollBar().addAdjustmentListener(scrollListener);
		levelScroll.getVerticalScrollBar().addAdjustmentListener(scrollListener);
		
		//Create a Settings Component
		Settings settings = new Settings();
		settings.setStringListener(dataDecider);
		
		//Assign scrollListener to the TileChooser scroll bars
		tileChooserContainer.getTileChooser().setAdjustmentListeners(scrollListener);
		
		//Create a tabbed pane to hold Panels other than the level builder
		JTabbedPane jt = new JTabbedPane();
		jt.addTab("Tileset", tileChooserContainer);
		jt.addTab("Settings", settings);
		jt.addTab("Collision Manager", new JPanel());
		
		jt.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		//Create the split pane which will allow the user to control component sizes
		JSplitPane jp = new JSplitPane();
		jp.setLeftComponent(jt);
		jp.setRightComponent(levelScroll);
		jp.getLeftComponent().setMinimumSize(tileChooserContainer.getPreferredSize());
				
		//Add a mouse listener to the split pane divider so that selecting / painting can't happen if split pane is being changed
		BasicSplitPaneUI bspui = (BasicSplitPaneUI) jp.getUI();
		bspui.getDivider().addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				System.out.println("Pressing the mouse");
				scrolling = true;
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				System.out.println("Releasing the mouse");
				scrolling = false;
			}
			
		});
		
		//Put everything onto the JFrame content pane
		contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		contentPane.add(jp, BorderLayout.CENTER);
		//contentPane.setBackground(new Color(128, 255, 128));
		
		//Initialize the menu bar
		menuBar = createMenu();
		this.setJMenuBar(menuBar);
		
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
	
	private JMenuBar createMenu()
	/*
	 * Initializes the JMenuBar for the frame
	 */
	{
		JMenuBar menuBar = new JMenuBar();
		
		menuListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent action) {
				String itemName = action.getActionCommand();
				switch(itemName){
					case "New":
						System.out.println("Handle New");
						contentPane.removeAll();
						contentPane.repaint();
						tilesetPath = "continue";
						hitNew();
						startUp();
						break;
					case "Open...":
						System.out.println("Handle Open");
						contentPane.removeAll();
						contentPane.repaint();
						
						int result2 = fc.showOpenDialog(null);
						
						String loadPath = "";
						
						if (result2 == JFileChooser.APPROVE_OPTION) {
							loadPath = fc.getSelectedFile().getAbsolutePath();
						} else {
							System.out.println("Error with selecting load path");
							System.exit(0);
						}
						
						handleLoad(loadPath);
						while (tilesetPath == "continue")
							tilesetPath = userInputTileset(fc);
						
						//load;
						break;
					case "Save":
						System.out.println("Handle Save");
						XMLHandler xml = new XMLHandler();
						
						JFileChooser temp = new JFileChooser();
						
						int result3 = temp.showSaveDialog(null);
						
						String savePath = "";
						
						if (result3 == JFileChooser.APPROVE_OPTION) {
							savePath = temp.getSelectedFile().getAbsolutePath();
						} else {
							System.out.println("Error with selecting save path");
							System.exit(0);
						}
						
						xml.saveToFile(savePath, totalLayers, levelBuilderGrid, tileChooserContainer.getTileChooser().getGrid());
						break;
					case "Save As...":
						System.out.println("Handle Save As");
						XMLHandler xml3 = new XMLHandler();
						
						JFileChooser temp2 = new JFileChooser();
						
						int result4 = temp2.showSaveDialog(null);
						
						String savePath2 = "";
						
						if (result4 == JFileChooser.APPROVE_OPTION) {
							savePath2 = temp2.getSelectedFile().getAbsolutePath();
						} else {
							System.out.println("Error with selecting save path");
							System.exit(0);
						}
						
						xml3.saveToFile(savePath2, totalLayers, levelBuilderGrid, tileChooserContainer.getTileChooser().getGrid());
						break;
					case "Export As Layers":
						System.out.println("Handle Export As Layers");
						
						//User has chosen to export
						JFileChooser fc = new JFileChooser();
						int returnVal = fc.showSaveDialog(null);
						
						//Sets the levelBuilderGrids save path to the user selection
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							levelBuilderGrid.setSavePath(fc.getSelectedFile().getAbsolutePath());
						}
						
						//Exports the grid as a .png file
						levelBuilderGrid.exportGrid(totalLayers);
						break;
					case "Export As Image":
						System.out.println("Handle Export As Image");
					
						//User has chosen to export
						JFileChooser fc2 = new JFileChooser();
						int returnVal2 = fc2.showSaveDialog(null);
						
						//Sets the levelBuilderGrids save path to the user selection
						if (returnVal2 == JFileChooser.APPROVE_OPTION) {
							levelBuilderGrid.setSavePath(fc2.getSelectedFile().getAbsolutePath());
						}
						
						//Exports the grid as a .png file
						levelBuilderGrid.exportGridAsImage(totalLayers);
						break;
					case "Toggle Grid":
						System.out.println("Toggling the Grid");
						
						showGrid = ((AbstractButton)action.getSource()).getModel().isSelected();
						
						levelBuilderGrid.toggleGrid(showGrid);
						
						System.out.println("Show Grid is now: " + showGrid);
						break;
					case "Toggle Zoom":
						System.out.println("Toggling the Zoom");
						
						showZoom = ((AbstractButton)action.getSource()).getModel().isSelected();
						
						if (showZoom) {
							magFrame = new JFrame("Magnifier");
							
							mag = new Magnifier(new Dimension(150, 150), 2.0);
							magFrame.getContentPane().add(mag);
							magFrame.pack();
							magFrame.setLocation(new Point(300, 300));
							magFrame.setVisible(true);
							//magFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						} else {
							magFrame.removeAll();
							magFrame.dispose();
						}
					case "Zoom In":
						System.out.println("Zooming In");
						
						if (showZoom)
							mag.incrementZoom(0.5);
						
						break;
					case "Zoom Out":
						System.out.println("Zooming Out");
						
						if (showZoom)
							mag.incrementZoom(-0.5);
				}
						
			}
			
		};
		
		//File Menu Option
		JMenu menu = new JMenu("File");
		JMenuItem menuItem;
		
		menuItem = new JMenuItem("New");
		menuItem.addActionListener(menuListener);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Open...");
		menuItem.addActionListener(menuListener);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save");
		menuItem.addActionListener(menuListener);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save As...");
		menuItem.addActionListener(menuListener);
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("Export As Layers");
		menuItem.addActionListener(menuListener);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Export As Image");
		menuItem.addActionListener(menuListener);
		menu.add(menuItem);
		
		menuBar.add(menu);
		
		JCheckBoxMenuItem checkBoxItem;
		
		//Edit Menu Option
		JMenu editMenu = new JMenu("View");
		checkBoxItem = new JCheckBoxMenuItem("Toggle Grid");
		checkBoxItem.setSelected(true);
		checkBoxItem.addActionListener(menuListener);
		editMenu.add(checkBoxItem);
		
		editMenu.addSeparator();
		
		checkBoxItem = new JCheckBoxMenuItem("Toggle Zoom");
		checkBoxItem.addActionListener(menuListener);
		editMenu.add(checkBoxItem);
		
		menuItem = new JMenuItem("Zoom In");
		menuItem.addActionListener(menuListener);
		editMenu.add(menuItem);
		
		menuItem = new JMenuItem("Zoom Out");
		menuItem.addActionListener(menuListener);
		editMenu.add(menuItem);
		
		menuBar.add(editMenu);
	
		return menuBar;
	}
	
	private void handleLoad(String path)
	/*
	 * Incorporates XMLHandler to create a loader that loads a properly formatted XML file as a grid
	 */
	{
		String levelGrid = "LevelGrid";
		String loadFile = path;
		
		XMLHandler loader = new XMLHandler(loadFile);
		ROW_COUNT = loader.getRows(levelGrid);
		COLUMN_COUNT = loader.getCols(levelGrid);
		totalLayers = loader.getLayers(levelGrid);
		
		BufferedImage[][][] imgs = new BufferedImage[totalLayers][ROW_COUNT][COLUMN_COUNT];
		
		imgs = loader.getImages(totalLayers, ROW_COUNT, COLUMN_COUNT);
		
		loadImages = imgs;
		
		System.out.println("Loaded");
	}
	
}
