import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Random;

public class KeyImplementation implements KeyInterface {

	private static final BigInteger p = BigInteger.valueOf(191);
	private static final BigInteger g = BigInteger.valueOf(131);
	
	private int a;
	
	private BigInteger secretKey;
	
	public KeyImplementation() throws RemoteException {
	}
	
	public BigInteger calculateX() throws RemoteException {
		Random rand = new Random();
		a = rand.nextInt(20) + 1;
		System.out.println("Value of a is: " + a);
		
		BigInteger x =  g.modPow(BigInteger.valueOf(a), p);
		System.out.println("Value of x is: " + x);
		
		return x;
	}
	
	public BigInteger getP() throws RemoteException {
		return p;
	}

	public BigInteger getG() throws RemoteException {
		return g;
	}

	public BigInteger calculateKey(BigInteger y) throws RemoteException {
		secretKey = y.modPow(BigInteger.valueOf(a), p);
		System.out.println("Server's private key is: " + secretKey);
		
		return secretKey;
	}

	public boolean checkSameSecret(BigInteger key) throws RemoteException {
		if (secretKey.equals(key)) {
			return true;
		} else {
			return false;
		}
	}
	
}
