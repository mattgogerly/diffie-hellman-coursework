import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface KeyInterface extends Remote {
	
	public BigInteger getP() throws RemoteException;
	public BigInteger getG() throws RemoteException;
	
	public void calculateKey(ClientInterface client) throws RemoteException, Exception;
	public boolean checkSameSecret(ClientInterface client, int key) throws RemoteException;
	
	public void getCiphertext(ClientInterface client, String uid) throws RemoteException;
	
}
