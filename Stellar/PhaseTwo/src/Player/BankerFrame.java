package Player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.FormatException;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.Operation;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

public class BankerFrame extends JFrame
{
	BetSingleton bet;
	int UPDATE_RATE = 5;
	int TIME_OUT = 60;
	boolean isTimeOut = false;
	JTextArea logText;
	

	public BankerFrame(String title) 
	{
		super(title);
		this.getContentPane().setLayout(new BorderLayout());
		
		
		logText = new JTextArea();
		logText.setEditable(false);
		
		// init scrollable text
		JScrollPane scroll = new JScrollPane (logText);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// set borders
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		logText.setBorder(BorderFactory.createCompoundBorder(border,
		           BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		// set container
	    Container container = getContentPane();
	    container.add(scroll, BorderLayout.CENTER);
				
		
	    // start program
	    bet = BetSingleton.getBet();
	    start();
		
	}
	
	private void start() 
	{
		// update info in another thread
		Thread t = new Thread(new Runnable() 
		{
			@Override
		    public void run() 
			{
				// for timeout
				int total_waiting_time = 0;
				
				// wait for both to reveal
				while( !(bet.hasRevAlice && bet.hasRevBob) ) 
				{
					// get transaction history
					query(bet.bankerId);
					
					// wait UPDATE_RATE time
					try 
					{
						// timeout, refund
						if(total_waiting_time > TIME_OUT) 
						{
							logText.setText("TIME OUT\n");
							if(!bet.hasBetAlice && bet.hasBetBob) //refund to bob
							{
								logText.setText("REFUND TO BOB\n");
								transaction(bet.bankerSeed, bet.bobId, "TO REFUND", "20");
							}
							else if(!bet.hasBetBob && bet.hasBetAlice) //refund to alice
							{
								logText.setText("REFUND TO ALICE\n");
								transaction(bet.bankerSeed, bet.aliceId, "TO REFUND", "20");
							}
							else if(bet.hasBetBob && bet.hasBetAlice) //refund to both
							{
								logText.setText("REFUND TO BOTH\n");
								transaction(bet.bankerSeed, bet.bobId, "TO REFUND", "20");
								transaction(bet.bankerSeed, bet.aliceId, "TO REFUND", "20");
							}
							else 
							{
								logText.setText("REFUND TO NONE\n");
							}
							isTimeOut = true;
							break;
						}
						
						TimeUnit.SECONDS.sleep(UPDATE_RATE); // update in secs
						logText.setText(""); // clear screen
						total_waiting_time += UPDATE_RATE; // update timeout
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}	 
				
				// call to end the game with payments
				if(!isTimeOut) {
					endGame();	
				}
				else {
					isTimeOut = false;
				}
				
				// reset game after 30 secs
				try {
					int counter = 30;
					while(counter >= 0) 
					{
						logText.append("\n\nGame reset in " + counter + " secs\n");
						TimeUnit.SECONDS.sleep(1);
						counter--;
					}
					// reset everything
					bet.reset();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		// start thread
		t.start();
	}
	
	private void endGame() 
	{
		logText.setText("BOTH REVEALED\n");
		
		// get info
		int alice_num = bet.AliceNum;
		int bob_num = bet.BobNum;
		String alice_hash = bet.AliceHash;
		String bob_hash = bet.BobHash;
		
		// check if pairs match
		String cal_alice = bet.getMD5(String.valueOf(alice_num));
		String cal_bob = bet.getMD5(String.valueOf(bob_num));
		
		boolean isGameVaild = false;
		if( alice_hash.equals(cal_alice) && bob_hash.equals(cal_bob) ) 
		{
			isGameVaild = true;
		}
		
		// pay if game is vaild
		if( isGameVaild ) 
		{
			int result = (alice_num + bob_num) % 2;
			if(result == 0) // alice wins
			{
				bet.winner = "Alice";
				transaction(bet.bankerSeed, bet.aliceId, "Alice wins the bet", "38");
			}
			else // bob wins
			{
				bet.winner = "Bob";
				transaction(bet.bankerSeed, bet.bobId, "Bob wins the bet", "38");
			}
		}
		else{
			logText.setText("invalid game\n");
		}
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
			logText.append("Winner: " + bet.winner + "\n");
			logText.append("Pay success!\n");
			logText.append("Amount: 38 lumens\n");
			logText.append("Game restart in 30 secs\n");
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
	
	private void query(String accountID) 
	{
		try 
		{
			String uri_str = "https://horizon-testnet.stellar.org/accounts/" + accountID + "/transactions";
			URL url = new URL(uri_str);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
			conn.setRequestMethod("GET");
			conn.connect();
			int responsecode = conn.getResponseCode(); 
			
			String inline="";
			if(responsecode != 200)
				logText.setText("HttpResponseCode: " + responsecode);
			else
			{
				Scanner sc = new Scanner(url.openStream());
				while(sc.hasNext()){
					inline+=sc.nextLine();
				}
				sc.close();
			}
			
			JSONParser parse = new JSONParser();
			JSONObject jobj = (JSONObject)parse.parse(inline);
			JSONObject embedded = (JSONObject) jobj.get("_embedded");
			JSONArray records = (JSONArray) embedded.get("records");
			Iterator<?> i = records.iterator();
						
			int counter = 0;
			while (i.hasNext()) 
			{
				JSONObject record = (JSONObject) i.next();
				String id, source, time, envelop_xdr, memo;
				String dest = "";
				long amount;
				
				boolean status = (Boolean)record.get("successful");
				id = (String)record.get("id");
				source = (String)record.get("source_account");
				time = toCDT( (String)record.get("created_at") );
				envelop_xdr = (String)record.get("envelope_xdr");				
				memo = (String)record.get("memo");
				
				if(counter != 0) 
				{	
					// Envelop + dest
					Transaction t = Transaction.fromEnvelopeXdr(envelop_xdr);
					Operation[] op = t.getOperations();
					org.stellar.sdk.xdr.Operation pop = op[0].toXdr();	
					KeyPair p = KeyPair.fromXdrPublicKey(pop.getBody().getPaymentOp().getDestination().getAccountID());
					
					dest = p.getAccountId();
					amount = pop.getBody().getPaymentOp().getAmount().getInt64();
					
					if( isToday(time) ) {
						String record_str = counter + "  " + id+"  "+status+"  "+source+"  "+dest+"  "+time+"  "+"  "+memo+"  "+amount/10000000+"\n";
						bet.history.add(record_str);
						logText.append(record_str);
					}
					else {
						logText.append(counter + "\t* old record\n");
					}
				}
				else {
					logText.append(counter + "  " +"Create Account From Friendbot"+"\n");
				}
				counter++;
			}
		} catch (IOException e) {
			logText.append("Error!" + "\n\n");
			logText.append(": " + e.getMessage());
		} catch (FormatException e) {
			logText.append("Error!" + "\n\n");
			logText.append(": " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logText.append("Error!" + "\n\n");
			logText.append(": " + e.getMessage());
		} catch (ParseException e) {
			logText.append("Error!" + "\n\n");
			logText.append(": " + e.getMessage());
		} catch (RuntimeException e) {
			logText.append("Error!" + "\n\n");
			logText.append(": " + e.getMessage());
		}
	}
	
	private String toCDT(String UTC) 
	{
		// convert UTC to CDT
		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = null;
		try {
			date = utcFormat.parse(UTC);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		DateFormat cdtFormat = new SimpleDateFormat("yyyy-MM-dd");
		cdtFormat.setTimeZone(TimeZone.getTimeZone("CDT"));
		
		return cdtFormat.format(date);
	}
	
	private boolean isToday(String time) 
	{
		// get current time in yyyy-MM-dd
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		String curr_time = dtf.format(localDate);
		
		// check if it's today
		if( curr_time.equals(time) )
			return true;
		else
			return false;
	}

}
