package com.example.server.Email;

import java.util.Properties;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.server.Announce.Announce;
import com.example.server.Security.EndPointsConfig;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public ResponseEntity<?> sendMessage(String senderEmail, String senderPassword, String to, String subject, String text, String filePath, String imagePath) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (filePath != null) {
                MimeBodyPart fileBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filePath);
                fileBodyPart.setDataHandler(new DataHandler(source));
                fileBodyPart.setFileName(filePath);
                multipart.addBodyPart(fileBodyPart);
            }

            if (imagePath != null) {
                MimeBodyPart imageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(imagePath);
                imageBodyPart.setDataHandler(new DataHandler(source));
                imageBodyPart.setHeader("Content-ID", "<image>");
                multipart.addBodyPart(imageBodyPart);
            }

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Message sent successfully!");

            return ResponseEntity.ok().body(new Announce("Message sent successfully!"));

        } catch (MessagingException e) {
            return ResponseEntity.badRequest().body(new Announce("Message sent failed!"));
        }
    }

    @Override
    public void responseMessage(String to, String subject, String text, String filePath,
            String imagePath) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EndPointsConfig.senderMailServer, EndPointsConfig.senderPasswordServer);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EndPointsConfig.senderMailServer));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (filePath != null) {
                MimeBodyPart fileBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filePath);
                fileBodyPart.setDataHandler(new DataHandler(source));
                fileBodyPart.setFileName(filePath);
                multipart.addBodyPart(fileBodyPart);
            }

            if (imagePath != null) {
                MimeBodyPart imageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(imagePath);
                imageBodyPart.setDataHandler(new DataHandler(source));
                imageBodyPart.setHeader("Content-ID", "<image>");
                multipart.addBodyPart(imageBodyPart);
            }

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Message sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getRequest() {
        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");
            properties.put("mail.imaps.host", "imap.gmail.com");
            properties.put("mail.imaps.port", "993");
            properties.put("mail.smtp.auth", "true");

            Authenticator authenticator = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EndPointsConfig.senderMailServer, EndPointsConfig.senderPasswordServer);
                }
            };

            Session session = Session.getInstance(properties, authenticator);
            Store store = session.getStore("imaps");

            store.connect("imap.gmail.com", EndPointsConfig.senderMailServer, EndPointsConfig.senderPasswordServer);

            Folder inbox = store.getFolder("INBOX");
            if (inbox == null) {
                System.out.println("No INBOX");
                return null;
            }

            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.getMessages();
            if (messages.length < 1) {
                System.out.println("No New Mail");
                return null;
            }

            Message lastMessage = messages[messages.length - 1];
            String text = lastMessage.getSubject();
            String res = text.substring(29);

            // Flag the message for deletion
            lastMessage.setFlag(Flags.Flag.DELETED, true);

            // Expunge to remove messages flagged for deletion
            inbox.expunge();

            inbox.close(true);

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String createSuccessResponse(String senderEmail, String message, String additionalInfo) {
        return "<html><body>"
        + "<p>Dear " + senderEmail + ",</p>"
        + "<p>" + message + "</p>"
        + (additionalInfo != null ? "<p>" + additionalInfo + "</p>" : "")
        + "<p>Thank you,<br/>Your Server Team</p>"
        + "</body></html>";
    }

    @Override
    public String createErrorResponse(String senderEmail, String errorMessage) {
        return "<html><body>"
        + "<p>Dear " + senderEmail + ",</p>"
        + "<p><b>" + errorMessage + "</b></p>"
        + "<p>An error occurred</p>"
        + "<p>Please contact support for assistance.</p>"
        + "<p>Thank you,<br/>Your Server Team</p>"
        + "</body></html>";
    }
}
