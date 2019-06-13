package player;

import org.stellar.sdk.KeyPair;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import java.awt.Color;

public class CreateFrame extends JFrame
{
	JTextArea textArea;
	
	public CreateFrame(String title) 
	{
		// set title
		super(title);	
		
		// init textArea
		textArea = new JTextArea();
		textArea.setEditable ( false );
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		textArea.setBorder(BorderFactory.createCompoundBorder(border,
	            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		// init scrollable text
		JScrollPane scroll = new JScrollPane (textArea);
	    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
	    // set up container
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(scroll, BorderLayout.CENTER);
		
		// call to create new key pair
		createNewKeyPair();
	}
	
	private void createNewKeyPair() 
	{
		KeyPair pair = KeyPair.random();
		String sSeed = new String(pair.getSecretSeed());
		String accountID = pair.getAccountId();
		String friendbotUrl = String.format("https://friendbot.stellar.org/?addr=%s", pair.getAccountId());
		InputStream response = null;
		String body = "";
		try {
			response = new URL(friendbotUrl).openStream();
			textArea.append("Account ID: " + accountID + "\nSecret Seed: " + sSeed +"\n\n\n");
			body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
			textArea.append("SUCCESS! You have a new account :)\n" + body);
		} catch (MalformedURLException e) {
			textArea.setText(e.getMessage());
		} catch (IOException e) {
			textArea.setText(e.getMessage());
		}
	}
}
