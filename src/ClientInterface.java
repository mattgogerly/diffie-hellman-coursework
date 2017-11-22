import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
	
	public void setP(BigInteger p) throws RemoteException;
	public void setG(BigInteger g) throws RemoteException;
	public void setX(BigInteger x) throws RemoteException;
	
	public BigInteger getY() throws RemoteException;
	public int getSecretKey() throws RemoteException;
	public boolean calculateKey() throws RemoteException;
	
	public void setCiphertext(String cipherText) throws RemoteException;
	public void decryptCiphertext() throws RemoteException;
	
}
