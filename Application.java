/*
 * Application.java
 * Author:  Tyler MacDonald
 * Email:	tcmacd18@g.holycross.edu
 * Purpose:	Initialize MainFrame and run the program in a SwingUtilities thread
 * Post:	Program is started and message box prompts appear
 */

import javax.swing.SwingUtilities;


public class Application {
	
	public static void main(String[] args) {
		
		//Run MainFrame in a thread, separates the main function from the frame functionality
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				new MainFrame();
			}
			
		});
	}

}
