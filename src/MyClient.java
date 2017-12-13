import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class to act as the driver for the client
 */

public class MyClient {
	
	// Use a static Registry so the JVM garbage collector doesn't delete it
	private static Registry reg;
	
	/**
	 * Main method to run the client
	 * 
	 * @param args: array supposedly containing a hostname and userID
	 */
	public static void main(String[] args) {
		// If we don't have both arguments then exit
		if (args.length != 2) {
			return;
		} else {
			try {
				// Set the security policy for the client
				System.setProperty( "java.security.policy", "SecurityPolicy" );
				if (System.getSecurityManager() == null) {
				    System.setSecurityManager(new SecurityManager());
				}
				
				// Get the hostname and uid from the arguments
				String hostname = args[0];
				String uid = args[1];
				
				// Create a new client implementation based on a client interface and export it to the registry
				ClientImplementation client = new ClientImplementation();
				ClientInterface clientStub = (ClientInterface) UnicastRemoteObject.exportObject(client, 0);
				
				// Find the registry for the given hostname and get the ServerInterface
				reg = LocateRegistry.getRegistry(hostname);
			    ServerInterface si = (ServerInterface) reg.lookup("Key");
				
			    // Tell the server to start calculating the key for this client
			    si.calculateKey(clientStub);
			    
			    // If we have the same key then a secure connection was established
			    if (si.checkSameSecret(clientStub, clientStub.getSecretKey())) {
			    	System.out.println("Secure connection to server established!");
			    	
				    si.getCiphertext(clientStub, uid);
			    }
			    
			    // Exit the client
			    System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
