import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class Application {
	
	public static void main(String[] args) {
		
		//Main Program frame
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				new MainFrame();
			}
			
		});
	}

}
