package com.fbnm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;

public class Mail {
	private static String email_server = Config.getField("email_server");
	private static String email_port = Config.getField("email_port");
	private static String email_user = Config.getField("email_user");
	private static String email_pass = Config.getField("email_pass");
	
	//private static String email_server = "smtp.gmail.com";
	//private static String email_port = "587";
	//private static String email_user = "hens@halogen-group.com";
	//private static String email_pass = "jE5p@e47gK1wr9m";
	
	public static boolean send(String recipient, String subject, String body) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", email_server);
		props.put("mail.smtp.port", email_port);
		props.put("mail.smtp.starttls.enable", "true");
		
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email_user, email_pass);
			}
		});
		
		MimeMessage message = new MimeMessage(session);
		MimeMultipart multipart = new MimeMultipart();
		
		BodyPart messageBodyPart = new MimeBodyPart();
		try {
			messageBodyPart.setContent(body, "text/html");
			multipart.addBodyPart(messageBodyPart);
			
			String file1 = "C:\\FBNMAlert\\alert_logo.png";
			BodyPart attachBodyPart1 = new MimeBodyPart();
			DataSource source1 = new FileDataSource(file1);
			attachBodyPart1.setDataHandler(new DataHandler(source1));
			//attachBodyPart1.setFileName(Paths.get(file1).getFileName().toString());
			attachBodyPart1.setDisposition(MimeBodyPart.INLINE);
			attachBodyPart1.setHeader("Content-ID", "<logo>");
			multipart.addBodyPart(attachBodyPart1);
			
			message.setContent(multipart);
			message.setFrom(new InternetAddress(email_user, "FirstTrust Mortgage Bank"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
			message.setSubject(subject);
			
			Transport.send(message);
			
			return true;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
	}
	
	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		
		return result;
	}
	
	public static boolean sendEmailAlert(String transaction_type, String name, String account_no, String amount, String narrative, String time, String branch, String available_balance, String recipient) {
		String body = "C:\\FBNMAlert\\alert_template.html";
		
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(new FileInputStream(new File(body)), writer);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		String message = writer.toString();
		if (transaction_type == null) {
			message = message.replace("{transaction_type}", "");
		} else {
			message = message.replace("{transaction_type}", transaction_type.toLowerCase());
		}
		if (name == null) {
			message = message.replace("{name}", "");
		} else {
			message = message.replace("{name}", name.toUpperCase());
		}
		if (account_no == null) {
			message = message.replace("{account_no}", "");
		} else {
			message = message.replace("{account_no}", account_no);
		}
		if (amount == null) {
			message = message.replace("{amount}", "");
		} else {
			message = message.replace("{amount}", amount);
		}
		if (narrative == null) {
			message = message.replace("{narrative}", "");
		} else {
			message = message.replace("{narrative}", narrative);
		}
		if (time == null) {
			message = message.replace("{time}", "");
		} else {
			message = message.replace("{time}", time);
		}
		if (branch == null) {
			message = message.replace("{branch}", "");
		} else {
			message = message.replace("{branch}", branch);
		}
		if (available_balance == null) {
			message = message.replace("{available_balance}", "");
		} else {
			message = message.replace("{available_balance}", available_balance);
		}
		
		String subject = "FirstTrust - "+transaction_type+" Alert";
		
		//send("yemi.ogundare@fbnmortgages.com", subject, message);
		
		if (send(recipient, subject, message))
			return true;
		else
			return false;
	}
}
