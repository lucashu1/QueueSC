package database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;

import queues.Queue;
import queues.QueueEntry;
import users.User;

public class DBInterface {
	
	private static final String baseDbURL = "jdbc:postgresql://queuescdb.canjkvgt5lzz.us-east-1.rds.amazonaws.com:5432/";
	private static final String dbName = "mainDB";
	private static final String dbUser = "QueueSCAdmin";
	private static final String dbPassword = "bluesky4238";
	
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
		if (getQueueEntryFromDB(qe.getQueue().getqCode(), qe.getUser().getEmail()) == null) {
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
		try {
			QueryBuilder<QueueEntry, Integer> queryBuilder = queueEntryDao.queryBuilder();
			Where<QueueEntry, Integer> where = queryBuilder.where();
			// the user in the entry must be the one we are looking for
			where.eq(QueueEntry.USER_FIELD_NAME, allegedUser);
			// and
			where.and();
			// it must be the correct queue
			where.eq(QueueEntry.QUEUE_FIELD_NAME, allegedQueue);
			PreparedQuery<QueueEntry> preparedQuery = queryBuilder.prepare();
			
			// Run the query and return the result if appropriate
			List<QueueEntry> results = queueEntryDao.query(preparedQuery);
			if (results.size() != 1) {
				return (QueueEntry) null;
			} else {
				return results.get(0);
			}
		}
		catch (SQLException se) {
			System.out.println("Problem reading from database");
			return (QueueEntry) null; 
		}

	}
	
	public QueueEntry getQueueEntryFromDBByPosition(String qCode, int position) {
		// Get the queue object
		Queue allegedQueue = getQueueFromDB(qCode);
		if (allegedQueue == null) {
			return (QueueEntry) null;
		}
		
		// Build the query
		try {
			QueryBuilder<QueueEntry, Integer> queryBuilder = queueEntryDao.queryBuilder();
			Where<QueueEntry, Integer> where = queryBuilder.where();
			// the queueEntry must be in the correct queue
			where.eq(QueueEntry.QUEUE_FIELD_NAME, allegedQueue);
			// and
			where.and();
			// the queueEntry must be the first in that queue
			where.eq(QueueEntry.POSITION_FIELD_NAME, 1);
			PreparedQuery<QueueEntry> preparedQuery = queryBuilder.prepare();
			
			// Run the query and return the result if appropriate
			List<QueueEntry> results = queueEntryDao.query(preparedQuery);
			if (results.size() != 1) {
				return (QueueEntry) null;
			} else {
				return results.get(0);
			}
		} catch (SQLException se) {
			System.out.println("Problem reading from database");
			return (QueueEntry) null; 		
		}

	}
	
	////////////////////////////////////////////
	//////// Update Existing rows in DB ////////
	////////////////////////////////////////////
	public boolean incrementNumUsersProcessed(String qCode) {
		// Get the current value of the "numUsersProcessed" field
		Queue thisQueue = getQueueFromDB(qCode);
		if (thisQueue == null) {
			return false;
		}
		int curVal = thisQueue.getNumUsersProcessed();

		try {
			// Build the update query
			UpdateBuilder<Queue, String> updateBuilder = queueDao.updateBuilder();
			// Only get the queue 
			updateBuilder.where().eq(Queue.QCODE_FIELD_NAME, qCode);
			// Increment the field
			updateBuilder.updateColumnValue(Queue.NUMUSERSPROCESSED_FIELD_NAME, curVal + 1);

			// Run the query
			updateBuilder.update();
			return true;
		}
		catch (SQLException se) {
			System.out.println("There was a problem with the database.");
			return false;
		}		
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
		deleteQueueEntryFromDB(topOfQueue.getQueue().getqCode(), topOfQueue.getUser().getEmail());
		
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
		
		try {
			// Build query for the Queue
			QueryBuilder<Queue, String> queueQB = queueDao.queryBuilder();
			queueQB.where().eq(Queue.QCODE_FIELD_NAME, qCode);
			
			// Join with query for QueueEntry
			QueryBuilder<QueueEntry, Integer> queueEntryQB = queueEntryDao.queryBuilder();
			
			return queueEntryQB.join(queueQB).query();
		} catch (SQLException se) {
			return new Vector<QueueEntry>();
		}
	}
	
	
	public List<QueueEntry> getQueueEntriesForUser(String email) {
		try {
			// Build query for User
			QueryBuilder<User, String> userQB = userDao.queryBuilder();
			userQB.where().eq(User.EMAIL_FIELD_NAME, email);
			
			// Join with query for QueueEntries
			QueryBuilder<QueueEntry, Integer> queueEntryQB = queueEntryDao.queryBuilder();
			
			return queueEntryQB.join(userQB).query();
		} catch (SQLException se) {
			return new Vector<QueueEntry>();
		}
	}
	
	public List<Queue> getQueuesManagedByUser(String email) {
		try {
			// Build query for User
			QueryBuilder<User, String> userQB = userDao.queryBuilder();
			userQB.where().eq(User.EMAIL_FIELD_NAME, email);
			
			// Join with query for Queues
			QueryBuilder<Queue, String> queueQB = queueDao.queryBuilder();
			
			return queueQB.join(userQB).query();
		} catch (SQLException se) {
			return new Vector<Queue>();
		}
	}

	/////////////////////////////
	/////// Delete models ///////
	/////////////////////////////
	public boolean deleteUserFromDB(String email) {
		try {
			userDao.deleteById(email);	
			return true;
		}
		catch (SQLException se) {
			return false;
		}
	}

	
	public boolean deleteQueueFromDB(String qCode) {
		// Returns true if the queue was deleted successfully
		try {
			queueDao.deleteById(qCode);	
			return true;
		}
		catch (SQLException se) {
			return false;
		}
	}
	
	
	public boolean deleteQueueEntryFromDB(String qCode, String email) {
		try {
			// Build query for the queue
			QueryBuilder<Queue, String> queueQB = queueDao.queryBuilder();
			queueQB.where().eq(Queue.QCODE_FIELD_NAME, qCode);
			
			// build query for the user
			QueryBuilder<User, String> userQB = userDao.queryBuilder();
			queueQB.where().eq(User.EMAIL_FIELD_NAME, email);
			
			// Combine into query for QueueEntry
			QueryBuilder<QueueEntry, Integer> queueEntryQB = queueEntryDao.queryBuilder();
			List<QueueEntry> results = queueEntryQB.join(queueQB).join(userQB).query();
			if (results.size() != 1) {
				return false;
			}
			
			queueEntryDao.delete(results.get(0));
			return true;
		}
		catch (SQLException se) {
			System.out.println("There was a problem with the database.");
			return false;
		}		
	}
	
}
