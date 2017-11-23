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
	public boolean calculateKey() {
		try {		
			// Generate a random number b
			Random rand = new Random();
			b = BigInteger.valueOf(rand.nextInt(20) + 1);
			
			// Calculate y (g^b % p) and set calculating back to false (so server can get)
			y = g.modPow(b, p);
			
			// Calculate the secret key on the client side (x^b % p)
			BigInteger tempKey = x.modPow(b, p);
			secretKey = tempKey.intValue();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Method to set X value.
	 * 
	 * @param x: the value for x calculated by the remote server.
	 */
	public void setX(BigInteger x) {
		this.x = x;
	}
	
	/**
	 * Method to set p value.
	 * 
	 * @param p: the value for p provided by the remote server.
	 */
	public void setP(BigInteger p) {
		this.p = p;
	}
	
	/**
	 * Method to set g value.
	 * 
	 * @param p: the value for g provided by the remote server.
	 */
	public void setG(BigInteger g) {
		this.g = g;
	}
	
	
	public BigInteger getY() {		
		return y;
	}
	
	public int getSecretKey() {
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
