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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AliceFrame extends JFrame
{
//	private String accountId = "GCOCNLTLMTOWLR3SRG2537WA7RP5FIBSSPSMPYCK3RLUUKQHXOSQZQNJ";
//	private String seed = "SBX542QPZI4MIET4QCNGYTSVDIYBM6SUD5BBK6IY36ZO3OTO6AY55TIY";
	
	JButton ranBtn;
	JButton betBtn;
	JButton revBtn;
	JButton viwBtn;
	JButton queBtn;
	JTextArea logText;
	
	int ranNum = -1; // local stored, publish after reveal
	String md5 = ""; // local stored, publish after reveal
	
	BetSingleton bet;
	
	public AliceFrame(String title) 
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
		if(!bet.isAlicePlaying) 
		{
			logText.append("\nWelcome to the bet\n\n");
			logText.append("========================================\n");
			logText.append("You are playing as Alice\n");
			logText.append("Stellar Account id: " + bet.aliceId + "\n");
			logText.append("Stellar Secret Seed: " + bet.aliceSeed + "\n");
			logText.append("========================================\n");
			logText.append("Bet = 20 lumens\n");
			logText.append("You will win if (X+Y)%2 = 0\n");
			logText.append("You will get 38 lumens upon winning\n");
			logText.append("========================================\n\n");
			
			bet.isAlicePlaying = true;
		}
	}
	
	private void random() 
	{
		if(bet.isAlicePlaying && ranNum == -1) 
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
		if(bet.isAlicePlaying && ranNum != -1) 
		{
			if(bet.hasBetAlice == true) {
				logText.append("You already make the bet!\n");
			}
			else {
				bet.hasBetAlice = true;
				String memo = md5;
				String amount = "20";
				
				// make transaction in another thread
				Thread t = new Thread(new Runnable() 
				{
			         @Override
			         public void run() {
			        	 transaction(bet.aliceSeed, bet.bankerId, memo, amount);
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
		if(!bet.hasBetAlice) 
		{
			logText.append("Please bet first...\n");
		}
		else if(!bet.hasBetBob) 
		{
			logText.append("Waiting for Bob to bet...\n");
		}
		else if(!bet.hasRevAlice) 
		{
			bet.AliceNum = ranNum; // publish number
			bet.hasNumAlice = true;
			bet.AliceHash = md5; // publish hash	
			
			logText.append("\n========================================\n");
			logText.append("You revealed to Bob and Banker\n");
			logText.append("========================================\n");

			ranNum = -1; // reset after
			md5 = ""; // reset after
			bet.hasRevAlice = true;		
		}
		else
		{
			logText.append("You have already revealed!\n");
		}
	}
	
	private void viewReveal() 
	{
		if(bet.hasNumAlice && bet.hasBetAlice && bet.hasRevAlice && bet.hasRevBob) 
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
			logText.append("Your number: " + bet.AliceNum + "\n");
			logText.append("Your hash: " + bet.AliceHash + "\n\n");
			logText.append("Bob's number: " + bet.BobNum + "\n");
			logText.append("Bob's hash: " + bet.BobHash + "\n\n");
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
