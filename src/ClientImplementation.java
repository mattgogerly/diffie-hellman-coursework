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
	
	// The ciphertext retrieved from the server
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
	
	
	/**
	 * Method to return the Y value (but only once calculated)
	 */
	public BigInteger getY() {	
		/*
		 *  This needs to be thread safe - otherwise the server could get Y before 
		 *  we have calculated it which would result in an error. We therefore
		 *  force the requesting thread to wait until this client has set calculatingY
		 *  to false, indicating the value is ready for getting.
		 */
		
		while (calculatingY) {
			try {
				wait();
			} catch (InterruptedException e) {
				//
			}
		}
		
		return y;
	}
	
	/**
	 * Method to return the calculated secret key (only once calculated)
	 */
	public int getSecretKey() {
		/*
		 *  This needs to be thread safe - otherwise the server could try to get
		 *  the key for comparison before we have calculated it which would result in 
		 *  an error. We therefore force the requesting thread to wait until this client 
		 *  has set calculatingKey to false, indicating the value is ready for getting.
		 */
		
		while (calculatingKey) {
			try {
				wait();
			} catch (InterruptedException e) {
				//
			}
		}
		
		return secretKey;
	}
	
	/**
	 * Method to set the retrieved ciphertext and start decrypting it
	 * 
	 * @param cipherText: the ciphertext retrieved from the remote server
	 */
	public void setCiphertext(String cipherText) {
		this.cipherText = cipherText;
		
		decryptCiphertext();
	}
	
	/**
	 * Method that handles decrypting the ciphertext
	 */
	public void decryptCiphertext() {		
		// List to store each 8 character chunk
		List<char[]> chunks = new ArrayList<char[]>();
		
		// Loop over the ciphertext and split it into 8 character chunks
		int i = 0;
		while (i < cipherText.length()) {
			char[] chunk = cipherText.substring(i, Math.min(i + 8, cipherText.length())).toCharArray();
			chunks.add(chunk);
			i += 8;
		}
		
		System.out.println();
		
		// For each chunk of 8 characters
		for (int j = 0; j < chunks.size(); j++) {	
			char[] current = chunks.get(j);
			
			// Do two rounds of desubstitution and then two rounds of detransposition
			deSubstitute(current);
			deSubstitute(current);
			
			deTranspose(current);					
			deTranspose(current);
		}
		
		// Print the decrypted chunks
		for (int j = 0; j < chunks.size(); j++) {
			char[] current = chunks.get(j);
			
			for (int k = 0; k < 8; k++) {
				System.out.print(current[k]);
			}
		}
	}
	
	/**
	 * Method to detranspose a chunk of characters
	 * 
	 * @param characters: the array of characters to be detransposed
	 * @param distance: the distance to the left each character should move
	 */
	private void deTranspose(char[] characters) {
		// Calculate the distance to shuffle each letter by
		int shuffleDistance = secretKey % 8;
		
		// Create a temp helper array
		char[] temp = new char[characters.length];
		
		// Copy the original array to the temp array
		System.arraycopy(characters, shuffleDistance, temp, 0, characters.length - shuffleDistance);
		
		// Copy the characters back into their detransposed positions
	    System.arraycopy(characters, 0, temp, characters.length - shuffleDistance, shuffleDistance);
	    System.arraycopy(temp, 0, characters, 0, characters.length);
	}
	
	/**
	 * Method to desubstitute each character (reverse Caesar cipher)
	 * 
	 * @param characters: the array of characters to be desubstituted
	 */
	private void deSubstitute(char[] characters) {
		// Calculate the distance each character needs to be shifted
		int distance = secretKey % 26;
		
		for (int i = 0; i < characters.length; i++) {
			char current = characters[i];
			
			// Basic sanity checking - make sure the character we're desubstituting is in caps between A and Z
			if (current >= 'A' && current <= 'Z') {
				// Perform Caeser shift to the left by distance
				current = (char) (current - distance);
				
				if (current < 'A') {
					current = (char) (current + 'Z' - 'A' + 1);
				}
				
				characters[i] = current;
			}
		}
	}
	
}
