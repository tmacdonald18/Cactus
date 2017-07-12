import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OptionsPanel extends JPanel {

	private JButton insert, delete, resize;
	private JTextField rows, cols, width;
	private DataTextPanel data;
	private TileChooser tileChooser;
	
	public OptionsPanel()
	/*
	 * This is a constructor
	 */
	{
		data = new DataTextPanel();
		resize = new JButton("Resize");
		tileChooser = new TileChooser("C:\\Users\\Tyler\\Desktop\\test.png");
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.gridx = 0;
		gc.gridy = 0;
		
		add(resize, gc);
		
		gc.gridy = 1;
		gc.fill = GridBagConstraints.BOTH;
		add(data, gc);
		
		gc.gridy = 2;
		add(tileChooser, gc);
	}

	public DataTextPanel getData() {
		return data;
	}

	public void setData(DataTextPanel data) {
		this.data = data;
	}

}
