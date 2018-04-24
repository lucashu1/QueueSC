package database;

import queues.Queue;
import queues.QueueEntry;
import users.User;

public class PrepDemo {
	public static void main(String [] args) {
		DBInterface dbInterface = new DBInterface();
		
		System.out.println("Prepping DB for demo...");
		// Clear DB
		dbInterface.clearTables();
		
		// ----- Create users and add to DB ----- //
		// Email: president@usc.edu, Password: fucla
		User nikias = new User("Max", "Nikias", "president@usc.edu");
		nikias.setPasswordHash(nikias.hashPassword("fucla"));
		dbInterface.addUsertoDB(nikias);
		
		// Email: lucashu@usc.edu, Password: queuescrules
		User lucas = new User("Lucas", "Hu", "lucashu@usc.edu"); 
		lucas.setPasswordHash(lucas.hashPassword("queuescrules"));
		dbInterface.addUsertoDB(lucas);
		
		// Email: nthakur@usc.edu, Password: password123
		User natasha = new User("Natasha", "Thakur", "nthakur@usc.edu"); 
		natasha.setPasswordHash(natasha.hashPassword("password123"));
		dbInterface.addUsertoDB(natasha);
		
		// Email: cote@usc.edu, Password: pequalsnp
		User cote = new User("Aaron", "Cote", "cote@usc.edu");
		cote.setPasswordHash(cote.hashPassword("pequalsnp"));
		dbInterface.addUsertoDB(cote);
		
		// ----- Create queues and add to DB ----- //
		Queue cpQueue = new Queue("CP1234", "CS270 CP Help", "Week of 4/23 270 questions", 
				cote, false, false, false, false, 15); // Managed by Cote
		dbInterface.addQueueToDB(cpQueue);
		Queue crepesQueue = new Queue("CREPE1", "Village Crepes", "Village crepe line",
				nikias, false, false, false, false, 10); // Managed by Nikias
		dbInterface.addQueueToDB(crepesQueue);
		
		// ----- Add queue entries to DB ----- //
		QueueEntry cpQE1 = new QueueEntry(nikias, cpQueue, "", 0);
		cpQE1.setPosition(1);
		dbInterface.addQueueEntryToDB(cpQE1);
		QueueEntry cpQE2 = new QueueEntry(lucas, cpQueue, "", 0);
		cpQE2.setPosition(2);
		dbInterface.addQueueEntryToDB(cpQE2);
	}
}
