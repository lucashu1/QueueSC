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
	private double longitude;
	private double latitude;
	private double radius;
	// TODO: Google Maps location
	
	//getPosition returns int
	//enqueue adds new entrant with 
	public boolean inRange(double userLat, double userLong) {
	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(latitude - userLat);
	    double lonDistance = Math.toRadians(longitude - userLong);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(latitude))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters
	    distance = distance * 3.28084; //converts meters to feet
	    
	      if (distance <= radius) {
	    	  	return true;
	      }
	      return false;
	}
	
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