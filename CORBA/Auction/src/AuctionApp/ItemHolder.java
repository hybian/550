package AuctionApp;

/**
* AuctionApp/ItemHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Auction.idl
* Friday, March 8, 2019 9:19:17 PM CST
*/

public final class ItemHolder implements org.omg.CORBA.portable.Streamable
{
  public AuctionApp.Item value = null;

  public ItemHolder ()
  {
  }

  public ItemHolder (AuctionApp.Item initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = AuctionApp.ItemHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    AuctionApp.ItemHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return AuctionApp.ItemHelper.type ();
  }

}
