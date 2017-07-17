import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Settings extends JPanel {
	
	private StringListener selectionListener;
	private int counter;
	private JComboBox layerList;
	
	public Settings()
	/*
	 * This is a constructor
	 */
	{
		counter = 0;
		String[] layers = {"0"};
		
		this.layerList = new JComboBox(layers);
		//layerList.setSelectedIndex(0);
		layerList.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JComboBox cb = (JComboBox)e.getSource();
					selectionListener.textEmitted("layerdecision," + (String)cb.getSelectedItem());
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
			
		});
		
		
		JLabel layerSelection = new JLabel("Select a layer: ");
		layerSelection.setFont(new Font("Arial", Font.BOLD, 14));
		
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.gridx = 1;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		
		add(layerList, gc);
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		
		add(layerSelection, gc);
		
		gc.gridx = 0;
		gc.gridy = 1;
		
		JButton addLayer = new JButton("Add Layer");
		addLayer.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				counter++;
				layerList.addItem(Integer.toString(counter));
			}
			
		});
		
		add(addLayer, gc);
		
		//this.setBackground(new Color(179, 255, 179));

	}
	
	public void setStringListener(StringListener listener) {
		this.selectionListener = listener;
	}
}
