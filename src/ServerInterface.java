import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An interface for the client to use to call methods on the server
 */

public interface ServerInterface extends Remote {
	
	BigInteger getP() throws RemoteException;
	BigInteger getG() throws RemoteException;
	
	void calculateKey(ClientInterface client) throws RemoteException, Exception;

	void getCiphertext(ClientInterface client, String uid) throws RemoteException;
	
}
