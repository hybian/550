package helloserver;

import org.omg.CORBA.ORB;

import HelloApp.HelloPOA;

public class HelloServant extends HelloPOA
{
	private String message = "Hello World!\n";
	private ORB orb;
	
	public void setORB(ORB orb_val) 
	{
		orb = orb_val;
	}
	
	public String hellomessage()
	{
		return message;
	}
	
	public void hellomessage(String newMessage) 
	{
		message = newMessage;
	}
	
}
