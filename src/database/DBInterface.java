package database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import queues.Queue;
import users.User;

public class DBInterface {
	
	private static String jdbcURL = "jdbc:postgresql://queuescdb.canjkvgt5lzz.us-east-1.rds.amazonaws.com:5432/mainDB";
	
	private JdbcPooledConnectionSource connectionSource = null;
	
	private Dao<User, String> userDao; // <class to be persisted, type of primary key>
	private Dao<Queue, String> queueDao;
	private Dao<QueueEntry, Integer> queueEntryDao; // assume queueEntry has auto-generated 

	
	public DBInterface() {
		setupDatabase();
	}
	
	private void setupDatabase() {
		try {
			// Connect to database
			connectionSource = new JdbcPooledConnectionSource(jdbcURL);
			// Create Database Access Objects
			userDao = DaoManager.createDao(connectionSource, User.class);
			queueDao = DaoManager.createDao(connectionSource, Queue.class);
			
			// Create database tables if they do not exist
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, Queue.class);
			
		}
		catch (Exception e) {
			System.out.println("There is a problem connecting to the database");
		}
		
	}
	
	public boolean close() {
		if (connectionSource != null) {
			try {
				connectionSource.close();
			}
			catch (IOException ioe) {
				return false;
			}
		}
		return true;
	}
	
	public boolean addUserToQueue(User u, Queue q) {
		// Returns true if the user was succesfully added
		
	}
	
	public boolean isUserInQueue(User u, String queueName) {
		// Returns true if the user is in the queue in the DB
		
	}
	
	public Vector<User> getUsersInQueue(Queue q) {
		// Returns a vector of user objects in queue; if queue DNE, returns empty vector
		
	}
	
	public boolean addUsertoDB(User u) {
		// Returns true if the the user object was successfully added to DB
		
	}
	
	public boolean deleteQueue(Queue q) {
		// Returns true if the queue was deleted sucessfully
	}
	
}
