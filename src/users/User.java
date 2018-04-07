package users;
import java.util.ArrayList;

import queues.Queue;

abstract public class User {
	//Member Variables 
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private ArrayList<Queue> enteredQueues;
	private ArrayList<Queue> managedQueues;
	//Constructor
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
