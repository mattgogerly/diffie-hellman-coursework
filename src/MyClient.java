import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

// https://www.e-reading.club/chapter.php/71303/10/Milne_-_The_house_at_Pooh_Corner.html

public class MyClient {
	
	private static Registry reg;
	
	public static void main(String[] args) {
		if (args.length != 2) {
			return;
		} else {
			try {
				System.setProperty( "java.security.policy", "SecurityPolicy" );
				if (System.getSecurityManager() == null) {
				    System.setSecurityManager(new SecurityManager());
				}
				
				String hostname = args[0];
				String uid = args[1];
				
				ClientImplementation client = new ClientImplementation();
				ClientInterface clientStub = (ClientInterface) UnicastRemoteObject.exportObject(client, 0);
				reg = LocateRegistry.getRegistry(hostname);
			    KeyInterface ki = (KeyInterface) reg.lookup("Key");
				
			    ki.calculateKey(clientStub);
			    if (ki.checkSameSecret(clientStub, clientStub.getSecretKey())) {
			    	System.out.println("Secure connection to server established!");
			    }
			    
			    ki.getCiphertext(clientStub, uid);
			    
			    Scanner scan = new Scanner(System.in);
			    scan.nextLine();
			    scan.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
