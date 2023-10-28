package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String message, String subject, String to) {
		
		boolean flag = false;
		String from = "pankajpathakipem@gmail.com";
		String host = "smtp.gmail.com";
		
		//get the system properties
		Properties properties = System.getProperties();
		System.out.println("Properties : "+properties);
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		//step 1 - to get the session object
		Session session = Session.getInstance(properties, new Authenticator() {
			//Passcode - kxucpucykycbcipx
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("pankajpathakipem@gmail.com", "kxucpucykycbcipx");
			}
		
		});	
		session.setDebug(true);
		//Step 2 : compose the message
		try {
			MimeMessage m = new MimeMessage(session);
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			m.setSubject(subject);
			
			m.setText(message);
		//step 3- send the message using Transport class
			Transport.send(m);
			System.out.println("message sent successfully");
			flag = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}
