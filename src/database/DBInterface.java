package database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;

import queues.Queue;
import queues.QueueEntry;
import users.User;

public class DBInterface {
	
	private static String baseDbURL = "jdbc:postgresql://queuescdb.canjkvgt5lzz.us-east-1.rds.amazonaws.com:5432/";
	private static String dbName = "mainDB";
	private static String dbUser = "QueueSCAdmin";
	private static String dbPassword = "bluesky4238";
	
	private JdbcPooledConnectionSource connectionSource = null;
	
	private Dao<User, String> userDao; // <class to be persisted, type of primary key>
	private Dao<Queue, String> queueDao;
	private Dao<QueueEntry, Integer> queueEntryDao; // assume queueEntry has auto-generated id

	
	public DBInterface() {
		setupDatabase();
	}
	
	private void setupDatabase() {
		try {
			// Connect to database
			connectionSource = new JdbcPooledConnectionSource(baseDbURL + dbName + "?user=" + dbUser + "&password=" + dbPassword);
			
			// Create Database Access Objects
			userDao = DaoManager.createDao(connectionSource, User.class);
			queueDao = DaoManager.createDao(connectionSource, Queue.class);
			queueEntryDao = DaoManager.createDao(connectionSource, QueueEntry.class);
			
			// Create database tables if they do not exist
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, Queue.class);
			TableUtils.createTable(connectionSource, QueueEntry.class);
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
	
	public User getUserFromDB(String userEmail) {
		try {
			User allegedUser = userDao.queryForId(userEmail);
		} catch (SQLException se) {
			System.out.println("Problem reading from db");
		}

	}
	
	public Queue getQueueFromDB() {
		
	}
	
	public boolean addQueueEntryToDB(QueueEntry qe) {
		// Returns true if the user was succesfully added
		
	}
	
	public boolean isUserInQueue(String userEmail, String qCode) {
		// Returns true if the user is in the queue in the DB
		
		// Get the user object
		User allegedUser = getUserFromDB()
		QueryBuilder<QueueEntry, Integer> queryBuilder =queueEntryDao.queryBuilder();
		// get the WHERE object to build our query
		Where<QueueEntry, Integer> where = queryBuilder.where();
		// the name field must be equal to "foo"
		where.eq(QueueEntry.u, userEmail);
		// and
		where.and();
		// the password field must be equal to "_secret"
		where.eq(, "_secret");
		PreparedQuery<Account> preparedQuery = queryBuilder.prepare();

	}
	
	public Vector<User> getUsersInQueue(String qCode) {
		// Returns a vector of user objects in queue; if queue DNE, returns empty vector
		if (doesQueueExist(qCode)) {
			
		}
	}
	
	public boolean doesQueueExist(String qCode) {
		// Returns true if a queue exists with that qCode
		try {
			Queue allegedQueue = queueDao.queryForId(qCode);
			if (allegedQueue == null) {
				return false;
			}
			return true;
		} catch (SQLException se) {
			// There was some problem accessing the db
			return false;
		}
	}
	
	public boolean addUsertoDB(User u) {
		// Returns true if the the user object was successfully added to DB
		if (doesUserExist(u.getEmail())) {
			return false;
		}
		try {
			userDao.create(u);
			return true;
		} catch (SQLException se) {
			// There was some kind of problem saving the user to the db
			return false;
		}
	}
	
	public boolean doesUserExist(String email) {
		try {
			User allegedUser = userDao.queryForId(email);
			if (allegedUser == null) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException se) {
			System.out.println("There was a problem querying the database.");
			return true;
		}
	}
	
	public boolean deleteQueue(String qCode) {
		// Returns true if the queue was deleted successfully
		
	}
	
	public boolean deleteUser(String email) {
		
	}
	
	public boolean removeUserFromQueue(String email, String qCode) {
		if ()
	}
	
	
	public int getPositionInQueue (String email, String qCode) {
		if (isUserInQueue(email, qCode)) {
			
		} else {
			return -1;
		}
	}
	
	public void advanceQueue (String qCode) {
		
	}
}
