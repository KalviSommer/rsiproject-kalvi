package com.example.rsiadvisor.Methods;

import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Email {
    @PostMapping("sendEmail") //ei tohiks olla getmapping, ainult testimiseks
    public static void sendEmail(String email) throws MessagingException {
        send(email, "Subject", "Test email");
    }

    public static void send(String toEmail, String subject, String body) throws MessagingException {

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("rsiadvisor.info@gmail.com", "rsiadvisor");
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("rsiadvisor.info@gmail.com"));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toEmail)
        );
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
}
