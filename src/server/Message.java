package server;

import java.io.Serializable;
import java.util.Vector;

// ---------- POSSIBLE MESSAGE TYPES & NECESSARY FIELDS ----------
// QUEUE MODIFICATION:
	// createQueue
		// Request: qCode, queueName, queueDescription, email (of owner), (numFieldDescription, textFieldDescription), 
			// isLocationRestricted, isPublic, maxSize, (latitude, longitude, radius)
		// Response: success/failure (qCodeTaken)
	// deleteQueue
		// Request: qCode
		// Response: success/failure (qCodeInvalid)
	// enqueue
		// Request: qCode, email, numFieldInput, textFieldInput, (latitude, longitude)
		// Response: success/failure (qCodeInvalid, emailInvalid, missingFormInput, userOutOfRange, queueAlreadyFull)
	// dequeue
		// Request: qCode
		// Response: success/failure (qCodeInvalid)
	// removeUser
		// Request: qCode, email
		// Response: success/failure (qCodeInvalid, emailInvalid, userNotInQueue)

// USER:
	// registerUser
		// Request: email, firstName, lastName, password
		// Response: success/failure (emailTaken, emailInvalid)
	// userLogin
		// Request: email, password
		// Response: success/failure (emailDoesNotExist, incorrectPassword)
	// guestLogin
		// Request: email, firstName, lastName, qCode
		// Response: success/failure (emailTaken, qCodeInvalid)

// LOAD INFO:
	// pullQueueInfo
		// Request: qCode
		// Response: qCode, queueName, queueDescription, email (of owner), numFieldRequired, textFieldRequired, 
			// numFieldDescription, textFieldDescription,
			// isLocationRestricted, isPublic, maxSize, latitude, longitude, radius,
			// avgWaitTime, numUsersProcessed, numCurrentEntries
			// - or -: failure (qCodeInvalid)
	// pullUserInfo
		// Request: email
		// Response: email, firstName, lastName, queuesEntered (qCodes), queuesEnteredPositions, queuesManaging (qCodes)
			// - or- : failure (emailInvalid)
	// forceRefresh
		// (Force client to refresh if queue is modified)

public class Message implements Serializable {
	private static final long serialVersionUID = 14252135;
	
	private String type; // What type of message this is (e.g. userLoginRequest, removeUserResponse, etc.)
	private String responseStatus; // E.g. "success", "failure", "emailInvalid", etc.
	
	// ALL POSSIBLE FIELDS:
	// Queue info
	private String qCode;
	private String queueName;
	private String queueDescription;
	private String numFieldDescription;
	private String textFieldDescription;
	private boolean numFieldRequired;
	private boolean textFieldRequired;
	private boolean isLocationRestricted;
	private boolean isPublic;
	private int maxSize;
	private double radius;
	private double avgWaitTime;
	private int numUsersProcessed;
	private int numCurrentEntries;
	
	// User info
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private Integer numFieldInput; // For joining a queue, if necessary
	private String textFieldInput; // For joining a queue, if necessary
	
	// Location info
	private double longitude;
	private double latitude;

	// General info to display
	private Vector<String> queuesEnteredNames; // List of names for queues the inputted user is in
	private Vector<String> queuesEnteredCodes; // List of qCodes for queues the inputted user is in
	private Vector<Integer> queuesEnteredPositions; // List of positions (ints) for queues the inputted user is in
	private Vector<String> queuesManagingNames; // List of names for queues the inputted user is managing
	private Vector<String> queuesManagingCodes; // List of qCodes for queues the inputted user is managing
	

	// ---------- CONSTRUCTOR ----------
	// YOU MUST MANUALLY FILL OUT THE NECESSARY FIELDS FOR THE GIVEN TYPE! (Use setters)
	public Message(String type) {
		this.type = type;
	}
	
	// ---------- GETTERS/SETTERS ----------
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	public String getqCode() {
		return qCode;
	}
	public void setqCode(String qCode) {
		this.qCode = qCode;
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public String getQueueDescription() {
		return queueDescription;
	}
	public void setQueueDescription(String queueDescription) {
		this.queueDescription = queueDescription;
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

	public void setNumFieldInput(Integer numFieldInput) {
		this.numFieldInput = numFieldInput;
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
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
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
	public int getNumCurrentEntries() {
		return numCurrentEntries;
	}
	public void setNumCurrentEntries(int numCurrentEntries) {
		this.numCurrentEntries = numCurrentEntries;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getNumFieldInput() {
		return numFieldInput;
	}
	public void setNumFieldInput(int numFieldInput) {
		this.numFieldInput = numFieldInput;
	}
	public String getTextFieldInput() {
		return textFieldInput;
	}
	public void setTextFieldInput(String textFieldInput) {
		this.textFieldInput = textFieldInput;
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
	public Vector<String> getQueuesEnteredNames() {
		return queuesEnteredNames;
	}
	public void setQueuesEnteredNames(Vector<String> queuesEnteredNames) {
		this.queuesEnteredNames = queuesEnteredNames;
	}
	public Vector<String> getQueuesEnteredCodes() {
		return queuesEnteredCodes;
	}
	public void setQueuesEnteredCodes(Vector<String> queuesEnteredCodes) {
		this.queuesEnteredCodes = queuesEnteredCodes;
	}
	public Vector<Integer> getQueuesEnteredPositions() {
		return queuesEnteredPositions;
	}
	public void setQueuesEnteredPositions(Vector<Integer> queuesEnteredPositions) {
		this.queuesEnteredPositions = queuesEnteredPositions;
	}
	public Vector<String> getQueuesManagingNames() {
		return queuesManagingNames;
	}
	public void setQueuesManagingNames(Vector<String> queuesManagingNames) {
		this.queuesManagingNames = queuesManagingNames;
	}
	public Vector<String> getQueuesManagingCodes() {
		return queuesManagingCodes;
	}
	public void setQueuesManagingCodes(Vector<String> queuesManagingCodes) {
		this.queuesManagingCodes = queuesManagingCodes;
	}

}
