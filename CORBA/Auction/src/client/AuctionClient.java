package client;

import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import AuctionApp.Auction;
import AuctionApp.AuctionHelper;
import AuctionApp.Item;


public class AuctionClient 
{

	static Auction auctionImpl;
	static Scanner reader;

    public static void main(String args[])    
    {      
    	try
    	{  
    		// create and initialize the ORB       
    		ORB orb = ORB.init(args, null);
    		
    		// get the root naming context        
    		org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");  
    		
    		// Use NamingContextExt instead of NamingContext. This is         
    		// part of the Interoperable naming Service.          
    		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef); 
    		
    		// resolve the Object Reference in Naming        
    		String name = "Auction";        
    		auctionImpl = AuctionHelper.narrow(ncRef.resolve_str(name));        
    		System.out.println("\nclient--->Successfully connected to the server!\n");    
    		
    		// start client service
    		reader = new Scanner(System.in);
    		start();
    		reader.close();
    	} 
    	catch (Exception e)
    	{  
    		System.out.println("ERROR : " + e) ;  
    		e.printStackTrace(System.out);  
    	}    
    }
    
    /* ---------------------start method--------------------- */
    private static void start() 
    {
    	String header = "=================================\n"
    				  + "|                               |\n"
    			      + "|     Welcom to the auction!    |\n"
    			      + "|                               |\n"
    			      + "=================================\n";
    	System.out.println(header + "\n\n");
    	
    	System.out.println("Seller: enter 1");
    	System.out.println("Bidder: enter 2");
    	int role = reader.nextInt(); 
    	reader.nextLine();
    	
    	if(role == 1) 
    	{
    		while(true)
    			start_seller();
    	}
    	else if(role == 2) 
    	{
    		while(true)
    			start_bidder();
    	}
    	else 
    	{
    		System.out.println("Invaild role");
    	}
    }
    
    /* ---------------------Seller start method--------------------- */
    private static void start_seller() 
    {
    	String sellerMenu = "=================================\n"
    					  + "|     1.offer item              |\n"
    					  + "|     2.view high bidder        |\n"
    					  + "|     3.sell                    |\n"
    					  + "|     4.view auction status     |\n"
    					  + "|     5.exit                    |\n"
    					  + "=================================\n";
    	System.out.println("\n\n" + sellerMenu);
    	
    	int action = reader.nextInt(); 
    	reader.nextLine();
    	
    	switch(action)
    	{
    		case 1 :
    			offer_item();
    			break; 
    		case 2 :
    			view_high_bidder();
    			break; 
    		case 3 :
    			sell();
    			break; 
    		case 4 :
    			view_auction_status();
    			break; 
    		case 5 :
    			System.exit(0); 
    		default : 
    			System.out.println("Invalid Option!");
    	}		
    }
    
    /* ---------------------Seller bidder method--------------------- */
    private static void start_bidder() 
    {
    	String bidderMenu = "=================================\n"
    					  + "|     1.view auction status     |\n"
    					  + "|     2.bid                     |\n"
    					  + "|     3.view bid status         |\n"
    					  + "|     4.exit                    |\n"
    					  + "=================================\n";
    	System.out.println("\n\n" + bidderMenu);
    	
    	int action = reader.nextInt(); 
    	reader.nextLine();
    	
    	switch(action)
    	{
    		case 1 :
    			view_auction_status();
    			break; 
    		case 2 :
    			bid();
    			break; 
    		case 3 :
    			view_bid_status();
    			break; 
    		case 4 :
    			System.exit(0); 
    		default : 
    			System.out.println("Invalid Option!");
    	}		
    }
    
    /* ---------------------Seller helper methods--------------------- */
    private static void offer_item() 
    {   	
    	System.out.println("Please Enter Your ID: ");
    	String sellerID = reader.next();
    	reader.nextLine();
    	
    	System.out.println("Please Enter Item Description: ");
    	String itemDescr = reader.nextLine();
    	
    	System.out.println("Please Enter Initial Price: ");
    	String iniPrice = reader.next();
    	reader.nextLine();
    	
		Item item = new Item(sellerID, itemDescr, iniPrice);
		
		long start = System.nanoTime();    
		System.out.println(auctionImpl.offer_item(item));
		long elapsedTime = System.nanoTime() - start;
		System.out.println("%%elapsedTime: " + elapsedTime);
    }
    
    private static void view_high_bidder() 
    {
    	System.out.println("Please Enter Your ID: ");
    	String userID = reader.next();
    	reader.nextLine();
    	
    	long start = System.nanoTime();    
    	System.out.println(auctionImpl.view_high_bidder(userID));
    	long elapsedTime = System.nanoTime() - start;
		System.out.println("%%elapsedTime: " + elapsedTime);
    }
    private static void sell() 
    {
    	System.out.println("Please Enter Your ID: ");
    	String userID = reader.next();
    	reader.nextLine();
    	
    	long start = System.nanoTime();  
    	System.out.println(auctionImpl.sell(userID));
    	long elapsedTime = System.nanoTime() - start;
		System.out.println("%%elapsedTime: " + elapsedTime);
    }
    private static void view_auction_status() 
    {
    	System.out.println("Please Enter Your ID: ");
    	String userID = reader.next();
    	reader.nextLine();
    	
    	long start = System.nanoTime();  
    	System.out.println(auctionImpl.view_auction_status(userID));
    	long elapsedTime = System.nanoTime() - start;
		System.out.println("%%elapsedTime: " + elapsedTime);
    }
    
    /* ---------------------Bidder helper methods--------------------- */
    private static void bid() 
    {
    	System.out.println("Please Enter Your ID: ");
    	String userID = reader.next();
    	reader.nextLine();
    	
    	System.out.println("Please Enter Bidding Price: ");
    	String bidPrice = reader.next();
    	reader.nextLine();
    	
    	long start = System.nanoTime();  
    	System.out.println(auctionImpl.bid(userID, bidPrice));
    	long elapsedTime = System.nanoTime() - start;
		System.out.println("%%elapsedTime: " + elapsedTime);
    }
    
    private static void view_bid_status() 
    {
    	System.out.println("Please Enter Your ID: ");
    	String userID = reader.next();
    	reader.nextLine();
    	
    	long start = System.nanoTime();  
    	System.out.println(auctionImpl.view_bid_status(userID));
    	long elapsedTime = System.nanoTime() - start;
		System.out.println("%%elapsedTime: " + elapsedTime);
    }
}
