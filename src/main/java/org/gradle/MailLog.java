package org.gradle;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailLog {

    private final static String USER_NAME = "picloudlogger";
    private final static String PASSWORD = "rifsberjabakari87"; //TODO encrypt
    private final static String[] RECIPIENTS = {"jfjclarke@gmail.com", "ossur.ingi@gmail.com"};
    
    public MailLog( String... log ) {
    	String subject = "PiCloud log " + new Date(); 
    	sendFromGMail(RECIPIENTS, subject, log);
    }
    
    private void sendFromGMail(String[] to, String subject, String... log) {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", USER_NAME);
        props.put("mail.smtp.password", PASSWORD);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props,
      		  new javax.mail.Authenticator() {
      			protected PasswordAuthentication getPasswordAuthentication() {
      				return new PasswordAuthentication(USER_NAME, PASSWORD);
      			}
      		  });
        
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(USER_NAME + "@gmail.com"));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for( int i = 0; i < to.length; i++ ) {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for( int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject(subject);
            
            StringBuilder builder = new StringBuilder();
            for(int i = 0 ; i < log.length; i++){
            	builder.append(log[i]);
            	builder.append("\n");
            }
            
            message.setText(builder.toString());
            Transport transport = session.getTransport("smtp");
            transport.connect(host, USER_NAME, PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            Transport.send(message);
        }
        catch (AddressException ae) {
            ae.printStackTrace();
        } catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) {
    	String[] arr = {"testmessage", "line2", "line3"};
    	@SuppressWarnings("unused")
		MailLog log = new MailLog(arr);
    }
}