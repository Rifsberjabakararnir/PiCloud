package org.gradle;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendLog {

    private static String USER_NAME = "picloudlogger";
    private static String PASSWORD = "rifsberjabakari87";
    private static String[] RECIPIENTS = {"jfjclarke@gmail.com", "ossur.ingi@gmail.com"};

    private String[] log;
    
    public SendLog( String[] log ) {
    	this.log = log;
    	String subject = "PiCloud log " + new Date(); 
    	sendFromGMail(USER_NAME, PASSWORD, RECIPIENTS, subject);
    }
    
    private void sendFromGMail(String from, String pass, String[] to, String subject) {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
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
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (AddressException ae) {
            ae.printStackTrace();
        }
        catch (MessagingException me) {
            me.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
    	String[] arr = {"testmessage", "line2", "line3"};
    	@SuppressWarnings("unused")
		SendLog log = new SendLog(arr);
    }
}