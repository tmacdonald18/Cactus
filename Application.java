import java.io.File;

import javax.swing.SwingUtilities;


public class Application {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				new MainFrame();
			}
			
		});
	}

}
