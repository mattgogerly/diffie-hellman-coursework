import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An interface for the server to use to call methods on the client.
 */

public interface ClientInterface extends Remote {
	
	public void setVars(BigInteger x, BigInteger p, BigInteger g) throws RemoteException;
	
	public BigInteger getY() throws RemoteException;
	public int getSecretKey() throws RemoteException;
	public void calculateKey() throws RemoteException;
	
	public void setCiphertext(String cipherText) throws RemoteException;
	public void decryptCiphertext() throws RemoteException;
	
}
