import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.Random;

/**
 * An implementation of the ServerInterface remote interface.
 * 
 * @see ServerInterface
 * 
 */

public class ServerImplementation implements ServerInterface {

	// Set our p and g values as specified in the spec
	private static final BigInteger p = BigInteger.valueOf(191);
	private static final BigInteger g = BigInteger.valueOf(131);

	/**
	 *  A Hashtable containing clients and their keys so they can be used later.
	 *  We use a Hashtable because it is synchronised, unlike a HashMap. This prevents
	 *  the potential conflict of a client's key being changed whilst another thread
	 *  is trying to get the key.
	 */
	private final Hashtable<ClientInterface, Integer> secretKeys;
	
	/**
	 * Constructor to create a new ServerImplementation. We set the security policy
	 * and codebase here so that it doesn't have to be dealt with later.
	 */
	public ServerImplementation() throws RemoteException {
		System.setProperty("java.security.policy", "SecurityPolicy");
		System.setProperty("java.rmi.server.codebase", "http://users.ecs.soton.ac.uk/tjn1f15/comp2207.jar");
		
		// Initialise the Hashtable
		secretKeys = new Hashtable<>();
	}
	
	/**
	 * Method to return the P value set on creation
	 */
	public BigInteger getP() throws RemoteException {
		return p;
	}

	/**
	 * Method to return the G value set on creation
	 */
	public BigInteger getG() throws RemoteException {
		return g;
	}

	/**
	 * Method to calculate a secret key. This needs to be synchronised as we use the instance variables
	 * a and y. If this wasn't synchronised then if two clients requested a connection at the same time
	 * these values could potentially change halfway through each calculation, resulting in one or all
	 * of the clients being unable to calculate the same key, thus preventing a connection.
	 */
	public synchronized void calculateKey(ClientInterface client) throws RemoteException {
		// Generate a random a value for this client
		Random rand = new Random();
		// Values that will be calculated when a client requests a connection
		int a = rand.nextInt(20) + 1;
		
		// Calculate x (g^a % p)
		BigInteger x =  g.modPow(BigInteger.valueOf(a), p);
		
		// Send the x, p and g values to the client and tell it to start calculating its key
		client.setVars(x, p, g);
		client.calculateKey();
		
		// Get the y value from the client (note the server thread may be forced to wait here if the client is very slow)
		BigInteger y = client.getY();
		
		// Calculate the secret key (y^a % p)
		BigInteger temp = y.modPow(BigInteger.valueOf(a), p);
		
		// Set the int value of the key
		int secretKey = temp.intValue();
		
		// Store the client alongside its key
		secretKeys.put(client, secretKey);
	}
	
	/**
	 * Method to request the ciphertext from the remote server.
	 * 
	 * @param client: the client requesting the ciphertext
	 * @param uid: the userID provided by the client
	 */
	public void getCiphertext(ClientInterface client, String uid) {
		try {			
			// If something went wrong setting the security manager in the constructor set it again			
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			
			// Locate the registry on the provided remote server
			Registry reg = LocateRegistry.getRegistry("svm-tjn1f15-comp2207.ecs.soton.ac.uk", 12345);

			// Get the interface for the name we were given
			CiphertextInterface ci = (CiphertextInterface) reg.lookup("CiphertextProvider");
			
			// Retrieve the secret key for the client
			int secretKey = secretKeys.get(client);
			
			// Get the ciphertext from the remote server
			System.out.println("Requesting cipher text...");
			String text = ci.get(uid, secretKey);
			
			// Callback to the client
			callbackToClient(client, text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to callback to the client, telling it to decrypt the ciphertext
	 * 
	 * @param client: the client in question
	 * @param text: the ciphertext obtained
	 */
	private void callbackToClient(ClientInterface client, String text) {
		try {
			// Send the client the ciphertext
			client.setCiphertext(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
