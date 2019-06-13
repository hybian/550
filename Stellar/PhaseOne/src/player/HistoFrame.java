package player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
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
import org.stellar.sdk.FormatException;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Operation;
import org.stellar.sdk.Transaction;


public class HistoFrame extends JFrame
{
	JTextArea textArea;
	JTextArea textAccount;
	JButton queryBtn;
	
	public HistoFrame(String title) 
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
	    
	    // init scrollable text
	 	JScrollPane scroll = new JScrollPane (textArea);
	 	scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	 	scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    
	    // set container
	    Container container = getContentPane();
	    container.add(subPanel, BorderLayout.NORTH);
		container.add(scroll, BorderLayout.CENTER);
		
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
		textArea.append("ID"+"\t"+"Succeful"+"\t"+"Src Account"+"\t"+"Dest Account"+"\t"+"Time"+"\t"+"MEMO"+"\t"+"Amount"+"\n\n");
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
				textArea.setText("HttpResponseCode: " + responsecode);
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
						textArea.append(counter + "\t" + id+"\t\t"+status+"\t\t"+source+"\t\t"+dest+"\t\t"+time+"\t"+"\t"+memo+"\t\t"+amount/10000000+"\n\n");
					}
					else {
						textArea.append(counter + "\told record " + time + "\n\n");
					}
				}
				else {
					textArea.append(counter + "\t" +"Create Account From Friendbot"+"\n\n");
				}
				counter++;
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
		} catch (ParseException e) {
			textArea.append("Error!" + "\n\n");
			textArea.append(": " + e.getMessage());
		} catch (RuntimeException e) {
			textArea.append("Error!" + "\n\n");
			textArea.append(": " + e.getMessage());
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
