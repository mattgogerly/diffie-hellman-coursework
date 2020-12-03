import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * The provided interface for the stub running on the specified remote
 * server. Provides the means to get a ciphertext, encrypted using
 * a provided user id and secret key.
 * 
 * @author Tim Norman, University of Southampton
 */

public interface CiphertextInterface extends Remote {
	
	String get(String uid , int key) throws RemoteException;
	
}