package player;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.FormatException;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TransFrame extends JFrame
{
	JTextArea srcAcctText;
	JTextArea srcSeedText;
	JTextArea destAcctText;
	JTextArea memoText;
	JTextArea amountText;
	JTextArea logText;
	JButton transBtn;
	
	public TransFrame(String title) 
	{
		// set title
		super(title);
		this.getContentPane().setLayout(new GridLayout(2, 1));
		
		srcAcctText = new JTextArea();
		srcSeedText = new JTextArea();
		destAcctText = new JTextArea();
		memoText = new JTextArea();
		amountText = new JTextArea();
		logText = new JTextArea();
		transBtn = new JButton("Make Transaction");
		
		// init subPanels
		JPanel subPanelNorth = new JPanel();
		JPanel subPanelNorthLeft = new JPanel();
		JPanel subPanelNorthRight = new JPanel();
		JPanel subPanelSouth = new JPanel();
		
		subPanelNorth.setLayout(new BorderLayout());
		subPanelNorthLeft.setLayout(new GridLayout(5, 1));
		subPanelNorthRight.setLayout(new GridLayout(5, 1));
		subPanelSouth.setLayout(new BorderLayout());
		
		// init labels
		JLabel srcAcctLabel = new JLabel("Srouce Account ID:");
		JLabel srcSeedLabel = new JLabel("Srouce Secret Seed:");
		JLabel destAcctLabel = new JLabel("Destination Account ID:   ");
		JLabel memoLabel = new JLabel("Memo:");
		JLabel amountLabel = new JLabel("Amount (lumen):");
		
		// subPanelNorth
		subPanelNorth.add(subPanelNorthLeft, BorderLayout.WEST);
		subPanelNorth.add(subPanelNorthRight, BorderLayout.CENTER);
		
		// subPanelNorthLeft
		subPanelNorthLeft.add(srcAcctLabel);
		subPanelNorthLeft.add(srcSeedLabel);
		subPanelNorthLeft.add(destAcctLabel);
		subPanelNorthLeft.add(memoLabel);
		subPanelNorthLeft.add(amountLabel);
		
		// subPanelNorthRight
		subPanelNorthRight.add(srcAcctText);
		subPanelNorthRight.add(srcSeedText);
		subPanelNorthRight.add(destAcctText);
		subPanelNorthRight.add(memoText);
		subPanelNorthRight.add(amountText);
		
		// subPanelSouth
		subPanelSouth.add(transBtn, BorderLayout.NORTH);
		subPanelSouth.add(logText, BorderLayout.CENTER);
		
		// set borders
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		subPanelNorth.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		subPanelSouth.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		srcAcctText.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		srcSeedText.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		destAcctText.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		memoText.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		amountText.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		logText.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		// set container
	    Container container = getContentPane();
	    container.add(subPanelNorth);
		container.add(subPanelSouth);
		
		// btn listener
		transBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				logText.setText("");
				String srcAcctStr = srcAcctText.getText();
				String srcSeedStr = srcSeedText.getText();
				String destAcctStr = destAcctText.getText();
				String memoStr = memoText.getText();
				String amountStr = amountText.getText();
				
				if(srcAcctStr.isEmpty()) {
					logText.setText("Empty Source Account ID!");
				}
				else if(srcSeedStr.isEmpty()) {
					logText.setText("Empty Source Secret Seed!");
				}
				else if(destAcctStr.isEmpty()) {
					logText.setText("Empty Destination Account ID!");
				}
				else if(memoStr.isEmpty()) {
					memoStr = "";
					logText.setText("Empty Memo.");
				}
				else if(amountStr.isEmpty()) {
					logText.setText("Empty Amount!");
				}
				else {
					transaction(srcAcctStr, srcSeedStr, destAcctStr, memoStr, amountStr);
				}
			}
		});
	}
	
	private void transaction(String srcAcctStr, String srcSeedStr, String destAcctStr, String memoStr, String amountStr) 
	{
		// First, check to make sure that the destination account exists.
		// You could skip this, but if the account does not exist, you will be charged
		// the transaction fee when the transaction fails.
		// It will throw HttpResponseException if account does not exist or there was another error.
		try 
		{
			Network.useTestNetwork();
			Server server = new Server("https://horizon-testnet.stellar.org");

			KeyPair source = KeyPair.fromSecretSeed(srcSeedStr);
			KeyPair destination = KeyPair.fromAccountId(destAcctStr);
			
			server.accounts().account(destination);
			
			// If there was no error, load up-to-date information on your account.
			AccountResponse sourceAccount = server.accounts().account(source);
			
			// Start building the transaction.
			Transaction transaction = new Transaction.Builder(sourceAccount)
			        .addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), amountStr).build())
			        // A memo allows you to add your own metadata to a transaction. It's
			        // optional and does not affect how Stellar treats the transaction.
			        .addMemo(Memo.text(memoStr))
			        .setTimeout(60)
			        .build();
			
			// Sign the transaction to prove you are actually the person sending it.
			transaction.sign(source);
			
			// And finally, send it off to Stellar!
			SubmitTransactionResponse response = server.submitTransaction(transaction);
			logText.append("Success!\n\n");
			logText.append(response.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			logText.append("Error!" + "\n\n");
			logText.append(": " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logText.append("Error!" + "\n\n");
			logText.append(": " + e.getMessage());
		} catch (Exception e) {
			logText.append("Something went wrong!" + "\n\n\t:" + e.getMessage());
			  // If the result is unknown (no response body, timeout etc.) we simply resubmit
			  // already built transaction:
			  // SubmitTransactionResponse response = server.submitTransaction(transaction);
		}
	}
}
