import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// https://www.e-reading.club/chapter.php/71303/10/Milne_-_The_house_at_Pooh_Corner.html

public class ClientImplementation implements ClientInterface {
	
	private BigInteger b, p, g;
	private BigInteger x;
	private BigInteger y;
	private int secretKey;
	
	public volatile boolean calculating = false;
	
	private String cipherText;
	
	public ClientImplementation() {
		//
	}
	
	public boolean calculateKey() {
		try {			
			calculating = true;
		
			Random rand = new Random();
			b = BigInteger.valueOf(rand.nextInt(20) + 1);
			
			y = g.modPow(b, p);
			calculating = false;
			
			BigInteger tempKey = x.modPow(b, p);
			secretKey = tempKey.intValue();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void setX(BigInteger x) {
		this.x = x;
	}
	
	public void setP(BigInteger p) {
		this.p = p;
	}
	
	public void setG(BigInteger g) {
		this.g = g;
	}
	
	public BigInteger getY() {
		while (calculating) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		
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
		
		int l = 0;
		for (int j = 0; j < chunks.size(); j++) {
			char[] current = chunks.get(j);
			
			for (int k = 0; k < 8; k++) {
				System.out.print(current[k] + " ");
			}
			
			l++;
			if (l % 3 == 0) {
				System.out.println();
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
