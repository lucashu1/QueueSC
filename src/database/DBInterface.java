package database;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
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
	
	private static final String baseDbURL = "jdbc:postgresql://queuescdb.canjkvgt5lzz.us-east-1.rds.amazonaws.com:5432/";
	private static final String dbName = "mainDB";
	private static final String dbUser = "QueueSCAdmin";
	private static final String dbPassword = "bluesky4238";
	
	private JdbcPooledConnectionSource connectionSource = null;
	
	private Dao<User, String> userDao; // <class to be persisted, type of primary key>
	private Dao<Queue, String> queueDao;
	private Dao<QueueEntry, Integer> queueEntryDao; // assume queueEntry has auto-generated id
	
	private static final String acceptableChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"; // for generating qCode
	private static final int lengthOfQCode = 6;
	private static SecureRandom rnd;

	
	public DBInterface() {
		setupDatabase();
		rnd = new SecureRandom();
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
			TableUtils.createTableIfNotExists(connectionSource, User.class);
			TableUtils.createTableIfNotExists(connectionSource, Queue.class);
			TableUtils.createTableIfNotExists(connectionSource, QueueEntry.class);
		}
		catch (Exception e) {
			System.out.println("There is a problem connecting to the database");
			e.printStackTrace(System.out);
		}
		
	}
	
	// WARNING: THIS DELETES ALL DATA STORED IN DATABASE
	public void clearTables() {
		try {
			TableUtils.clearTable(connectionSource, User.class);
			TableUtils.clearTable(connectionSource, Queue.class);
			TableUtils.clearTable(connectionSource, QueueEntry.class);
		} catch (SQLException se) {
			System.out.println("Problem in DBInterface.clearTables().");
		}
	}
	
	public void dropTables() {
		try {
			TableUtils.dropTable(connectionSource, User.class, true);
			TableUtils.dropTable(connectionSource, Queue.class, true);
			TableUtils.dropTable(connectionSource, QueueEntry.class, true);
		} catch (SQLException se) {
			System.out.println("Problem in DBInterface.dropTables().");
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
		try {
			queueEntryDao.create(qe);
			return true;
		} catch (SQLException se) {
			// There was some kind of problem saving the user to the db
			se.printStackTrace();
			System.out.println("problem in addQueueEntryToDB");
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
	
	public List<QueueEntry> getAllQueueEntries() {
		try {
			// Build the query
			QueryBuilder<QueueEntry, Integer> queryBuilder = queueEntryDao.queryBuilder();
		
			// Run the query and return the result if appropriate
			return queryBuilder.query();

		}
		catch (SQLException se) {
			System.out.println("Problem reading from database");
			return (List<QueueEntry>) null; 
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
		
		try {
			// Build query for queue
			QueryBuilder<Queue, String> queueQB = queueDao.queryBuilder();
			queueQB.where().eq(Queue.QCODE_FIELD_NAME, qCode);
			
			// Build query for user
			QueryBuilder<User, String> userQB = userDao.queryBuilder();
			userQB.where().eq(User.EMAIL_FIELD_NAME, email);
			
			
			// Join the query, run it, and return the result if appropriate
			QueryBuilder<QueueEntry, Integer> qb = queueEntryDao.queryBuilder();
			List<QueueEntry> results = qb.join(queueQB).join(userQB).query();
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
	
	
	// FIX
	public QueueEntry getQueueEntryFromDBByPosition(String qCode, int position) {
		// Get the queue object
		Queue allegedQueue = getQueueFromDB(qCode);
		if (allegedQueue == null) {
			return (QueueEntry) null;
		}
		
		// Build the query
		try {
			// Build query for queue
			QueryBuilder<Queue, String> queueQB = queueDao.queryBuilder();
			queueQB.where().eq(Queue.QCODE_FIELD_NAME, qCode);
			
			// Build query for queueEntry
			QueryBuilder<QueueEntry, Integer> queryBuilder = queueEntryDao.queryBuilder();
			queryBuilder.where().eq(QueueEntry.POSITION_FIELD_NAME, position);
			
			// Run the query and return the result if appropriate
			List<QueueEntry> results = queryBuilder.join(queueQB).query();
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
	public void updateQueuePositions (String qCode, int pos) {
		// Decrements every queue Entry starting at pos by 1
		List<QueueEntry> queueEntries = getEntriesInQueue(qCode);
		for (QueueEntry qe : queueEntries) {
			if (qe.getPosition() >= pos) {
				qe.setPosition(qe.getPosition() - 1);
				try {
					queueEntryDao.update(qe);
				} catch (SQLException se) {
					System.out.println("Problem updating Queue in DB.");
				}
			}
		}
	}
	
	
	public void updateAverageWaitTime(String qCode, int newAvgWaitTime) {
		try {
			// Build the update query
			UpdateBuilder<Queue, String> updateBuilder = queueDao.updateBuilder();
			updateBuilder.where().eq(Queue.QCODE_FIELD_NAME, qCode);
			
			// Increment the field
			updateBuilder.updateColumnValue(Queue.AVGWAITTIME_FIELD_NAME, newAvgWaitTime);

			// Run the query
			updateBuilder.update();
		} catch (SQLException se) {
			System.out.println("Problem updating avg. wait time in DB.");
		}
	}
	
	
	public void removeUserFromQueue (String email, String qCode) {
		QueueEntry qe = getQueueEntryFromDB(email, qCode);
		
		// delete the queueEntry from DB
		deleteQueueEntryFromDB(qCode, email);
		
		//update the positions of the people in the queue
		updateQueuePositions(qCode, qe.getPosition() + 1);
		
	}
	
	
	public QueueEntry advanceQueue (String qCode) {		
		// remove the "top" user from the queue
		QueueEntry topOfQueue = getQueueEntryFromDBByPosition(qCode, 1);
		deleteQueueEntryFromDB(topOfQueue.getQueue().getqCode(), topOfQueue.getUser().getEmail());
		
		//Get the queue
		Queue thisQueue = getQueueFromDB(qCode);
		if (thisQueue == null) {
			return (QueueEntry) null;
		}
		
		// variables for niceness
		int lastMean = thisQueue.getAvgWaitTime();
		int numUsersProcessed = thisQueue.getNumUsersProcessed();	
		
		// Update the average waiting time
		Date now = new Date();
		int timeDelta = (int) ((now.getTime() - topOfQueue.getTimeOfEntry().getTime())/1000.0); // num of milliseconds in queue
		int newMean = lastMean + ((timeDelta - lastMean)/(numUsersProcessed + 1));
		updateAverageWaitTime(qCode, newMean);
		
		// increment num users processed
		incrementNumUsersProcessed(qCode);
		
		// return the QueueEntry that was popped from Queue
		return topOfQueue;
	}
	
	
	/////////////////////////////
	/////// Queue Queries ///////
	/////////////////////////////
	// FIX
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
	
	// FIX
	public int getNumEntriesInQueue(String qCode) { 
		// Returns the number of QueueEntries in a Queue; if queue DNE or error, returns -1
		
		// See if that queue exists and get the object
		Queue q = getQueueFromDB(qCode);
		if (q == null) {
			return -1;
		}
		
		try {
			// Build query for the Queue
			QueryBuilder<Queue, String> queueQB = queueDao.queryBuilder();
			queueQB.where().eq(Queue.QCODE_FIELD_NAME, qCode);
			
			// Join with query for QueueEntry
			QueryBuilder<QueueEntry, Integer> queueEntryQB = queueEntryDao.queryBuilder();
			
			return queueEntryQB.join(queueQB).query().size();
		} catch (SQLException se) {
			return -1;
		}
	}
	
	// FIX
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
	
	// FIX
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
	
	
	public HashSet<String> getQCodesInUse() {
		// Get all Queues from DB
		QueryBuilder<Queue, String> queueQB = null;
		List<Queue> activeQueues = null;
		try {
			// Query for all queues
			queueQB = queueDao.queryBuilder();
			activeQueues = queueQB.query();
		} catch (SQLException se) {
			return new HashSet<String>();
		}
		
		// Get their QCodes and put in vector
		HashSet<String> returnSet = new HashSet<String>();
		for (Queue q : activeQueues) {
			returnSet.add(q.getqCode());
		}
		return returnSet;
	}
	
	
	public String generateValidQCode() {
		// Get a list of current Q codes in DB
		HashSet<String> usedQCodes = getQCodesInUse();
		String newQCode;
		while (true) {
			newQCode = generateRandomStringHelper(lengthOfQCode);
			if (!usedQCodes.contains(newQCode)) {
				return newQCode;
			}
		}
	}
	
	
	private String generateRandomStringHelper(int len) {
		StringBuilder sb = new StringBuilder(len);
		for( int i = 0; i < len; i++ ) {
			sb.append( acceptableChars.charAt( rnd.nextInt(acceptableChars.length())));
		}
		return sb.toString();
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
	
	// FIX
	public boolean deleteQueueEntryFromDB(String qCode, String email) {
		try {
			// Build query for the queue
			QueryBuilder<Queue, String> queueQB = queueDao.queryBuilder();
			queueQB.where().eq(Queue.QCODE_FIELD_NAME, qCode);
			
			// build query for the user
			QueryBuilder<User, String> userQB = userDao.queryBuilder();
			userQB.where().eq(User.EMAIL_FIELD_NAME, email);
			
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
