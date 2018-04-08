package server;

import java.io.IOException;
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
import queues.QueueManager;
import users.User;

@ServerEndpoint(value = "/ws",
	decoders = MessageDecoder.class, 
	encoders = MessageEncoder.class)

public class QueueSCServer {
	private static Vector<Session> sessions = new Vector<Session>();
//	private QueueManager qm;
//	private UserManager um;
	private DBInterface dbInterface;
	
	@OnOpen
	public void open(Session session) {
		System.out.println("Connection made!");
		sessions.add(session);
	}
	
	@OnMessage
	public void onMessage(Session session, Message message) {
		// TODO: handle message
		System.out.println(message);
		try {
			for(Session s : sessions) {
				s.getBasicRemote().sendObject(message);
			}
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
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
	}
	
	// ---------- PROCESS MESSAGES ---------- //
	// Send Message m to Session s. Return true if successful
	private boolean sendMessage(Message m, Session s) {
		try {
			s.getBasicRemote().sendObject(m);
		} catch (IOException | EncodeException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	// QUEUE FUNCTIONALITY
	private void processCreateQueueRequest(Message m, Session s) {
		String qCode = m.getqCode();
		String name = m.getQueueName();
		String description = m.getQueueDescription();
		String owner = m.getEmail();
		boolean numFieldRequired = m.isNumFieldRequired();
		boolean textFieldRequired = m.isTextFieldRequired();
		boolean isLocationRestricted = m.isLocationRestricted();
		boolean isPublic = m.isPublic();
		int maxSize = m.getMaxSize();
		
		if (dbInterface.doesQueueExist(qCode)) { // qCode is taken --> send error message
			Message resp = new Message("createQueueResposne");
			resp.setResponseStatus("qCodeTaken");
			sendMessage(resp, s);
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
	}
	private void processDeleteQueueRequest(Message m, Session s) {
		String qCode = m.getqCode();
		boolean isSuccess = dbInterface.deleteQueue(qCode); // Try to delete queue
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
		Integer numFieldInput = m.getNumFieldInput();
		String textFieldInput = m.getTextFieldInput();
		
		// User not found --> send error mesage
		boolean userFound = dbInterface.doesUserExist(email);
		if (!userFound) { 
			Message response = new Message("enqueueResponse");
			response.setResponseStatus("emailInvalid");
			sendMessage(response, s);
		}
		
		// Queue not found --> send error message
		boolean queueFound = dbInterface.doesQueueExist(qCode);
		if (!queueFound) {
			Message response = new Message("enqueueResponse");
			response.setResponseStatus("qCodeInvalid");
			sendMessage(response, s);
		}
		
		User u = dbInterface.getUserFromDB(email);
		Queue q = dbInterface.getQueueFromDB(qCode);
		
		// Check for valid numerical input (if necessary)
		if (q.isNumFieldRequired() && numFieldInput == null) {
			Message response = new Message("enqueueResponse");
			response.setResponseStatus("missingFormInput");
			sendMessage(response, s);
		}
		// Check for valid text input (if necessary)
		if (q.isTextFieldRequired() && (textFieldInput == null || textFieldInput.equals(""))) {
			Message response = new Message("enqueueResponse");
			response.setResponseStatus("missingFormInput");
			sendMessage(response, s);
		}
		
		// Check for user within bounds (if necessary)
		if (q.isLocationRestricted()) {
			double latitude = m.getLatitude();
			double longitude = m.getLongitude();
			if (!q.inRange(latitude, longitude)) {
				Message resp = new Message("enqueueResponse");
				resp.setResponseStatus("userOutOfRange");
				sendMessage(resp, s);
			}
		}
		
		// Add queue entry
		QueueEntry qe = new QueueEntry(u, q, textFieldInput, numFieldInput);
		dbInterface.addQueueEntryToDB(qe);
	}
	private void processDequeueRequest(Message m, Session s) {
		
	}
	private void processRemoveUserRequest(Message m, Session s) {
		
	}
	
	// USER FUNCTIONALITY
	private void processRegisterUserRequest(Message m, Session s) {
		
	}
	private void processUserLoginRequest(Message m, Session s) {
		
	}
	private void processGuestLoginRequest(Message m, Session s) {
		
	}
	
	// LOAD INFO TO DISPLAY
	private void processPullQueueInfoRequest(Message m, Session s) {
		
	}
	private void processUserInfoRequest(Message m, Session s) {
		
	}
	
	// OTHER
	// Send a "refresh" message to every session
	private void forceAllSessionsRefresh() {
		for (Session s : sessions) {
			Message m = new Message("forceRefresh");
			sendMessage(m, s);
		}
	}
	
}
