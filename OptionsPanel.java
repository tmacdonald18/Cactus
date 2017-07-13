import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OptionsPanel extends JPanel {

	private JButton insert, delete, resize;
	private JTextField rows, cols, width;
	private DataTextPanel data;
	private TileChooser tileChooser;
	
	private StringListener selectionListener;
	
	public OptionsPanel()
	/*
	 * This is a constructor
	 */
	{
		//data = new DataTextPanel();
		//tileChooser = new TileChooser("C:\\Users\\Tyler\\Desktop\\test.png");
		tileChooser = new TileChooser("C:\\Users\\n0286782\\Desktop\\test.png");
		tileChooser.setStringListener(new StringListener(){

			@Override
			public void textEmitted(String text) {
				selectionListener.textEmitted(text);
			}
			
		});
		
		setLayout(new BorderLayout());
		
		//add(data, BorderLayout.NORTH);
		
		add(tileChooser, BorderLayout.CENTER);
	}

	public DataTextPanel getData() {
		return data;
	}

	public void setData(DataTextPanel data) {
		this.data = data;
	}

	public void setStringListener(StringListener listener) {
		this.selectionListener = listener;
	}
	
	public TileChooser getTileChooser() {
		return this.tileChooser;
	}
}
