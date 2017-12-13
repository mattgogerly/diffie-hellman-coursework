import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An interface for the client to use to call methods on the server
 */

public interface ServerInterface extends Remote {
	
	public BigInteger getP() throws RemoteException;
	public BigInteger getG() throws RemoteException;
	
	public void calculateKey(ClientInterface client) throws RemoteException, Exception;
	public boolean checkSameSecret(ClientInterface client, int key) throws RemoteException;
	
	public void getCiphertext(ClientInterface client, String uid) throws RemoteException;
	
}
