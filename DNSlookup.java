
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.Serializable;
/*
public class DNSreply implements Serializable {

    final String address;
    final String hostname;

    public DNSreply (String address, String hostname) {
        this.address = address;
        this.hostname = hostname;
    }

}*/

public interface DNSlookup extends Remote {
	
    DNSreply lookup (String hostname) throws RemoteException;

}
