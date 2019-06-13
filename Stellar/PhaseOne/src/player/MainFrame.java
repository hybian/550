package player;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MainFrame extends JFrame
{
	JButton creatBtn;
	JButton queryBtn;
	JButton transBtn;
	JButton histoBtn;
	
	
	public MainFrame(String title) 
	{
		super(title);
		
		creatBtn = new JButton("Create New Key Pair");
		queryBtn = new JButton("Query Account ID");
		transBtn = new JButton("Make Transaction");
		histoBtn = new JButton("View Transactions");
		
		Container container = getContentPane();
		container.setLayout(new GridLayout(4, 1));
		container.add(creatBtn);
		container.add(queryBtn);
		container.add(transBtn);
		container.add(histoBtn);
		
		creatBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				CreateFrame cf = new CreateFrame("Create new key pair");
				cf.setSize(700, 500);
				cf.setLocationRelativeTo(null);
				cf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				cf.setVisible(true);
			}
		});
		
		queryBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				QueryFrame qf = new QueryFrame("Query account id");
				qf.setSize(700, 300);
				qf.setLocationRelativeTo(null);
				qf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				qf.setVisible(true);
			}
		});
		
		transBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				TransFrame tf = new TransFrame("Make Transaction");
				tf.setSize(800, 500);
				tf.setLocationRelativeTo(null);
				tf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				tf.setVisible(true);
			}
		});
		
		histoBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				HistoFrame hf = new HistoFrame("View Transactions");
				hf.setSize(1000, 700);
				hf.setLocationRelativeTo(null);
				hf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				hf.setVisible(true);
			}
		});

	}
}
