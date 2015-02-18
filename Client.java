
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private Client() {}

    public static void main(String[] args) {

	String host = (args.length < 1) ? null : args[0];
       int port = Integer.valueOf(args[1]);
       String fName = args[2]; // Storing the input file
       DNSreply response = null;
       String[] parts = null;
       
       FileOutputStream fp = null;
       File out;
       String myhost;
       String data = "Output file";
       
		try {
			out = new File("output.txt");
			fp = new FileOutputStream(out);
			
			if (!out.exists())
			{
				out.createNewFile();
			}
			
			fp.write(data.getBytes());
		}
		catch (IOException e)
		{
			 e.printStackTrace();
		}
	    // Code: read input file and write output file 
	    // Read input
	    ArrayList<DNSreply> inputFile = new ArrayList<DNSreply>();
	    try {	    
	    	//array list to store file contents of Layer1.txt
	    Scanner ipF = new Scanner(new File(fName));
	    int i=0;
	    while (ipF.hasNext())
	    {
	    	
	        String tempHost = ipF.next();
	        //String tempAddr = ipF.next();
	        inputFile.add(new DNSreply( null, tempHost));
	    	
	        //printing on console
	        i++;
	        
	   	}
	    ipF.close();    	
	    }
	    catch (Exception e)
	    {
	    }
	    
	  // For each element of inputFile  
	   for(int index = 0; index < inputFile.size(); index++)
	   {
	  		   
		  
		// Split hostname
	   // Split the hostname to three parts
	   	myhost = inputFile.get(index).hostname;
	    parts = myhost.split("[.]");
	   	
	    
       try {
			    Registry registry = LocateRegistry.getRegistry(host, port);
			    DNSlookup stub = (DNSlookup) registry.lookup("Server1");
	    response = stub.lookup(myhost);//chk argument
	    
	} catch (Exception e) {
	    System.err.println("Client exception: " + e.toString());
	    e.printStackTrace();
	}
	    if(response==null)
	    {
	    }
	    
	    String a = parts[0] +"."+ parts[1];
	    //System.out.println(a);
	    //System.out.println(response.hostname);
	    if (response.hostname.equals(a))
        {
        	// Iterative
	    	// Need to call server 2 with returned IP
	    	try {
	    	Registry registry = LocateRegistry.getRegistry(response.address, port);
	    	DNSlookup stub = (DNSlookup) registry.lookup("Server2");
	    	response = stub.lookup(response.hostname);
	    	}
	    	catch (Exception e) {
	    	    System.err.println("Client exception: " + e.toString());
	    	    e.printStackTrace();
	    	}
        }
	    else
	    {
	    	//Done..IP retrieved!
	    }
	    
	  //Write output file    
	    
	    try {
	    	fp.write('\n');
	    	fp.write(myhost.getBytes());
	    	fp.write('\t');
			fp.write(response.address.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Write did not happen
		}
	    
	  }// End of for
	  try {
		fp.flush();
		fp.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		// Unsuccessful close
	}
	  
} // main
} // class

