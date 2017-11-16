import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface KeyInterface extends Remote {
	
	public BigInteger getP() throws RemoteException;
	public BigInteger getG() throws RemoteException;
	
	public BigInteger calculateX() throws RemoteException;
	public BigInteger calculateKey(BigInteger y) throws RemoteException;
	public boolean checkSameSecret(BigInteger key) throws RemoteException;
	
	public String getCiphertext(String uid) throws RemoteException;
	
}
