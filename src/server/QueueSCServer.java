package server;

import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import database.DBInterface;
import queues.Queue;
import queues.QueueEntry;
import users.User;

@ServerEndpoint(value = "/ws",
	decoders = MessageDecoder.class, 
	encoders = MessageEncoder.class)

public class QueueSCServer {
	private static Vector<Session> sessions;
//	private QueueManager qm;
//	private UserManager um;
	private DBInterface dbInterface;
	
	public QueueSCServer() {
		sessions = new Vector<Session>();
		dbInterface = new DBInterface();
		prepDemo(dbInterface);
	}
	
	// Prep demo for given DB Interface
	private void prepDemo(DBInterface dbInterface) {
		// Clear DB
		dbInterface.clearTables();
		
		// ----- Create users and add to DB ----- //
		// Email: president@usc.edu, Password: fucla
		User nikias = new User("Max", "Nikias", "president@usc.edu");
		nikias.setPasswordHash(nikias.hashPassword("fucla"));
		dbInterface.addUsertoDB(nikias);
		
		// Email: lucashu@usc.edu, Password: queuescrules
		User lucas = new User("Lucas", "Hu", "lucashu@usc.edu"); 
		lucas.setPasswordHash(lucas.hashPassword("queuescrules"));
		dbInterface.addUsertoDB(lucas);
		
		// Email: nthakur@usc.edu, Password: password123
		User natasha = new User("Natasha", "Thakur", "nthakur@usc.edu"); 
		natasha.setPasswordHash(natasha.hashPassword("password123"));
		dbInterface.addUsertoDB(natasha);
		
		// Email: cote@usc.edu, Password: pequalsnp
		User cote = new User("Aaron", "Cote", "cote@usc.edu");
		cote.setPasswordHash(cote.hashPassword("pequalsnp"));
		dbInterface.addUsertoDB(cote);
		
		// ----- Create queues and add to DB ----- //
		Queue cpQueue = new Queue("CP1234", "CS270 CP Help", "Week of 4/23 270 questions", 
				cote, false, false, false, false, 15); // Managed by Cote
		dbInterface.addQueueToDB(cpQueue);
		Queue crepesQueue = new Queue("CREPE1", "Village Crepes", "Village crepe line",
				nikias, false, true, true, false, 10); // Managed by Nikias
		dbInterface.addQueueToDB(crepesQueue);
		
		// ----- Add queue entries to DB ----- //
		QueueEntry cpQE1 = new QueueEntry(nikias, cpQueue, "", 0);
		dbInterface.addQueueEntryToDB(cpQE1);
		QueueEntry cpQE2 = new QueueEntry(lucas, cpQueue, "", 0);
		dbInterface.addQueueEntryToDB(cpQE2);
	}
	
	@OnOpen
	public void open(Session session) {
		System.out.println("Connection made!");
		sessions.add(session);
	}
	
	@OnMessage
	public void onMessage(Session session, Message message) {
		if (message.getType().equals("createQueueRequest")) {
			processCreateQueueRequest(message, session);
		} else if (message.getType().equals("deleteQueueRequest")) {
			processDeleteQueueRequest(message, session);
		} else if (message.getType().equals("enqueueRequest")) {
			processEnqueueRequest(message, session);
		} else if (message.getType().equals("dequeueRequest")) {
			processDequeueRequest(message, session);
		} else if (message.getType().equals("removeUserRequest")) {
			processRemoveUserRequest(message, session);
		} else if (message.getType().equals("registerUserRequest")) {
			processRegisterUserRequest(message, session);
		} else if (message.getType().equals("userLoginRequest")) {
			processUserLoginRequest(message, session);
		} else if (message.getType().equals("guestLoginRequest")) {
			processGuestLoginRequest(message, session);
		} else if (message.getType().equals("pullQueueInfoRequest")) {
			processPullQueueInfoRequest(message, session);
		} else if (message.getType().equals("pullUserInfoRequest")) {
			processPullUserInfoRequest(message, session);
		} else {
			System.out.println("Unknown message type received: " + message.getType());
		}
	} 
	
	@OnClose
	public void close(Session session) {
		System.out.println("Disconnecting!");
		sessions.remove(session);
	}
	
	@OnError
	public void error(Throwable error) {
		System.out.println("Error!");
		error.printStackTrace();
	}
	
	// ---------- PROCESS MESSAGES ---------- //
	// Send Message m to Session s. Return true if successful
	private boolean sendMessage(Message m, Session s) {
		synchronized(s) {
			try {
				s.getBasicRemote().sendObject(m);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
	
	// QUEUE FUNCTIONALITY
	private void processCreateQueueRequest(Message m, Session s) {
		String qCode = dbInterface.generateValidQCode();
		String name = m.getQueueName();
		System.out.println("Attempting to create queue with name: " + name);
		String description = m.getQueueDescription();
		String ownerEmail = m.getEmail();
		User owner = dbInterface.getUserFromDB(ownerEmail);
		boolean numFieldRequired = m.isNumFieldRequired();
		boolean textFieldRequired = m.isTextFieldRequired();
		boolean isLocationRestricted = m.isLocationRestricted();
		boolean isPublic = m.isPublic();
		int maxSize = m.getMaxSize();
		
		if (dbInterface.getQueueFromDB(qCode) != null) { // qCode is taken --> send error message
			Message resp = new Message("createQueueResposne");
			resp.setResponseStatus("qCodeTaken");
			sendMessage(resp, s);
			return;
		}
		
		Queue q = new Queue(qCode, name, description, owner, numFieldRequired, textFieldRequired, isLocationRestricted, isPublic, maxSize);
		
		// Add optional fields, if necessary
		if (numFieldRequired) {
			q.setNumFieldDescription(m.getNumFieldDescription());
		}
		if (textFieldRequired) {
			q.setTextFieldDescription(m.getTextFieldDescription());
		}
		if (isLocationRestricted) {
			q.setLatitude(m.getLatitude());
			q.setLongitude(m.getLongitude());
			q.setRadius(m.getRadius());
		}
		
		dbInterface.addQueueToDB(q);
		Message resp = new Message("createQueueResponse"); // Success message
		resp.setqCode(qCode);
		resp.setResponseStatus("success");
		sendMessage(resp, s);
	}
	private void processDeleteQueueRequest(Message m, Session s) {
		String qCode = m.getqCode();
		boolean isSuccess = dbInterface.deleteQueueFromDB(qCode); // Try to delete queue
		Message response = new Message("deleteQueueResponse");
		if (isSuccess) { // Success --> send "success"
			response.setResponseStatus("success");
		} else { // Failure (qCode not found) --> send "qCodeInvalid"
			response.setResponseStatus("qCodeInvalid");
		}
		sendMessage(response, s);
	}
	private void processEnqueueRequest(Message m, Session s) {
		String email = m.getEmail();
		String qCode = m.getqCode();
		System.out.println("Attempting to join queue with Q-Code: " + qCode);
		Integer numFieldInput = m.getNumFieldInput();
		String textFieldInput = m.getTextFieldInput();
		
		// User not found --> send error mesage
		boolean userFound = (dbInterface.getUserFromDB(email) != null);
		if (!userFound) { 
			Message response = new Message("enqueueResponse");
			response.setResponseStatus("emailInvalid");
			sendMessage(response, s);
			return;
		}
		
		// Queue not found --> send error message
		boolean queueFound = (dbInterface.getQueueFromDB(qCode) != null);
		if (!queueFound) {
			Message response = new Message("enqueueResponse");
			response.setResponseStatus("qCodeInvalid");
			sendMessage(response, s);
			return;
		}
		
		User u = dbInterface.getUserFromDB(email);
		Queue q = dbInterface.getQueueFromDB(qCode);
		
		// Check if queue is already full
		if (dbInterface.getEntriesInQueue(qCode).size() >= q.getMaxSize()) {
			Message response = new Message("enqueueResponse");
			response.setResponseStatus("queueAlreadyFull");
			sendMessage(response, s);
			return;
		}
		
		// Check if user is already in queue
		if (dbInterface.getQueueEntryFromDB(qCode, email) != null) {
			Message response = new Message("enqueueResponse");
			response.setResponseStatus("userAlreadyInQueue");
			sendMessage(response, s);
			return;
		}
		
		// Check for valid numerical input (if necessary)
		if (q.isNumFieldRequired() && numFieldInput == null) {
			Message response = new Message("enqueueResponse");
			response.setResponseStatus("missingFormInput");
			sendMessage(response, s);
			return;
		}
		// Check for valid text input (if necessary)
		if (q.isTextFieldRequired() && (textFieldInput == null || textFieldInput.equals(""))) {
			Message response = new Message("enqueueResponse");
			response.setResponseStatus("missingFormInput");
			sendMessage(response, s);
			return;
		}
		
		// Check for user within bounds (if necessary)
		if (q.isLocationRestricted()) {
			double latitude = m.getLatitude();
			double longitude = m.getLongitude();
			if (!q.inRange(latitude, longitude)) {
				Message resp = new Message("enqueueResponse");
				resp.setResponseStatus("userOutOfRange");
				sendMessage(resp, s);
				return;
			}
		}
		
		// Village Crepes demo: send userOutOfRange
		if (q.getName().equals("Village Crepes")) {
			Message resp = new Message("enqueueResponse");
			resp.setResponseStatus("userOutOfRange");
			sendMessage(resp, s);
			return;
		}
		
		// Add queue entry
		QueueEntry qe = new QueueEntry(u, q, textFieldInput, numFieldInput);
		qe.setPosition(dbInterface.getNumEntriesInQueue(qCode)+1);
		dbInterface.addQueueEntryToDB(qe);
		Message resp = new Message("enqueueResponse");
		resp.setResponseStatus("success");
		sendMessage(resp, s);
		
		// Force other users looking at this queue to refresh
		forceOthersRefresh(qCode, s);
	}
	private void processDequeueRequest(Message m, Session s) {
		String qCode = m.getqCode();
		if (dbInterface.getQueueFromDB(qCode) == null) { // Queue does not exist --> send error message
			Message resp = new Message("dequeueResponse");
			resp.setResponseStatus("qCodeInvalid");
			sendMessage(resp, s);
			return;
		}
		
		// Success!
		QueueEntry qe = dbInterface.advanceQueue(qCode);
		String queueName = qe.getQueue().getName();
		String email = qe.getUser().getEmail();
		
		// Send email notification to user
		EmailSender es = new EmailSender(email, queueName);
		es.start();
		
		// Send response to client
		Message resp = new Message("dequeueResponse");
		resp.setResponseStatus("success");
		sendMessage(resp, s);
		
		// Force other users looking at this queue to refresh
		forceOthersRefresh(qCode, s);
	}
	private void processRemoveUserRequest(Message m, Session s) {
		String qCode = m.getqCode();
		String email = m.getEmail();
		
		// User not found --> send error mesage
		boolean userFound = (dbInterface.getUserFromDB(email) != null);
		if (!userFound) { 
			Message response = new Message("removeUserResponse");
			response.setResponseStatus("emailInvalid");
			sendMessage(response, s);
			return;
		}
		
		// Queue not found --> send error message
		boolean queueFound = (dbInterface.getQueueFromDB(qCode) != null);
		if (!queueFound) {
			Message response = new Message("removeUserResponse");
			response.setResponseStatus("qCodeInvalid");
			sendMessage(response, s);
			return;
		}
		
		// Make sure user is currently in queue
		if (dbInterface.getQueueEntryFromDB(email, qCode) == null) {
			Message response = new Message("removeUserResponse");
			response.setResponseStatus("userNotInQueue");
			sendMessage(response, s);
			return;
		}
		
		// Remove user
		dbInterface.removeUserFromQueue(email, qCode);
		Message response = new Message("removeUserResponse");
		response.setResponseStatus("success");
		sendMessage(response, s);
		
		forceOthersRefresh(qCode, s);
	}
	
	// USER FUNCTIONALITY
	private void processRegisterUserRequest(Message m, Session s) {
		String email = m.getEmail();
		String firstName = m.getFirstName();
		String lastName = m.getLastName();
		String password = m.getPassword();
		
		User u = new User(firstName, lastName, email);
		u.setPasswordHash(u.hashPassword(password));
		
		Message response = new Message("registerUserResponse");
		
		// User already exists
		if (dbInterface.getUserFromDB(email) != null) {
			response.setResponseStatus("emailTaken");
			sendMessage(response, s);
			return;
		}
		
		// Add user to DB, send success message
		dbInterface.addUsertoDB(u);
		response.setResponseStatus("success");
		sendMessage(response, s);
	}
	private void processUserLoginRequest(Message m, Session s) {
		String email = m.getEmail();
		String password = m.getPassword();
		User u = dbInterface.getUserFromDB(email);
		Message response = new Message("userLoginResponse");
		
		// User not found
		if (u == null) {
			response.setResponseStatus("emailDoesNotExist");
			sendMessage(response, s);
			return;
		}
		
		// Incorrect password
		if (!u.isPasswordValid(password)) {
			response.setResponseStatus("incorrectPassword");
			sendMessage(response, s);
			return;
		}
		
		// Else, successfully logged in
		response.setResponseStatus("success");
		response.setEmail(email);
		sendMessage(response, s);
	}
	private void processGuestLoginRequest(Message m, Session s) {
		String email = m.getEmail();
		String firstName = m.getFirstName();
		String lastName = m.getLastName();
		String qCode = m.getqCode();
		Message response = new Message("guestLoginResponse");
		
		// User already exists
		if (dbInterface.getUserFromDB(email) != null) {
			response.setResponseStatus("emailTaken");
			sendMessage(response, s);
			return;
		}
		
		// Queue not found --> send error message
		boolean queueFound = (dbInterface.getQueueFromDB(qCode) != null);
		if (!queueFound) {
			response.setResponseStatus("qCodeInvalid");
			sendMessage(response, s);
			return;
		}
		
		User u = new User(firstName, lastName, email);
		u.setIsGuest(true); // TODO: use setter
		
		// Add guest user to DB
		dbInterface.addUsertoDB(u);
		response.setResponseStatus("success");
		response.setEmail(email);
		sendMessage(response, s);
	}
	
	// LOAD INFO TO DISPLAY
	// Pull info on 1 specific queue
	private void processPullQueueInfoRequest(Message m, Session s) {
		String qCode = m.getqCode();
		Queue q = dbInterface.getQueueFromDB(qCode);
		Message response = new Message("pullQueueInfoResponse");
		
		// Queue not found --> send error message
		if (q == null) {
			response.setResponseStatus("qCodeInvalid");
			sendMessage(response, s);
			return;
		}
		
		// Fill all queue info fields
		response.setqCode(qCode);
		response.setQueueName(q.getName());
		response.setQueueDescription(q.getDescription());
		response.setEmail(q.getOwner().getEmail());
		response.setNumFieldRequired(q.isNumFieldRequired());
		response.setTextFieldRequired(q.isTextFieldRequired());
		response.setNumFieldDescription(q.getNumFieldDescription());
		response.setTextFieldDescription(q.getTextFieldDescription());
		response.setLocationRestricted(q.isLocationRestricted());
		response.setLatitude(q.getLatitude());
		response.setLongitude(q.getLongitude());
		response.setRadius(q.getRadius());
		response.setPublic(q.isPublic());
		response.setMaxSize(q.getMaxSize());
		response.setAvgWaitTime(q.getAvgWaitTime());
		response.setNumUsersProcessed(q.getNumUsersProcessed());
		response.setNumCurrentEntries(dbInterface.getEntriesInQueue(qCode).size());
		
		// Set usersInQueue (names, emails)
		Vector<QueueEntry> qes = new Vector<QueueEntry>(dbInterface.getEntriesInQueue(qCode));
		Collections.sort(qes);
		Vector<String> usersInQueueNames = new Vector<String>();
		Vector<String> usersInQueueEmails = new Vector<String>();
		for (int i = 0; i < qes.size(); i++) {
			User u = qes.get(i).getUser();
			usersInQueueNames.add(u.getFirstName() + " " + u.getLastName());
			usersInQueueEmails.add(u.getEmail());
		}
		response.setUsersInQueueNames(usersInQueueNames);
		response.setUsersInQueueEmails(usersInQueueEmails);
		
		// Send response
		response.setResponseStatus("success");
		sendMessage(response, s);
	}
	
	// Pull info on 1 given user
	private void processPullUserInfoRequest(Message m, Session s) {
		String email = m.getEmail();
		User u = dbInterface.getUserFromDB(email);
		Message response = new Message("pullUserInfoResponse");
		if (u == null) { // User not found
			response.setResponseStatus("emailInvalid");
			sendMessage(response, s);
			return;
		}
		response.setEmail(email);
		response.setFirstName(u.getFirstName());
		response.setLastName(u.getLastName());
		
		// Get info about what queues the user is in/managing
		Vector<QueueEntry> userQueueEntries = new Vector<QueueEntry>(dbInterface.getQueueEntriesForUser(email));
		Vector<Queue> userManagedQueues = new Vector<Queue>(dbInterface.getQueuesManagedByUser(email));
		
		Vector<String> queuesEnteredNames = new Vector<String>();
		Vector<String> queuesEnteredCodes = new Vector<String>();
		Vector<Integer> queuesEnteredPositions = new Vector<Integer>();
		Vector<String> queuesManagingNames = new Vector<String>();
		Vector<String> queuesManagingCodes = new Vector<String>();
		
		for (QueueEntry qe : userQueueEntries) {
			String qCode = qe.getQueue().getqCode();
			String name = dbInterface.getQueueFromDB(qCode).getName();
			
			queuesEnteredNames.add(name);
			queuesEnteredCodes.add(qCode);
			queuesEnteredPositions.add(qe.getPosition());
		}
		
		for (Queue q : userManagedQueues) {
			queuesManagingNames.add(q.getName());
			queuesManagingCodes.add(q.getqCode());
		}
		
		response.setQueuesEnteredCodes(queuesEnteredCodes);
		response.setQueuesEnteredNames(queuesEnteredNames);
		response.setQueuesEnteredPositions(queuesEnteredPositions);
		response.setQueuesManagingCodes(queuesManagingCodes);
		response.setQueuesManagingNames(queuesManagingNames);
		
		response.setResponseStatus("success");
		sendMessage(response, s);
	}
	
	// OTHER
	// Send a "refresh" message to every relevant session, given the inputted qCode
	private void forceOthersRefresh(String qCode, Session originalSession) {
		for (Session s : sessions) {
			if (s == originalSession) { // don't force user who created the change to refresh
				continue;
			}
			Message m = new Message("forceRefresh");
			m.setqCode(qCode);
			sendMessage(m, s);
		}
	}
}

