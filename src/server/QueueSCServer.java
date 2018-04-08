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
import queues.QueueManager;

@ServerEndpoint(value = "/ws",
	decoders = MessageDecoder.class, 
	encoders = MessageEncoder.class)
public class QueueSCServer {
	private static Vector<Session> sessions = new Vector<Session>();
	private QueueManager qm;
//	private UserManager um;
	private DBInterface dbInterface;
	
	@OnOpen
	public void open(Session session) {
		System.out.println("Connection made!");
		sessions.add(session);
	}
	
	@OnMessage
	public void onMessage(Message message, Session session) {
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
		
	}
	private void processDeleteQueueRequest(Message m, Session s) {
		
	}
	private void processEnqueueRequest(Message m, Session s) {
		
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
