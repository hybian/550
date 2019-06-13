package player;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import org.stellar.sdk.FormatException;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

public class QueryFrame extends JFrame
{
	JTextArea textArea;
	JTextArea textAccount;
	JButton queryBtn;
	
	public QueryFrame(String title) 
	{
		// set title
		super(title);	
		getContentPane().setLayout(new BorderLayout());
		
		// init text & btn
		textArea = new JTextArea();
		textArea.setEditable(false);
		textAccount = new JTextArea();
		queryBtn = new JButton("Query Now");
		
		// set borders
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		textAccount.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		textArea.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		// set panel
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BorderLayout());
	    subPanel.add(textAccount, BorderLayout.CENTER);
	    subPanel.add(queryBtn, BorderLayout.EAST);
	    
	    // set container
	    Container container = getContentPane();
	    container.add(subPanel, BorderLayout.NORTH);
		container.add(textArea, BorderLayout.CENTER);
		
		// btn listener
		queryBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				textArea.setText("");
				String accountID = textAccount.getText();
				if(accountID.isEmpty()) {
					textArea.setText("Empty Account ID!");
				}
				else {
					query(accountID);
				}
			}
		});
	}
	
	
	private void query(String accountID) 
	{
		try 
		{
			KeyPair pair = KeyPair.fromAccountId(accountID);
			Server server = new Server("https://horizon-testnet.stellar.org");
			AccountResponse account = null;
			account = server.accounts().account(pair);
			textArea.append("\n");
			textArea.append("Balances for account " + pair.getAccountId() + "\n\n");
			for (AccountResponse.Balance balance : account.getBalances()) 
			{
				textArea.append(String.format(
					    "Type: %s, Code: %s, Balance: %s",
					    balance.getAssetType(),
					    balance.getAssetCode(),
					    balance.getBalance()));
			}
		} catch (IOException e) {
			textArea.append("Error!" + "\n\n");
			textArea.append(": " + e.getMessage());
		} catch (FormatException e) {
			textArea.append("Error!" + "\n\n");
			textArea.append(": " + e.getMessage());
		} catch (IllegalArgumentException e) {
			textArea.append("Error!" + "\n\n");
			textArea.append(": " + e.getMessage());
		}
	}
}











