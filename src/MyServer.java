import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MyServer {
	
	public static void main(String[] args) {
		try {
			KeyImplementation serv = new KeyImplementation();
			KeyInterface stub = (KeyInterface) UnicastRemoteObject.exportObject(serv, 0);
			Registry reg = LocateRegistry.getRegistry();
			reg.rebind("Key", stub);
		} catch (RemoteException e) {
			System.err.println(e.getMessage());
		}
	}
	
}
