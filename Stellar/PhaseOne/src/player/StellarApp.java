package player;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class StellarApp 
{

	public static void main(String[] args) 
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() 
			{	
				JFrame frame = new MainFrame("Main Menu --Stellar Project");
				frame.setSize(450, 300);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});

	}

}
