package queues;

import java.util.Vector;

public class Queue {
	// Member Variables
	private int queueID;
	private String qCode;
	private String name;
	private String description;
	private String numFieldDescription;
	private String textFieldDescription;
	private boolean isLocationRestricted;
	private boolean isPublic;
	private int maxSize;
	private double avgWaitTime;
	private int numUsersProcessed;
	private Vector<QueueEntry> queueEntries;
	// TODO: Google Maps location
	
	// Return true if queue contains a user with the given email
	public boolean hasUser(String email) {
		for (QueueEntry qe : queueEntries) {
			if (qe.getUser().getEmail().equals(email)) {
				return true;
			}
		}
		return false;
	}
}