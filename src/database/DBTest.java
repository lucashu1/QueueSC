package database;

import queues.Queue;
import users.User;

public class DBTest {
	
	public static void main(String[] args) {
		DBInterface db = new DBInterface();
		// clear the tables first
		db.clearTables();
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
		Queue newQueue = new Queue(newQCode, "myQueue", "this is a test queue", newUser, false, false, false, false, 10);
		db.addQueueToDB(newQueue);
		// add another new user to the db and then the queue
		User newUser2 = new User("lucas", "hu", "lucashu@usc.edu");
		db.addUsertoDB(newUser2);
		
	}
}