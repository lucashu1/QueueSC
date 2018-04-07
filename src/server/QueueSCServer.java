package server;

import java.io.IOException;
import java.util.Vector;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import queues.QueueManager;

@ServerEndpoint(value = "/ws")
public class QueueSCServer {
	private static Vector<Session> sessions = new Vector<Session>();
	private QueueManager qm;
//	private UserManager um;
	
	@OnOpen
	public void open(Session session) {
		System.out.println("Connection made!");
		sessions.add(session);
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println(message);
		try {
			for(Session s : sessions) {
				s.getBasicRemote().sendText(message);
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
			close(session);
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
}
