package users;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

import queues.Queue;

public class User {
	//Member Variables 
	public int id;
	public String firstName;
	public String lastName;
	public String email;
	public ArrayList<Queue> enteredQueues;
	public ArrayList<Queue> managedQueues;
	public String passwordHash;
	public boolean isGuest;
	
	//Constructor
	public User() {
		
	}
	
	public User(String fn, String ln, String e)
	{
		firstName=fn;
		lastName=ln;
		email=e;
		
	}
	//Member Functions
	
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
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public ArrayList<Queue> getEnteredQueues() {
		return enteredQueues;
	}
	public void setEnteredQueues(ArrayList<Queue> enteredQueues) {
		this.enteredQueues = enteredQueues;
	}
	public ArrayList<Queue> getManagedQueues() {
		return managedQueues;
	}
	public void setManagedQueues(ArrayList<Queue> managedQueues) {
		this.managedQueues = managedQueues;
	}
		
}
