import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

public class MyClient {
	
	private String serverAddr;
	private String uid;
	
	private BigInteger b, p, g;
	private BigInteger x;
	private BigInteger secretKey;
	
	public static void main(String[] args) {
		if (args.length == 2) {
			MyClient client = new MyClient(args[0], args[1]);
		} else {
			System.exit(-1);
		}
	}
	
	public MyClient(String serverAddr, String uid) {
		this.serverAddr = serverAddr;
		this.uid = uid;
		
		establishSecureConnection();
		
		Scanner scanner = new Scanner(System.in); 
		scanner.nextLine();
	}
	
	private boolean establishSecureConnection() {
		try {
			Registry reg = LocateRegistry.getRegistry(serverAddr);
			KeyInterface ki = (KeyInterface) reg.lookup("Key");
			
			p = ki.getP();
			g = ki.getG();
			x = ki.calculateX();
			
			Random rand = new Random();
			b = BigInteger.valueOf(rand.nextInt(20) + 1);
			System.out.println("Value of b is: " + b);
			
			BigInteger y = g.modPow(b, p);
			ki.calculateKey(y);
			
			secretKey = x.modPow(b, p);
			
			System.out.println("Client's key is: " + secretKey);
			
			if (!ki.checkSameSecret(secretKey)) {
				throw new Exception("The private keys do not match, secure connection not established!");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
		
		System.out.println("Secure connection established!");
		return true;
	}
}
