import java.net.*;	
import java.util.*; //DR
import java.io.File; //DR
import java.io.FileNotFoundException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
	
public class Server implements DNSlookup {
	
    public Server() {}

    public DNSreply lookup(String hostname) {
    	
    
     // hostname passed by client-stored in this variable 
    String hostname23 = null;
    String hostNameX = null;
    DNSreply IPAddr = null;
    //String[] parts = new String[3];
    
    //	To store answer
        String IP = null;
        ArrayList<DNSreply> list = new ArrayList<DNSreply>();
    try {	    
    	//array list to store file contents of Layer1/2.txt
    Scanner layer = new Scanner(new File(fName));
    int x = 0;
    while (layer.hasNext())
    {
    	
        String tempHost = layer.next();
        String tempAddr = layer.next();
    	list.add(new DNSreply(tempAddr, tempHost));
    	
        //printing on console
   //     System.out.println(list.get(x).address+"  "+list.get(x).hostname);
        x++;
        
   	}
   	layer.close();    	
    }
    catch (Exception e)
    {
    	 System.out.println("\n Exception!");
    }
   

    // Split the hostname to three parts
    String [] parts = hostname.split("[.]");
    switch(flag)
    {
        
    case 1:
    // Iterative DNS	
       	
   	switch (serverID)
   	{
   	case 1:
   	//For Layer 1	
    hostNameX = parts[2];
    hostname23 = parts[0]+"."+parts[1];
   	
   	/*LUT: checkLookUp() returns the IP address corresponding to the hostname 
   	  entry resolved using info in list */
   	
   	IP = checkLookUp(list, hostNameX ); 
   	 
   	IPAddr = new DNSreply(IP, hostname23); //Returning hostname without domain+IP address
   	
   	break;
   	
   	case 2:
   	    hostname23 = parts[0]+"."+parts[1];
   	   	
   	   	/*LUT: checkLookUp() returns the IP address corresponding to the hostname 
   	   	  entry resolved using info in list */
   	   	
   	   	IP = checkLookUp(list, hostname23 ); 
   	   	 
   	   	IPAddr = new DNSreply(IP, hostname23); //Returning hostname without domain+IP address	
   	
   	   	break;
   
   	   	default:
   	   	   	System.out.println("\nIncorrect Command! ");
   	   	break;
   	} //End of switch serverID case
   	
   	break;
   	
    case 2:
    	//Code for recursive
   switch(serverID)
   {
   case 1:
   
      	hostNameX = parts[2];
        hostname23 = parts[0]+parts[1];
       	  	
       	       	
      	//check cache first
      	if (cache.size() != 0)
      	{
       	   IP = checkLookUp(cache, hostname); 
      	} 
      	if (IP != null)
      	{
      		IPAddr = new DNSreply(IP, hostname23);
      		return IPAddr;
      	}
      	
      	if (IP == null)
      	{
	IP = checkLookUp(list, hostNameX);
try {
       	
    	    Registry registry = LocateRegistry.getRegistry(IP, port); //Mentioning the go to server
    	    DNSlookup stub = (DNSlookup) registry.lookup("Server2");
    	    DNSreply response = stub.lookup(hostname);//chk argument
    //	    IP = response.address;
    	 //   System.out.println("response addr: " + response.address);
    	    
    	 // Add to cache
    	    if(response==null)
    	    {
    	    	System.out.println("problem from server 2 return");
    	    }
    	    else{
    	    	cache.add(new DNSreply(response.address,hostname));
                //return response;
    	    	return(new DNSreply(response.address,hostname));
    	    }
    	} catch (Exception e) {
    	    System.err.println("\nCall to second server failed!");
       	}
      	} //end if
       	
       	//IPAddr = new DNSreply(IP, hostname); //Returning hostname + IP address	
      	 
      	
   	break;
   	   
  	case 2:
             	  	   	
   	                   
   	    hostname23 = parts[0]+"."+parts[1];
   	   	
   	   	/*LUT: checkLookUp() returns the IP address corresponding to the hostname 
   	   	  entry resolved using info in list */
   	   	
   	   	IP = checkLookUp(list, hostname23 ); 
   	   	 
   	 if (IP == null)
   	 {
   		 System.out.println("IP returned is null!!");
   	 }
   	 else  	
   	 {
   		 IPAddr = new DNSreply(IP, hostname23); //Returning hostname without domain+IP address	
   		return IPAddr;
   	 }	   	
   	   	 	 
   	   	break;
   
   	   	default:
   	   	   	System.out.println("\nIncorrect Command! ");
   	   	break;
   }
    	
    	break;	
   
	default:
	   	   	System.out.println("\nIncorrect Command! ");
   	break;
    
    } // End of switch(flag)
   	
    return IPAddr;
    }
	
    private String checkLookUp(ArrayList<DNSreply> list, String hostnameX) {
		// TODO Auto-generated method stub

    	int p;
    	for (p = 0; p < list.size(); p++) {
            if (list.get(p).hostname.equals(hostnameX))
            {
            	return list.get(p).address;
            }
        }  	
    	  	
    	return null;
	}

    // Variables to collect arguments 
	public static int flag;
	public static String fName;
	public static int serverID;
	public static int port;
	
	ArrayList<DNSreply> cache = new ArrayList<DNSreply>();
	
    public static void main(String args[]) throws FileNotFoundException {
	port = Integer.valueOf(args[3]);

	if(args[0].equals("I"))
	{
		flag = 1;
	}
	else
	{
		flag = 2;
	}
		
	fName = args[1];
	serverID = Integer.parseInt(args[2]);

	
	try {
            Socket s = new Socket("google.com", 80);
            System.setProperty("java.rmi.server.hostname",s.getLocalAddress().getHostAddress());
            s.close();
	    Server obj = new Server();
	    DNSlookup stub = (DNSlookup) UnicastRemoteObject.exportObject(obj, 0);    
	        
	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry(port);
	    registry.bind("Server" + serverID, stub);

	    System.err.println("Server ready");
	} catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	}
    }

	/*@Override
	public DNSreply lookup(String hostname) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	} */
}

