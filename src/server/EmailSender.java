package server;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender extends Thread {

	private static final String USERNAME = "queueSC201@gmail.com";
	private static final String PASSWORD = "welcometoqueuesc";
	private static Properties properties = System.getProperties();

	private String recipientEmail;
	private String queueName;
	private Session session;

	public EmailSender(String email, String queueName) {
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		this.recipientEmail = email;
		this.queueName = queueName;
		
		// Get the default Session object.
		session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME, PASSWORD);
			}
		});
	}

	public void run() {

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(USERNAME));

			// Set To: header field of the header.
			message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));

			// Set Subject: header field
			message.setSubject("QueueSC Update: Your Turn for " + queueName);

			// Now set the actual message
			message.setText("Message from queue '" + queueName + "': It's your turn!");

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
	
	public static void main(String [] args) {
		// Testing email functionality...
		EmailSender testEmail = new EmailSender("lucashu1998@gmail.com", "Test Queue");
		testEmail.start();
	}
}