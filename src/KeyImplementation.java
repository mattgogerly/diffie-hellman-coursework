import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class KeyImplementation implements KeyInterface {

	private static final BigInteger p = BigInteger.valueOf(191);
	private static final BigInteger g = BigInteger.valueOf(131);
	
	private int a;
	
	private BigInteger secretKey;
	
	public KeyImplementation() throws RemoteException {
		System.setProperty("java.security.policy", "SecurityPolicy");
	}
	
	public BigInteger calculateX() throws RemoteException {
		Random rand = new Random();
		a = rand.nextInt(20) + 1;
		
		BigInteger x =  g.modPow(BigInteger.valueOf(a), p);
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
		return secretKey;
	}

	public boolean checkSameSecret(BigInteger key) throws RemoteException {
		if (secretKey.equals(key)) {
			System.out.println("Secure connection to client established!");
			return true;
		} else {
			System.out.println("Secure connection to client failed!");
			return false;
		}
	}
	
	public String getCiphertext(String uid) {
		try {
			System.setProperty("java.rmi.server.codebase", "http://users.ecs.soton.ac.uk/tjn1f15/comp2207.jar");
			
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			
			Registry reg = LocateRegistry.getRegistry("svm-tjn1f15-comp2207.ecs.soton.ac.uk");
			CiphertextInterface ci = (CiphertextInterface) reg.lookup("CypherRequest");
			
			String text = ci.get(uid, secretKey.intValue());
			return text;
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return null;
	}
	
}
