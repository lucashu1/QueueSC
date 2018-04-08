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

	public int getEntryID() {
		return entryID;
	}

	public void setEntryID(int entryID) {
		this.entryID = entryID;
	}

	public Date getTimeOfEntry() {
		return timeOfEntry;
	}

	public void setTimeOfEntry(Date timeOfEntry) {
		this.timeOfEntry = timeOfEntry;
	}

	public String getTextFieldInput() {
		return textFieldInput;
	}

	public void setTextFieldInput(String textFieldInput) {
		this.textFieldInput = textFieldInput;
	}

	public int getNumFieldInput() {
		return numFieldInput;
	}

	public void setNumFieldInput(int numFieldInput) {
		this.numFieldInput = numFieldInput;
	}

	public User getU() {
		return u;
	}

	public void setU(User u) {
		this.u = u;
	}

	public Queue getQ() {
		return q;
	}

	public void setQ(Queue q) {
		this.q = q;
	}
	
	
}
