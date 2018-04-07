package queues;

import java.util.Date;
import users.User;

public class QueueEntry {
	// Member variables
	private int entryID;
	private Date timeOfEntry;
	private String textFieldInput;
	private int numFieldInput;
	private User u;
	private Queue q;
	
	// QueueEntry constructor
	public QueueEntry(User u, Queue q, String textFieldInput, int numFieldInput) {
		this.u = u;
		this.q = q;
		this.textFieldInput = textFieldInput;
		this.numFieldInput = numFieldInput;
		timeOfEntry = new Date(); // Track current date/time
	}
	
	public User getUser() {
		return u;
	}
}
