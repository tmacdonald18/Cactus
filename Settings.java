/*
 * Settings.java
 * Author:	Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	An interactive settings display that allows the user to choose different options.
 * 			Allows user to add and select a layer.
 */

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Settings extends JPanel {
	
	//Sends messages from Settings to MainFrame
	private StringListener selectionListener;
	
	//Stores the current number of layers
	private int totalLayerCount;
	
	//JComboBox containing the list of layers
	//TODO: Change this to a JTable
	//private JComboBox<String> layerList;
	private JList<String> layerList;
	
	public Settings()
	/*
	 * This is a constructor
	 */
	{
		totalLayerCount = 0;
		
		//Initialize combo box
		//this.layerList = new JComboBox<String>(layers);
		final DefaultListModel<String> listModel = new DefaultListModel<String>();
		listModel.addElement("0");
		this.layerList = new JList<String>(listModel);
		layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		layerList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		layerList.setVisibleRowCount(-1);
		//listScroller.setPreferredSize(new Dimension(10, 80));
		
		layerList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectionListener.textEmitted("layerdecision," + layerList.getSelectedValue());
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
		
		//ActionListener that adds an item to the layer list if clicked
		//TODO: Change this to work with a table, and have there be an add button and a remove button that adds a new row or removes the highlighted row
		addLayer.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				totalLayerCount++;
				listModel.addElement(Integer.toString(totalLayerCount));
				selectionListener.textEmitted("addLayer");
			}
			
		});
		
		add(addLayer, gc);
	}
	
	public void setStringListener(StringListener listener) 
	/*
	 * Sets the selectionListener StringListener to listener
	 */
	{
		this.selectionListener = listener;
	}
}
