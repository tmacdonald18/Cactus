import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OptionsPanel extends JPanel {

	private JButton insert, delete, resize;
	private JTextField rows, cols, width;
	private DataTextPanel data;
	private TileChooser tileChooser;
	private String tilesetPath;
	
	private StringListener selectionListener;
	
	public OptionsPanel(String path)
	/*
	 * This is a constructor
	 */
	{
		//data = new DataTextPanel();
		//tileChooser = new TileChooser("C:\\Users\\Tyler\\Desktop\\test.png");
		this.tilesetPath = path;
		tileChooser = new TileChooser(tilesetPath);
		tileChooser.setStringListener(new StringListener(){

			@Override
			public void textEmitted(String text) {
				selectionListener.textEmitted(text);
			}
			
		});
		
		setLayout(new BorderLayout());
		
		//add(data, BorderLayout.NORTH);
		
		add(tileChooser, BorderLayout.CENTER);
		
		Dimension d = this.getPreferredSize();
		d.width = 300;
		setPreferredSize(d);
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
	
	public void setTilesetPath(String path) {
		this.tilesetPath = path;
	}
}
