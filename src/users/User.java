package users;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import queues.Queue;

@DatabaseTable(tableName = "users")
public class User {
	
	// Database field names
    public static final String EMAIL_FIELD_NAME = "email";
    public static final String PWDHASH_FIELD_NAME = "passwordhash";
    public static final String FIRSTNAME_FIELD_NAME = "firstName";
    public static final String LASTNAME_FIELD_NAME = "lastName";
    public static final String ENTEREDQUEUES_FIELD_NAME = "enteredQueues";
    public static final String MANAGEDQUEUES_FIELD_NAME = "managedQueues";
    public static final String ISGUEST_FIELD_NAME = "isGuest";

	// Member Variables 
    @DatabaseField(id = true)
	private String email;
    @DatabaseField
	private String firstName;
    @DatabaseField
	private String lastName;
    @DatabaseField
    private String passwordHash;
    @DatabaseField
    private boolean isGuest;
	
	// No-argument constructor for ORMLite
	public User() {
		
	}
	
	// Normal constructor
	public User(String fn, String ln, String e)
	{
		firstName=fn;
		lastName=ln;
		email=e;
		
	}
	
	// Member Functions
	
	public void leaveQueue(Queue q)
	{
		
	}
	public void joinQueue(Queue q)
	{
		
	}
	
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
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
