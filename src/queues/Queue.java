package queues;

import java.util.Vector;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import users.User;

@DatabaseTable(tableName = "queues")
public class Queue {
	// Database field names
    public static final String QCODE_FIELD_NAME = "qCode";
    public static final String NAME_FIELD_NAME = "name";
    public static final String DESCRIPTION_FIELD_NAME = "description";
    public static final String OWNER_FIELD_NAME = "owner";
    public static final String NUMFIELDDESCRIPTION_FIELD_NAME = "numFieldDescription";
    public static final String TEXTFIELDDESCRIPTION_FIELD_NAME = "textFieldDescription";
    public static final String ISNUMFIELDREQUIRED_FIELD_NAME = "isNumFieldRequired";
    public static final String ISTEXTFIELDREQUIRED_FIELD_NAME = "isTextFieldRequired";
    public static final String ISLOCATIONRESTRICTED_FIELD_NAME = "isLocationRestricted";    
    public static final String ISPUBLIC_FIELD_NAME = "isPublic";
    public static final String MAXSIZE_FIELD_NAME = "maxSize";
    public static final String AVGWAITTIME_FIELD_NAME = "avgWaitTime";
    public static final String NUMUSERSPROCESSED_FIELD_NAME = "numUsersProcessed";
    public static final String QUEUEENTRIES_FIELD_NAME = "queueEntries";
    public static final String LONGITUDE_FIELD_NAME = "longitude";
    public static final String LATITUDE_FIELD_NAME = "latitude";
    public static final String RADIUS_FIELD_NAME = "radius";

	
	// Member Variables
    @DatabaseField(id = true)
	private String qCode;
    @DatabaseField
	private String name;
    @DatabaseField
	private String description;
    @DatabaseField(foreign = true)
	private User owner; // email of the queue creator
    @DatabaseField
	private String numFieldDescription;
    @DatabaseField
	private String textFieldDescription;
    @DatabaseField
	private boolean isNumFieldRequired;
    @DatabaseField
	private boolean isTextFieldRequired;
    @DatabaseField
	private boolean isLocationRestricted;
    @DatabaseField
	private boolean isPublic;
    @DatabaseField
	private int maxSize;
    @DatabaseField
	private double avgWaitTime;
    @DatabaseField
	private int numUsersProcessed;
    @DatabaseField
	private Vector<QueueEntry> queueEntries;
    @DatabaseField
	private double longitude;
    @DatabaseField
	private double latitude;
    @DatabaseField
	private double radius; // feet
    
	// TODO: Google Maps location
    
    // no-argument constructor for ORMLite
    public Queue () {
    	
    }
	
	// Queue Constructor
		// If num/text fields are required, those descriptions must be set manually
		// If queue is location restricted, lat/long/radius must be set manually
	public Queue(String qCode, String name, String description, User owner,
			boolean numFieldRequired, boolean textFieldRequired, boolean isLocationRestricted,
			boolean isPublic, int maxSize) {
		this.qCode = qCode;
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.isNumFieldRequired = numFieldRequired;
		this.isTextFieldRequired = textFieldRequired;
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

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
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
		return isNumFieldRequired;
	}

	public void setNumFieldRequired(boolean numFieldRequired) {
		this.isNumFieldRequired = numFieldRequired;
	}

	public boolean isTextFieldRequired() {
		return isTextFieldRequired;
	}

	public void setTextFieldRequired(boolean textFieldRequired) {
		this.isTextFieldRequired = textFieldRequired;
	}
	
	
}