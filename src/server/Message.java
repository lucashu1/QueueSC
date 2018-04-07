package server;

// POSSIBLE MESSAGE TYPES:
	// Enqueue
	// Dequeue
	// Remove user
	// Create user
	// Create queue
	// Delete queue

public class Message {
	private String type;
	private String userEmail;
	private String qCode;
}
