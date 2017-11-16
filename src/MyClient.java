import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MyClient {
	
	private String serverAddr;
	
	private KeyInterface ki;
	private BigInteger b, p, g;
	private BigInteger x;
	private BigInteger secretKey;
	
	private String cipherText;
	
	public static void main(String[] args) {
		if (args.length == 2) {
			MyClient client = new MyClient(args[0], args[1]);
		} else {
			System.exit(-1);
		}
	}
	
	public MyClient(String serverAddr, String uid) {
		this.serverAddr = serverAddr;
		
		establishSecureConnection();
		
		try {
			//cipherText = ki.getCiphertext(uid);
			//System.out.println(cipherText);
			decryptCiphertext();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		Scanner scan = new Scanner(System.in);
		scan.nextLine();
		scan.close();
	}
	
	private boolean establishSecureConnection() {
		try {
			Registry reg = LocateRegistry.getRegistry(serverAddr);
			ki = (KeyInterface) reg.lookup("Key");
			
			p = ki.getP();
			g = ki.getG();
			x = ki.calculateX();
			
			Random rand = new Random();
			b = BigInteger.valueOf(rand.nextInt(20) + 1);
			
			BigInteger y = g.modPow(b, p);
			ki.calculateKey(y);
			
			secretKey = x.modPow(b, p);
			
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
	
	private void decryptCiphertext() {
		int shuffleDistance = secretKey.mod(BigInteger.valueOf(8)).intValue();
		
		List<char[]> chunks = new ArrayList<char[]>();
		
		int i = 0;
		while (i < cipherText.length()) {
			char[] chunk = cipherText.substring(i, Math.min(i + 8, cipherText.length())).toCharArray();
			chunks.add(chunk);
			i += 8;
		}
		
		for (int j = 0; j < chunks.size(); j++) {	
			char[] current = chunks.get(j);
			
			deSubstitute(current);
			deTranspose(current, shuffleDistance);
			deTranspose(current, shuffleDistance);
		}
		
		for (int j = 0; j < chunks.size(); j++) {
			char[] current = chunks.get(j);
			
			for (int k = 0; k < 8; k++) {
				System.out.print(current[k]);
			}
			
			System.out.println();
		}
	}
	
	private void deTranspose(char[] characters, int distance) {
		char[] temp = new char[characters.length];
		
		System.arraycopy(characters, distance, temp, 0, characters.length - distance);
	    System.arraycopy(characters, 0, temp, characters.length - distance, distance);
	    
	    System.arraycopy(temp, 0, characters, 0, characters.length);
	}
	
	private void deSubstitute(char[] characters) {
		int distance = secretKey.mod(BigInteger.valueOf(26)).intValue();
		
		for (int i = 0; i < characters.length; i++) {
			char current = characters[i];
			
			if (current >= 'a' && current <= 'z') {
				current = (char) (current - distance);
				
				if (current <'a') {
					current = (char) (current + 'z' - 'a' + 1);
				}
				
				characters[i] = current;
			} else if (current >= 'A' && current <= 'Z') {
				current = (char) (current - distance);
				
				if (current <'A') {
					current = (char) (current + 'Z' - 'A' + 1);
				}
				
				characters[i] = current;
			}
		}
	}
}
