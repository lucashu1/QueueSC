package queues;

import java.util.Vector;

public class Queue {
	// Member Variables
	private String qCode;
	private String name;
	private String description;
	private String owner; // email of the queue creator
	private String numFieldDescription;
	private String textFieldDescription;
	private boolean numFieldRequired;
	private boolean textFieldRequired;
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
	
	// Queue Constructor
		// If num/text fields are required, those descriptions must be set manually
		// If queue is location restricted, lat/long/radius must be set manually
	public Queue(String qCode, String name, String description, String owner,
			boolean numFieldRequired, boolean textFieldRequired, boolean isLocationRestricted,
			boolean isPublic, int maxSize) {
		this.qCode = qCode;
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.numFieldRequired = numFieldRequired;
		this.textFieldRequired = textFieldRequired;
		this.isLocationRestricted = isLocationRestricted;
		this.isPublic = isPublic;
		this.maxSize = maxSize;
	}
	
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

	public String getqCode() {
		return qCode;
	}

	public void setqCode(String qCode) {
		this.qCode = qCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNumFieldDescription() {
		return numFieldDescription;
	}

	public void setNumFieldDescription(String numFieldDescription) {
		this.numFieldDescription = numFieldDescription;
	}

	public String getTextFieldDescription() {
		return textFieldDescription;
	}

	public void setTextFieldDescription(String textFieldDescription) {
		this.textFieldDescription = textFieldDescription;
	}

	public boolean isLocationRestricted() {
		return isLocationRestricted;
	}

	public void setLocationRestricted(boolean isLocationRestricted) {
		this.isLocationRestricted = isLocationRestricted;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public double getAvgWaitTime() {
		return avgWaitTime;
	}

	public void setAvgWaitTime(double avgWaitTime) {
		this.avgWaitTime = avgWaitTime;
	}

	public int getNumUsersProcessed() {
		return numUsersProcessed;
	}

	public void setNumUsersProcessed(int numUsersProcessed) {
		this.numUsersProcessed = numUsersProcessed;
	}

	public Vector<QueueEntry> getQueueEntries() {
		return queueEntries;
	}

	public void setQueueEntries(Vector<QueueEntry> queueEntries) {
		this.queueEntries = queueEntries;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public boolean isNumFieldRequired() {
		return numFieldRequired;
	}

	public void setNumFieldRequired(boolean numFieldRequired) {
		this.numFieldRequired = numFieldRequired;
	}

	public boolean isTextFieldRequired() {
		return textFieldRequired;
	}

	public void setTextFieldRequired(boolean textFieldRequired) {
		this.textFieldRequired = textFieldRequired;
	}
	
	
}