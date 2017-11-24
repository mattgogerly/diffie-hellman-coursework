import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// https://www.e-reading.club/chapter.php/71303/10/Milne_-_The_house_at_Pooh_Corner.html

/**
 * An implementation of the ClientInterface remote interface.
 * 
 * @see ClientInterface
 * 
 * @author Matt Gogerly
 * @version 1.0
 */

public class ClientImplementation implements ClientInterface {
	
	// BigIntegers to store values calculated during the calculating of the secret key
	private BigInteger b, p, g;
	private BigInteger x;
	private BigInteger y;
	
	private volatile boolean calculatingY = false;
	private volatile boolean calculatingKey = false;
	
	// int to store the calculated secret key
	private int secretKey;
	
	// The ciphertext retreived from the server
	private String cipherText;
	
	/**
	 * Constructor for ClientImplementation. We don't need to store any values so we leave it empty.
	 */
	public ClientImplementation() {

	}
	
	/**
	 * Method to calculate the private key. 
	 * 
	 * This method is called by the server only once p, g and x have been set.
	 */
	public void calculateKey() {
		try {		
			calculatingKey = true;
			calculatingY = true;
			
			// Generate a random number b
			Random rand = new Random();
			b = BigInteger.valueOf(rand.nextInt(20) + 1);
			
			// Calculate y (g^b % p) and set calculating back to false (so server can get)
			y = g.modPow(b, p);
			
			calculatingY = false;
			
			// Calculate the secret key on the client side (x^b % p)
			BigInteger tempKey = x.modPow(b, p);
			secretKey = tempKey.intValue();
			
			calculatingKey = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to set x, p and g values.
	 * 
	 * @param x: the value for x calculated by the remote server.
	 * @param p: value of p provided by the server ("large" prime)
	 * @param g: value of g provided by server (primitive root of p)
	 */
	public void setVars(BigInteger x, BigInteger p, BigInteger g) {
		this.x = x;
		this.p = p;
		this.g = g;
	}
	
	
	public BigInteger getY() {		
		while (calculatingY) {
			try {
				wait();
			} catch (InterruptedException e) {
				//
			}
		}
		
		return y;
	}
	
	public int getSecretKey() {
		while (calculatingKey) {
			try {
				wait();
			} catch (InterruptedException e) {
				//
			}
		}
		
		return secretKey;
	}
	
	public void setCiphertext(String cipherText) {
		this.cipherText = cipherText;
	}
	
	public void decryptCiphertext() {
		int shuffleDistance = secretKey % 8;
		
		List<char[]> chunks = new ArrayList<char[]>();
		
		int i = 0;
		while (i < cipherText.length()) {
			char[] chunk = cipherText.substring(i, Math.min(i + 8, cipherText.length())).toCharArray();
			chunks.add(chunk);
			i += 8;
		}
		
		System.out.println();
		
		for (int j = 0; j < chunks.size(); j++) {	
			char[] current = chunks.get(j);
			
			deSubstitute(current);
			deSubstitute(current);
			
			deTranspose(current, shuffleDistance);					
			deTranspose(current, shuffleDistance);
		}
		
		for (int j = 0; j < chunks.size(); j++) {
			char[] current = chunks.get(j);
			
			for (int k = 0; k < 8; k++) {
				System.out.print(current[k]);
			}
		}
	}
	
	private void deTranspose(char[] characters, int distance) {
		char[] temp = new char[characters.length];
		
		System.arraycopy(characters, distance, temp, 0, characters.length - distance);
	    System.arraycopy(characters, 0, temp, characters.length - distance, distance);
	    
	    System.arraycopy(temp, 0, characters, 0, characters.length);
	}
	
	private void deSubstitute(char[] characters) {
		int distance = secretKey % 26;
		
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
