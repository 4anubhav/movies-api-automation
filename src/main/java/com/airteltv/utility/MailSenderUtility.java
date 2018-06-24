package com.airteltv.utility;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSenderUtility {

	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_AUTH_USER = "test.reporting1@gmail.com";
	private static final String SMTP_AUTH_PWD = "testing@1234";

	private static final String emailMsgTxt = "Wynk Desktop Automation Test Reports :.";
	private static final String emailFromAddress = "testing.report1@gmail.com";

	// Add List of Email address to who email needs to be sent to
	private static final String[] emailList = { "disha.verma@wynk.in" };

	// "suneet.singh@wynk.in" ,"rakesh.sharma@wynk.in"
	public static void main(String args[]) throws Exception {
		MailSenderUtility smtpMailSender = new MailSenderUtility();

		System.out.println("Sucessfully Sent mail to All Users");
	}

	public void sendMail(String emailSubjectTxt) throws MessagingException, IOException {
		MailSenderUtility smtpMailSender = new MailSenderUtility();

		smtpMailSender.postMail(emailList, emailSubjectTxt, emailMsgTxt, emailFromAddress);
		
	}

	private BodyPart messageBodyPart;

	public void postMail(String recipients[], String subject, String message, String from)
			throws MessagingException, IOException {
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.ssl.enable", "false");

		props.put("mail.transport.protocol", "smtp");
		// props.put("mail.debug", "true");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.starttls.enable", "false");

		Authenticator auth = new SMTPAuthenticator();
		Session session = Session.getInstance(props, auth);

		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		// new code added
		Multipart multipart = new MimeMultipart();

		// Part two is attachment
		messageBodyPart = new MimeBodyPart();

		// DataSource source = new FileDataSource(file);
		// messageBodyPart.setContent(message, "application/zip");
		// messageBodyPart.setDataHandler(new DataHandler(source));
		if (subject.contains("WEB")) {
			messageBodyPart.setText("Please open this link ---  http://10.1.2.142:8185/REPORTS/REPORT"
					+ Utils.getTodaysDate() + "/index.html");
		} else {
			messageBodyPart.setText("Please open this link ---  http://10.1.2.142:8185/REPORTS/REPORT-BE-"
					+ Utils.getTodaysDate() + "/index.html");
		}
		messageBodyPart.setDescription(message);

		multipart.addBodyPart(messageBodyPart);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(multipart);
		Transport.send(msg);
	}

	/**
	 * SimpleAuthenticator is used to do simple authentication when the SMTP server
	 * requires it.
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {

		public PasswordAuthentication getPasswordAuthentication() {
			String username = SMTP_AUTH_USER;
			String password = SMTP_AUTH_PWD;
			return new PasswordAuthentication(username, password);
		}
	}

}
