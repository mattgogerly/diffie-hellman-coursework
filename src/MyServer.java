import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class to act as the driver for the server
 */

public class MyServer {
	
	/**
	 * Main method to run the server
	 */
	public static void main(String[] args) {
		try {
			// Create a new ServerImplementation and bind the resulting ServerInterface to the registry
			ServerImplementation serv = new ServerImplementation();
			ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(serv, 0);
			Registry reg = LocateRegistry.getRegistry();
			reg.rebind("Key", stub);
		} catch (RemoteException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
}
