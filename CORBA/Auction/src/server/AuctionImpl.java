package server;


import AuctionApp.AuctionPOA;
import AuctionApp.Item;

public class AuctionImpl extends AuctionPOA
{
	private boolean auctionEmpty = true;
	private Item currItem = null;
	private String highBidder = null;
	private double highPrice = 0;
	
	@Override
	public String offer_item(Item item) 
	{
		if(item.seller == null || item.itemDescr == null || item.iniPrice == null) 
		{
			return "\n-->Invalid item";
		}
		else if(!auctionEmpty)
		{
			return "\n-->Another item is currently selling...";
		}
		else 
		{
			currItem = item;
			auctionEmpty = false;
			highPrice = Double.valueOf(item.iniPrice);
			return "\n-->Item offered!";
		}
	}

	@Override
	public String sell(String userID)
	{
		
		if(currItem.seller==null || !userID.equals(currItem.seller)) 
		{
			return "\n-->You are NOT the seller!";
		}
		else if(highBidder == null) 
		{
			return "\n-->No bids yet at this moment...";
		}
		else 
		{
			String result = "\n-->ITEM SOLD to " + highBidder + " at PRICE: $" + highPrice;
			auctionEmpty = true;
			currItem = null;
			highBidder = null;
			highPrice = 0;
			return result;
		}
	}

	@Override
	public String bid(String userID, String price) 
	{
		if(currItem == null)
			return "\n-->No item at this moment!";
		
		if( userID.equals(currItem.seller) ) 
		{
			return "\n-->Seller CANNOT bid!";
		}
		else if( Double.valueOf(price) > highPrice )
		{
			highBidder = userID;
			highPrice = Double.valueOf(price);
			return "\n-->Bid Successfully! You are now the high bidder.";
		}
		else 
		{
			return "\n-->Bid unsuccessful, price must be higher than current high!";
		}
	}

	@Override
	public String view_high_bidder(String userID) 
	{
		if(highBidder == null) 
		{
			return "\n-->No bids yet at this moment...";
		}
		else if( userID.equals(currItem.seller) ) 
		{
			return "\n-->Current HIGH BIDDER: " + highBidder + ", at PRICE: $" + highPrice;
		}
		else 
		{
			return "\n-->You must be the seller to view this!";
		}
	}

	@Override
	public String view_bid_status(String userID) 
	{		
		if(!auctionEmpty)
		{
			if( userID.equals(highBidder) )
				return "\n-->You ARE the current high bidder of this item.";
			else
				return "\n-->You ARE NOT the current high bidder of this item.";
		}
		else 
		{
			return "\n-->No item at this moment!";
		}
	}

	@Override
	public String view_auction_status(String userID) 
	{
		StringBuffer sb = new StringBuffer("\n-->Auction Status\n");
		sb.append("    -active: " + !auctionEmpty + "\n");
		if(!auctionEmpty) 
		{
			sb.append("    -Item description: " + currItem.itemDescr + "\n");
			sb.append("    -Current item price: " + highPrice + "\n");
		}
		if(currItem != null && userID.equals(currItem.seller)) 
		{
			sb.append("    -Current high bidder: " + highBidder + "\n");
		}
		return sb.toString();
	}
}
