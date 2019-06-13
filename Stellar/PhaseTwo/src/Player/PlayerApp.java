package Player;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class PlayerApp 
{
	public static void main(String[] args) 
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() 
			{	
				JFrame aFrame = new AliceFrame("Alice --Stellar Project");
				aFrame.setSize(600, 600);
				aFrame.setLocationRelativeTo(null);
				aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				aFrame.setVisible(true);
				
				JFrame bFrame = new BobFrame("Bob --Stellar Project");
				bFrame.setSize(600, 600);
				bFrame.setLocationRelativeTo(null);
				bFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				bFrame.setVisible(true);
				
				JFrame xFrame = new BankerFrame("Banker --Stellar Project");
				xFrame.setSize(600, 600);
				xFrame.setLocationRelativeTo(null);
				xFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				xFrame.setVisible(true);
			}
		});
	}
}
