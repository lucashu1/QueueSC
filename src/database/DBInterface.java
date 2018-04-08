package database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
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
	
	////////////////////////////
	///// Add models to DB /////
	////////////////////////////
	
	public boolean addUsertoDB(User u) {
		// Returns true if the the user object was successfully added to DB
		if (getUserFromDB(u.getEmail()) == null) {
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
	
	public boolean addQueueToDB(Queue q) {
		// Returns true if the the queue object was successfully added to DB
		if (getQueueFromDB(q.getqCode()) == null) {
			return false;
		}
		try {
			queueDao.create(q);
			return true;
		} catch (SQLException se) {
			// There was some kind of problem saving the user to the db
			return false;
		}
	}
	
	public boolean addQueueEntryToDB(QueueEntry qe) {
		// Returns true if the the queue object was successfully added to DB
		if (getQueueEntryFromDB(qe.getQ().getqCode(), qe.getUser().getEmail()) == null) {
			return false;
		}
		try {
			queueEntryDao.create(qe);
			return true;
		} catch (SQLException se) {
			// There was some kind of problem saving the user to the db
			return false;
		}			
	}
	
	////////////////////////////
	//// Get models from DB ////
	////////////////////////////
	
	public User getUserFromDB(String userEmail) {
		try {
			User allegedUser = userDao.queryForId(userEmail);
			return allegedUser;
		} catch (SQLException se) {
			System.out.println("Problem reading from db");
			return (User) null;
		}
	}
	
	public Queue getQueueFromDB(String qCode) {
		try {
			Queue allegedQueue = queueDao.queryForId(qCode);
			return allegedQueue;
		} catch (SQLException se) {
			System.out.println("Problem reading from db");
			return (Queue) null;
		}
	}
	
	public QueueEntry getQueueEntryFromDB(String qCode, String email) {
		// Get the user object
		User allegedUser = getUserFromDB(email);
		if (allegedUser == null) {
			return (QueueEntry) null;
		}
		
		// Get the queue object
		Queue allegedQueue = getQueueFromDB(qCode);
		if (allegedQueue == null) {
			return (QueueEntry) null;
		}
		
		// Build the query
		QueryBuilder<QueueEntry, Integer> queryBuilder = queueEntryDao.queryBuilder();
		Where<QueueEntry, Integer> where = queryBuilder.where();
		// the user in the entry must be the one we are looking for
		where.eq(QueueEntry.u, allegedUser);
		// and
		where.and();
		// it must be the correct queue
		where.eq(QueueEntry.q, allegedQueue);
		PreparedQuery<QueueEntry> preparedQuery = queryBuilder.prepare();
		
		// Run the query and return the result if appropriate
		List<QueueEntry> results = queueEntryDao.query(preparedQuery);
		if (results.size() != 1) {
			return (QueueEntry) null;
		} else {
			return results.get(0);
		}
	}
	
	public QueueEntry getQueueEntryFromDBByPosition(String qCode, int position) {
		// Get the queue object
		Queue allegedQueue = getQueueFromDB(qCode);
		if (allegedQueue == null) {
			return (QueueEntry) null;
		}
		
		// Build the query
		QueryBuilder<QueueEntry, Integer> queryBuilder = queueEntryDao.queryBuilder();
		Where<QueueEntry, Integer> where = queryBuilder.where();
		// the queueEntry must be in the correct queue
		where.eq(QueueEntry.q, allegedQueue);
		// and
		where.and();
		// the queueEntry must be the first in that queue
		where.eq(QueueEntry.position, 1);
		PreparedQuery<QueueEntry> preparedQuery = queryBuilder.prepare();
		
		// Run the query and return the result if appropriate
		List<QueueEntry> results = queueEntryDao.query(preparedQuery);
		if (results.size() != 1) {
			return (QueueEntry) null;
		} else {
			return results.get(0);
		}
	}
	
	////////////////////////////////////////////
	//////// Update Existing rows in DB ////////
	////////////////////////////////////////////
	public boolean incrementNumUsersProcessed(String qCode) {
	 	
		UpdateBuilder<Queue, String> updateBuilder = queueDao.updateBuilder();
		// update the password to be "none"
		updateBuilder.updateColumnValue("password", "none");
		// only update the rows where password is null
		updateBuilder.where().eq(Queue.qCode, qCode);
		updateBuilder.update();
	}

	
	
	
	/////////////////////////////
	////// Queue Modifiers //////
	/////////////////////////////
	//TODO:
	
	public void updateQueuePositions (String qCode) {
		
	}
	
	public void removeUserFromQueue (String email, String qCode) {
		// remove user from queue
		
		//update the positions of the people in the queue
		updateQueuePositions(qCode);
		
	}
	
	public QueueEntry advanceQueue (String qCode) {
		// returns the queueEntry that will be popped from the queue
		QueueEntry topOfQueue = getQueueEntryFromDBByPosition(qCode, 1);
		deleteQueueEntryFromDB()
		
		// Update the average waiting time
		
		// increment num users processed
		
		// update the row in the database
		
	}
	
	/////////////////////////////
	/////// Queue Queries ///////
	/////////////////////////////
		
	public List<QueueEntry> getEntriesInQueue(String qCode) { 
		// Returns a vector of QueueEntry objects for that queue; if queue DNE, returns empty vector
		
		// See if that queue exists and get the object
		Queue q = getQueueFromDB(qCode);
		if (q == null) {
			return new Vector<QueueEntry>();
		}
		
		// Build the query
		QueryBuilder<QueueEntry, Integer> queryBuilder = queueEntryDao.queryBuilder();
		Where<QueueEntry, Integer> where = queryBuilder.where();
		// it must be the correct queue
		where.eq(QueueEntry.q, q);
		PreparedQuery<QueueEntry> preparedQuery = queryBuilder.prepare();		
		
		// Run the query and return the result if appropriate
		List<QueueEntry> results = queueEntryDao.query(preparedQuery);
		if (results.size() == 0) {
			return new Vector<QueueEntry>();
		} else {
			return results;
		}
	}
	

	/////////////////////////////
	/////// Delete models ///////
	/////////////////////////////
	// TODO:

	public boolean deleteUserFromDB(String email) {
		userDao.deleteById(email);	
	}

	public boolean deleteQueueFromDB(String qCode) {
		// Returns true if the queue was deleted successfully
		
	}
	
	public boolean deleteQueueEntryFromDB(String) {
		
	}
	

	
	


}
