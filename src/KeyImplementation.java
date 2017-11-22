import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.Random;

public class KeyImplementation implements KeyInterface {

	private static final BigInteger p = BigInteger.valueOf(191);
	private static final BigInteger g = BigInteger.valueOf(131);
	
	private static Registry reg;
	
	private int a;
	private BigInteger y;
	
	private Hashtable<ClientInterface, Integer> secretKeys;
	
	public KeyImplementation() throws RemoteException {
		System.setProperty("java.security.policy", "SecurityPolicy");
		System.setProperty("java.rmi.server.codebase", "http://users.ecs.soton.ac.uk/tjn1f15/comp2207.jar");
		
		secretKeys = new Hashtable<ClientInterface, Integer>();
	}
	
	public BigInteger getP() throws RemoteException {
		return p;
	}

	public BigInteger getG() throws RemoteException {
		return g;
	}

	public synchronized void calculateKey(ClientInterface client) throws RemoteException {
		Random rand = new Random();
		a = rand.nextInt(20) + 1;
		
		BigInteger x =  g.modPow(BigInteger.valueOf(a), p);
		client.setX(x);
		client.setP(p);
		client.setG(g);
		
		client.calculateKey();
		
		y = client.getY();
		
		BigInteger temp = y.modPow(BigInteger.valueOf(a), p);
		int secretKey = temp.intValue();
		
		secretKeys.put(client, secretKey);
	}

	public boolean checkSameSecret(ClientInterface client, int key) throws RemoteException {
		if (secretKeys.get(client) == key) {
			System.out.println("Secure connection to client established!");
			System.out.println();
			return true;
		} else {
			System.out.println("Secure connection to client failed!");
			System.out.println();
			return false;
		}
	}
	
	public void getCiphertext(ClientInterface client, String uid) {
		try {			
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			
			reg = LocateRegistry.getRegistry("svm-tjn1f15-comp2207.ecs.soton.ac.uk");
			CiphertextInterface ci = (CiphertextInterface) reg.lookup("CiphertextProvider");
			
			int secretKey = secretKeys.get(client);
			
			System.out.println("Requesting cipher text...");
			String text = ci.get(uid, secretKey);
			
			System.out.println();
			System.out.println(text);
			System.out.println();
			
			client.setCiphertext(text);
			callbackToClient(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void callbackToClient(ClientInterface client) {
		try {
			client.decryptCiphertext();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
