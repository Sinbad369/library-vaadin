package com.rubinbaldoro.library.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toAddress, String token) {
        String subject = "Verify your Rubin's Library Account";
        // This assumes your app is running on localhost:8080. We will create this endpoint next!
        String confirmationUrl = "http://localhost:8080/verify?token=" + token;
        String message = "Welcome to Rubin's Library! Please click the link below to verify your registration:\n\n" + confirmationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toAddress);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom("sukhrobkholdorov999@gmail.com"); // Match the email in your properties file

        mailSender.send(email);
    }
}