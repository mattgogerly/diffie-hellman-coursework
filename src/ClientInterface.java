import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An interface for the server to use to call methods on the client.
 */

public interface ClientInterface extends Remote {
	
	void setVars(BigInteger x, BigInteger p, BigInteger g) throws RemoteException;
	
	BigInteger getY() throws RemoteException;
	void calculateKey() throws RemoteException;
	
	void setCiphertext(String cipherText) throws RemoteException;
	void decryptCiphertext() throws RemoteException;
	
}
