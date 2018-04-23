package queues;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import users.User;

@DatabaseTable(tableName = "queueEntries")
public class QueueEntry implements Comparable<QueueEntry> {
	
	// Database field names
    public static final String ENTRYID_FIELD_NAME = "entryID";
    public static final String TIMEOFENTRY_FIELD_NAME = "timeOfEntry";
    public static final String TEXTFIELDINPUT_FIELD_NAME = "textFieldInput";
    public static final String NUMFIELDINPUT_FIELD_NAME = "numFieldInput";
    public static final String USER_FIELD_NAME = "user";
    public static final String USEREMAIL_FIELD_NAME = "userEmail";
    public static final String QUEUE_FIELD_NAME = "queue";
    public static final String QCODE_FIELD_NAME = "qCode";
    public static final String POSITION_FIELD_NAME = "position";

    
	// Member variables
    @DatabaseField(generatedIdSequence = "queueentries_id_seq")
	private int entryID;
    @DatabaseField
	private Date timeOfEntry;
    @DatabaseField
	private String textFieldInput;
    @DatabaseField
	private Integer numFieldInput;
    @DatabaseField(foreign = true)
	private User user;
    @DatabaseField(foreign = true)
	private Queue queue;
    @DatabaseField
	private int position;
    
    // No-argument constructor for ORMLite
    public QueueEntry() {
    	
    }
	
	// QueueEntry constructor
	public QueueEntry(User u, Queue q, String textFieldInput, int numFieldInput) {
		this.user = u;
		this.queue = q;
		this.textFieldInput = textFieldInput;
		this.numFieldInput = numFieldInput;
		timeOfEntry = new Date(); // Track current date/time
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

	public User getUser() {
		return user;
	}

	public void setUser(User u) {
		this.user = u;
	}
	
	public Queue getQueue() {
		return queue;
	}

	public void setQueue(Queue q) {
		this.queue = q;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int pos) {
		this.position = pos;
	}
	
	public int compareTo(QueueEntry other) {
		return (this.getPosition() - other.getPosition());
	}
	
}
