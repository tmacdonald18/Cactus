import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class MainFrame extends JFrame {

	private GridUI grid;
	private OptionsPanel options;
	
	public MainFrame()
	/*
	 * This is a constructor
	 */
	{
		super("Cactus");
		
		grid = new GridUI(50, 50, "regular");
		options = new OptionsPanel();
		
		grid.setStringListener(new StringListener() {

			@Override
			public void textEmitted(String text) {
				sendTileToData(text);
			}
			
		});
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.gridx = 0;
		gc.gridy = 0;
		
		gc.fill = GridBagConstraints.BOTH;
		
		add(options, gc);
		
		gc.gridx = 1;
		gc.weightx = 5.0;
		
		add(new JScrollPane(grid), gc);
		
		pack();
		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        //save prompt
		    	//delete all files in current_session
		    	File cur = new File("src" + File.separator + "current_session");
		    	
		    	for(File file: cur.listFiles()) 
		    	    if (!file.isDirectory()) 
		    	        file.delete();
		    	
		    	System.exit(0);
		    }
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 1000);
		setVisible(true);
	}
	
	private void sendTileToData(String text) {
		options.getData().setText("");
		options.getData().append(text);
	}
}
