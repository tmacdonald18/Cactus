import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class MainFrame extends JFrame {

	public int ROW_COUNT, COLUMN_COUNT;
	
	private GridUI grid;
	private OptionsPanel options;
	
	private String tilesetPath = "";
	private String loadPath = "";
	private JScrollPane scroll;
	private Loader ld;
	private BufferedImage selectedImage = null;
	
	private boolean loading = false;
	
	public MainFrame()
	/*
	 * This is a constructor
	 */
	{
		super("Cactus");
		
		//set look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Need to choose to load a level or create a new one
		Object[] choice = {"Create New", "Load Level"};
		int n = JOptionPane.showOptionDialog(null, "Are you creating a new level or loading one?", "Start up", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, choice, choice[1]);
		
		JFileChooser fc = new JFileChooser();
		
		switch(n){
		case JOptionPane.NO_OPTION:
			//loading a level
			
			int result = fc.showOpenDialog(null);				
			
			if (result == JFileChooser.APPROVE_OPTION) {
				loadPath = fc.getSelectedFile().getAbsolutePath();
				ld = new Loader(loadPath);
				loading = true;
				
				JOptionPane.showMessageDialog(null, "Now you need to select the tileset filepath.");
				int result2 = fc.showOpenDialog(null);
				
				if (result2 == JFileChooser.APPROVE_OPTION) {
					tilesetPath = fc.getSelectedFile().getAbsolutePath();
				}
			}

			break;
		case JOptionPane.YES_OPTION:
			//creating a new level
			//prompt for row count and column count and tileset path
			
			String s = (String) JOptionPane.showInputDialog("Enter rows,columns (example: 32,32)");
			this.ROW_COUNT = Integer.parseInt(s.split(",")[0].replace(" ", ""));
			this.COLUMN_COUNT = Integer.parseInt(s.split(",")[1].replace(" ", ""));
			
			JOptionPane.showMessageDialog(null, "Now you need to select the tileset filepath.");
			int result2 = fc.showOpenDialog(null);
			
			if (result2 == JFileChooser.APPROVE_OPTION) {
				tilesetPath = fc.getSelectedFile().getAbsolutePath();
			}
			
			break;
		case JOptionPane.CLOSED_OPTION:
			System.exit(0);
			break;
		}
		
		JPanel gridContainer = new JPanel();
		gridContainer.setLayout(new GridBagLayout());
		
		if (loading)
			grid = new GridUI(ld.getRows(), ld.getCols(), "regular", ld.loadMap());
		else
			grid = new GridUI(ROW_COUNT, COLUMN_COUNT, "regular", null);
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridx = 0;
		gc.gridy = 0;
		gridContainer.add(grid, gc);
		gridContainer.setBorder(new TitledBorder("Level Designer"));
		
		options = new OptionsPanel(tilesetPath);
		options.setBorder(new TitledBorder("Tile Selector"));
		
		StringListener dataDecider = new StringListener(){
			@Override
			public void textEmitted(String text) {
				dataDecision(text);
			}
		};
		
		options.setStringListener(dataDecider);
		grid.setStringListener(dataDecider);
		
		scroll = new JScrollPane(gridContainer);
		
		JSplitPane jp = new JSplitPane();
		jp.setLeftComponent(options);
		jp.setRightComponent(scroll);
		jp.getLeftComponent().setMinimumSize(options.getPreferredSize());
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(jp, BorderLayout.CENTER);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        //save prompt
		    	//delete all files in current_session
		    	
		    	//show save prompt
		    	Object[] options = {"Yes", "No", "Cancel"};
				int answer = JOptionPane.CLOSED_OPTION;
		    	
				while(answer == JOptionPane.CLOSED_OPTION)
					answer = JOptionPane.showOptionDialog(null, "Would you like to save?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
				
				switch(answer) {
					case JOptionPane.YES_OPTION:
						
						JFileChooser fc = new JFileChooser();
						int returnVal = fc.showSaveDialog(null);
						
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							grid.setSavePath(fc.getSelectedFile().getAbsolutePath());
						}
						
						grid.saveGrid();
					case JOptionPane.NO_OPTION:
						File cur = new File("src" + File.separator + "current_session");
						
						for(File file : cur.listFiles())
							if (!file.isDirectory())
								file.delete();
						
						File current = new File("src" + File.separator + "current_session");
				    	
				    	for(File file: current.listFiles()) 
				    	    if (!file.isDirectory()) 
				    	        file.delete();
						
						System.exit(0);
						
						break;
					case JOptionPane.CANCEL_OPTION:
						break;
					}
		    }
		});
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//setLocationRelativeTo(null);
		setSize(1000, 1000);
		//setResizable(false);
		//pack();
		setVisible(true);
		
	}
	
	private void loadLevel(String path) 
	/*
	 * Loads a created level
	 */
	{
		
	}
	
	private void dataDecision(String text) {
		String[] data = text.split(",");
		String type = data[0];
		int row = Integer.parseInt(data[1]);
		int col = Integer.parseInt(data[2]);
		
		System.out.println(type + " " + row + " " + col);
		
		if (type.contains("mini")) {
			selectedImage = options.getTileChooser().getSpecficImage(row, col);
			System.out.println("The new selected image is: " + selectedImage.toString());
		} else if (type.contains("regular")) {
			if (selectedImage != null) {
				System.out.println("Setting tile to " + selectedImage.toString());
				grid.getTile(row, col).setImage(selectedImage);
			} else
				System.out.println("Looks like there is no selected image");
		}
	}
}
