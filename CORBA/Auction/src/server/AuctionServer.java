package server;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import AuctionApp.Auction;
import AuctionApp.AuctionHelper;

import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;

public class AuctionServer 
{

    public static void main(String args[]) throws Exception 
    {
    	// create and initialize the ORB
    	ORB orb = ORB.init(args, null);
        System.out.println("server--->initialized");

        // get reference to rootPOA & activate the POAManager
        org.omg.CORBA.Object obj=orb.resolve_initial_references("RootPOA");
        POA rootpoa = POAHelper.narrow(obj);
        rootpoa.the_POAManager().activate();
        System.out.println("server--->POAManager activated");

        // create servant and register it with the ORB
        AuctionImpl auctionImpl = new AuctionImpl();
        System.out.println("server--->AuctionImpl created");

        // get object reference from the servant
        org.omg.CORBA.Object ref = rootpoa.servant_to_reference(auctionImpl);
        Auction href = AuctionHelper.narrow(ref);
        System.out.println("server--->Got object reference from the servant");
        
        // get the root naming context. NameService invokes the name service
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        System.out.println("server--->Got the root naming context");
        
        // Use NamingContextExt which is part of the Interoperable      
        // Naming Service (INS) specification.
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
        System.out.println("server--->NamingContextExt");

        // bind the Object Reference in Naming
        String name = "Auction";
        NameComponent path[] = ncRef.to_name(name);
        ncRef.rebind(path, href);
        System.out.println("server--->Object reference in naming bound");
        
        // wait for invocations from clients
        System.out.println("\n\nAuctionServer ready and waiting ...");
        orb.run();
    }
	
}
