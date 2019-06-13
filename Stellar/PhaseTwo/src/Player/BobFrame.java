package Player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
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

public class BobFrame extends JFrame
{
//	private String accountId = "GBGB6S7PDWKRZXAGTBNNRQQ74STHARGWQ63QJKGGOTMGB4S7ICJ7XQTY";
//	private String seed = "SCKQEPDYBPMEQE55RNYORCLFHI4CZ46EXQWGBXFWPVSZ5M3SXOWMJIB5";
	
	JButton ranBtn;
	JButton betBtn;
	JButton revBtn;
	JButton viwBtn;
	JButton queBtn;
	JTextArea logText;
	
	int ranNum = -1;
	String md5 = "";
	
	BetSingleton bet;
	
	public BobFrame(String title) 
	{
		super(title);
		this.getContentPane().setLayout(new BorderLayout());
		
		// init subPanels
		JPanel subPanelWest = new JPanel();
		JPanel subPanelCenter = new JPanel();
		
		subPanelWest.setLayout(new GridLayout(5, 1));
		subPanelCenter.setLayout(new BorderLayout());
		
		ranBtn = new JButton("Generate Random Number");
		betBtn = new JButton("BET");
		revBtn = new JButton("Reveal");
		viwBtn = new JButton("View Reveal");
		queBtn = new JButton("View Transactions");
		logText = new JTextArea();
		logText.setEditable(false);
		
		// init scrollable text
		JScrollPane scroll = new JScrollPane (logText);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		subPanelWest.add(ranBtn);
		subPanelWest.add(betBtn);
		subPanelWest.add(revBtn);
		subPanelWest.add(viwBtn);
		subPanelWest.add(queBtn);
		subPanelCenter.add(scroll, BorderLayout.CENTER);
		
		// set borders
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		subPanelWest.setBorder(BorderFactory.createCompoundBorder(border,
		           BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		subPanelCenter.setBorder(BorderFactory.createCompoundBorder(border,
		           BorderFactory.createEmptyBorder(10, 10, 10, 10)));
				
		// set container
	    Container container = getContentPane();
	    container.add(subPanelWest, BorderLayout.WEST);
		container.add(subPanelCenter, BorderLayout.CENTER);
		
		ranBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				random();
			}
		});
		
		betBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				bet();
			}
		});
		
		revBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				reveal();
			}
		});
		
		viwBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				viewReveal();
			}
		});
		
		queBtn.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				viewTransactions();
			}
		});

		
		bet = BetSingleton.getBet();
		start();
		
		
		
		
	}
	
	private void start()
	{
		if(!bet.isBobPlaying) 
		{
			logText.append("\nWelcome to the bet\n\n");
			logText.append("========================================\n");
			logText.append("You are playing as Bob\n");
			logText.append("Stellar Account id: " + bet.bobId + "\n");
			logText.append("Stellar Secret Seed: " + bet.bobSeed + "\n");
			logText.append("========================================\n");
			logText.append("Bet = 20 lumens\n");
			logText.append("You will win if (X+Y)%2 = 1\n");
			logText.append("You will get 38 lumens upon winning\n");
			logText.append("========================================\n\n");
			
			bet.isBobPlaying = true;
		}
	}
	
	private void random() 
	{
		if(bet.isBobPlaying && ranNum == -1) 
		{
			// Obtain a number between [0 - 49].
			Random rand = new Random();
			ranNum = rand.nextInt(50);
			md5 = getMD5(String.valueOf(ranNum));
			// Append to log
			logText.append("\n========================================\n");
			logText.append("Your random number is: " + ranNum + "\n");
			logText.append("Your MD5 value is: " + md5 + "\n");
			logText.append("========================================\n");
		}
		else 
		{
			logText.append("You can't do it again!\n");
		}
	}
	
	private String getMD5(String string) // 28 bytes
	{
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			logText.append(e.getMessage() + "\n");
		}
		md5.update(StandardCharsets.UTF_8.encode(string));
		String md5_str = String.format("%032x", new BigInteger(1, md5.digest()));
		String return_str = md5_str.substring(0, 27);
		return return_str;
	}
	
	private void bet() 
	{
		if(bet.isBobPlaying && ranNum != -1) 
		{
			if(bet.hasBetBob == true) {
				logText.append("You already make the bet!\n");
			}
			else {
				bet.hasBetBob = true;
				String memo = md5;
				String amount = "20";

				// make transaction in another thread
				Thread t = new Thread(new Runnable() 
				{
			         @Override
			         public void run() {
			        	 transaction(bet.bobSeed, bet.bankerId, memo, amount);
			         }
				});
				t.start();
			}
		}
		else 
		{
			logText.append("Generate Random Number First!\n");
		}
	}
	
	
	private void reveal() 
	{
		if(!bet.hasBetBob) 
		{
			logText.append("Please bet first...\n");
		}
		else if(!bet.hasBetAlice) 
		{
			logText.append("Waiting for Alice to bet...\n");
		}
		else if(!bet.hasRevBob) 
		{
			bet.BobNum = ranNum; // publish number
			bet.hasNumBob = true;
			bet.BobHash = md5; // publish hash
			
			logText.append("\n========================================\n");
			logText.append("You revealed to Alice and Banker\n");
			logText.append("========================================\n");
			
			ranNum = -1; // reset after
			md5 = ""; // reset after
			bet.hasRevBob = true;
		}
		else 
		{
			logText.append("You have already revealed!\n");
		}
	}
	
	private void viewReveal() 
	{
		if(bet.hasNumBob && bet.hasBetBob && bet.hasRevBob && bet.hasRevAlice) 
		{
			int result = (bet.AliceNum + bet.BobNum) % 2;
			String winner="";
			if(result == 0) {
				winner = "Alice";
			}
			else {
				winner = "Bob";
			}
			
			logText.append("\n========================================\n");
			logText.append("Your number: " + bet.BobNum + "\n");
			logText.append("Your hash: " + bet.BobHash + "\n\n");
			logText.append("Alice's number: " + bet.AliceNum + "\n");
			logText.append("Alice's hash: " + bet.AliceHash + "\n\n");
			logText.append("(X+Y)%2 == " + result + "\n");
			logText.append("Winner: " + winner + "\n");
			logText.append("Banker will pay ASAP\n");
			logText.append("========================================\n");
		}
		else 
		{
			logText.append("Can't view yet\n");
		}
	}
	
	private void viewTransactions() 
	{
		logText.append("\n========================================\n");
		if(bet.history != null) 
		{
			for(int i=0; i<bet.history.size(); i++) 
			{
				logText.append(bet.history.get(i));
			}
		}
		logText.append("========================================\n");
	}
	
	private void transaction(String srcSeedStr, String destAcctStr, String memoStr, String amountStr) 
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
			        .setOperationFee(100)
			        .build();
			
			// Sign the transaction to prove you are actually the person sending it.
			transaction.sign(source);
			
			// And finally, send it off to Stellar!
			SubmitTransactionResponse response = server.submitTransaction(transaction);

			logText.append("========================================\n");
			logText.append("Bet success!\n");
			logText.append("Num:"+ranNum+"\n");
			logText.append("MD5:"+md5+"\n");
			logText.append("Amount: 20 lumens\n");
			logText.append("========================================\n\n");
			
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
		}
	}
}
