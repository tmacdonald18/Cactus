import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class MainFrame extends JFrame {

	public static final int ROW_COUNT = 50, COLUMN_COUNT = 50;
	
	private GridUI grid;
	private OptionsPanel options;
	
	private BufferedImage selectedImage = null;
	
	public MainFrame()
	/*
	 * This is a constructor
	 */
	{
		super("Cactus");
		
		JPanel gridContainer = new JPanel();
		gridContainer.setLayout(new GridBagLayout());
		grid = new GridUI(ROW_COUNT, COLUMN_COUNT, "regular");
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridx = 0;
		gc.gridy = 0;
		gridContainer.add(grid, gc);
		gridContainer.setBorder(new TitledBorder("Level Designer"));
		
		options = new OptionsPanel();
		options.setBorder(new TitledBorder("Tile Selector"));
		
		StringListener dataDecider = new StringListener(){
			@Override
			public void textEmitted(String text) {
				dataDecision(text);
			}
		};
		
		options.setStringListener(dataDecider);
		grid.setStringListener(dataDecider);
		
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		JScrollPane scroll = new JScrollPane(gridContainer);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER))
					.addComponent(options, 222, GroupLayout.DEFAULT_SIZE, 222)
					.addComponent(scroll, 0, GroupLayout.DEFAULT_SIZE, 1000)
		);
		
		layout.setVerticalGroup(layout.createParallelGroup()
				.addComponent(options)
				.addComponent(scroll, 0, GroupLayout.DEFAULT_SIZE, 1000)
		);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        //save prompt
		    	//delete all files in current_session
		    	grid.saveGrid();
		    	
		    	File cur = new File("src" + File.separator + "current_session");
		    	
		    	for(File file: cur.listFiles()) 
		    	    if (!file.isDirectory()) 
		    	        file.delete();
		    	
		    	System.exit(0);
		    }
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setLocationRelativeTo(null);
		setSize(1000, 1000);
		setResizable(false);
		//pack();
		setVisible(true);
		
		//show save prompt
		JOptionPane.showMessageDialog(this, "You need to choose a save file path and name!");
		
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			grid.setSavePath(fc.getSelectedFile().getAbsolutePath());
		}
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
