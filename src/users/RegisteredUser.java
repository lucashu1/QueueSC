package users;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class RegisteredUser extends User{

	//Member variables
	private String passwordHash;
	
	
	//Constructor
	public RegisteredUser(String fn, String ln, String e, String ph ) {
		super(fn, ln, e);
		passwordHash=ph;
	}

	//Member functions
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public boolean isPasswordValid(String pwd)
	{
		String hashedInput = hashPassword(pwd);
		if (hashedInput.equals(passwordHash)) {
			return true;
		}
		return false;
	}
	
	public String hashPassword(String password) {
		String myHash = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			byte[] digest = md.digest();
			myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
			return myHash;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error in hashPassword: " + e.getMessage());
			return myHash;
		}	
	}
	
}
