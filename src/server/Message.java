package server;

import java.io.Serializable;

// ---------- POSSIBLE MESSAGE TYPES ----------
// QUEUE MODIFICATION:
	// CreateQueue
		// Request: qCode, queueName, queueDescription, numFieldDescription, textFieldDescription, 
			// isLocationRestricted, isPublic, maxSize, latitude, longitude, radius
		// Response: success/failure (qCodeTaken)
	// DeleteQueue
		// Request: qCode
		// Response: success/failure (qCodeInvalid)
	// Enqueue
		// Request: qCode, email, numFieldInput, textFieldInput, latitude, longitude
		// Response: success/failure (qCodeInvalid, emailInvalid)
	// Dequeue
		// Request: qCode
		// Response: success/failure (qCodeInvalid)
	// RemoveUser
		// Request: qCode, email
		// Response: success/failure (qCodeInvalid, emailInvalid)

// USER:
	// RegisterUser
		// Request: email, firstName, lastName, password
		// Response: success/failure (emailTaken, emailInvalid)
	// UserLogin
		// Request: email, password
		// Response: success/failure (emailDoesNotExist, incorrectPassword)
	// GuestLogin
		// Request: email, firstName, lastName, qCode
		// Response: success/failure (emailTaken, qCodeInvalid)

// LOAD INFO:
	// PullQueueInfo
		// Request: qCode
		// Response: qCode, queueName, queueDescription, numFieldDescription, textFieldDescription,
			// isLocationRestricted, isPublic, maxSize, latitude, longitude, radius,
			// avgWaitTime, numUsersProcessed, numCurrentEntries
			// - or -: failure (qCodeInvalid)
	// PullUserInfo
		// Request: email
		// Response: email, firstName, lastName, queuesEntered, queuesEnteredPositions, queuesManaging
			// - or- : failure (emailInvalid)
	// ForceRefresh
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
	private int numFieldInput; // For joining a queue, if necessary
	private String textFieldInput; // For joining a queue, if necessary
	
	// Location info
	private double longitude;
	private double latitude;

	// Info to display
	private String queuesEnteredNames; // Semicolon-separated list of names for queues the inputted user is in
	private String queuesEnteredCodes; // Semicolon-separated list of qCodes for queues the inputted user is in
	private String queuesEnteredPositions; // Semicolon-separated list of positions (ints) for queues the inputted user is in
	private String queuesManagingNames; // Semicolon-separated list of names for queues the inputted user is managing
	private String queuesManagingCodes; // Semicolon-separated list of qCodes for queues the inputted user is managing
}
