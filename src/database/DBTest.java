package database;

import java.util.List;
import java.util.Vector;

import queues.Queue;
import queues.QueueEntry;
import users.User;

public class DBTest {
	
	public static void main(String[] args) {
		DBInterface db = new DBInterface();
		// clear the tables first
		// try adding user to database
		User newUser = new User("leonardo", "cicconi", "cicconi@usc.edu");
		db.addUsertoDB(newUser);
		// Get that user from database and print the details
		User userFromDB = db.getUserFromDB("cicconi@usc.edu");
		System.out.println("First name: " + userFromDB.getFirstName());
		System.out.println("Last name: " + userFromDB.getLastName());
		System.out.println("Email: " + userFromDB.getEmail());
		// create a queue and add the queue to the DB
		String newQCode = db.generateValidQCode();
		System.out.println("Generated QCODE: " + newQCode);
		Queue newQueue = new Queue(newQCode, "myQueue", "this is a test queue", newUser, false, false, false, false, 10);
		db.addQueueToDB(newQueue);
		// add another new user to the db and then the queue
		User newUser2 = new User("lucas", "hu", "lucashu@usc.edu");
		db.addUsertoDB(newUser2);
		User userFromDB2 = db.getUserFromDB("lucashu@usc.edu");
		Queue queueFromDB = db.getQueueFromDB(newQCode);
		if (queueFromDB == null) {
			System.out.println("The queue was not found in db...");
		}
		QueueEntry newQE = new QueueEntry(userFromDB2, queueFromDB, "", 0);
		newQE.setPosition(1);
		System.out.println("b4 QE email: " + newQE.getUser().getEmail());
		System.out.println("b4 QE qCode: " + newQE.getQueue().getqCode());

		db.addQueueEntryToDB(newQE);
		List<QueueEntry> results = db.getAllQueueEntries();
		for (QueueEntry qe : results) {
			System.out.println("email: " + qe.getUser().getEmail());
		}
		// Get that queue entry from the DB and print the details
		QueueEntry qeFromDB = db.getQueueEntryFromDB(newQCode, "lucashu@usc.edu");
		System.out.println("QE email: " + qeFromDB.getUser().getEmail());
		// TEST getQueueEntriesForUser
		Queue newQueue2 = new Queue(newQCode, "myQueue2", "this is a test queue", newUser, false, false, false, false, 10);
		db.addQueueToDB(newQueue2);
		QueueEntry newQE2 = new QueueEntry(newUser, queueFromDB, "", 0);
		newQE2.setPosition(2);
		db.addQueueEntryToDB(newQE2);
		List<QueueEntry> qesForUser2 = db.getQueueEntriesForUser("lucashu@usc.edu");
		for (QueueEntry qe : qesForUser2) {
			System.out.println("QE: ");
			System.out.println(qe.getUser().getEmail());
			System.out.println(qe.getQueue().getqCode());
			System.out.println(qe.getPosition());
		}
//		System.out.println("TEST");
//		List<QueueEntry> f = db.getEntriesInQueue(newQCode);
//		for (QueueEntry qe : qesForUser2) {
//			System.out.println("QE: ");
//			System.out.println(qe.getUser().getEmail());
//			System.out.println(qe.getQueue().getqCode());
//			System.out.println(qe.getPosition());
//		}
		System.out.println("num of people in queue: " + db.getEntriesInQueue(newQCode).size());
		db.advanceQueue(newQCode);
		System.out.println("num of people in queue: " + db.getEntriesInQueue(newQCode).size());
		db.advanceQueue(newQCode);
	}
	
}

// SELECT "queueEntries".* FROM "queueEntries" INNER JOIN "queues" ON "queueEntries"."queue_id" = "queues"."qCode" WHERE "queues"."qCode" = '8LF7PG' 