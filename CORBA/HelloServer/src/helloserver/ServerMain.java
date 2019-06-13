package helloserver;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import HelloApp.Hello;
import HelloApp.HelloHelper;

public class ServerMain 
{

	public static void main(String[] args)
	{
		try 
		{
			ORB orb = ORB.init(args, null);
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			
			HelloServant helloobj = new HelloServant();
			helloobj.setORB(orb);
			
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(helloobj);
			Hello href = HelloHelper.narrow(ref);
			
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
			NameComponent path[] = ncRef.to_name("ABC");
			ncRef.rebind(path, href);
			
			System.out.println("Hello Server ready and waiting...");
			
			for(;;) 
			{
				orb.run();
			}
			
		}
		catch(Exception e) 
		{
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

	}

}
