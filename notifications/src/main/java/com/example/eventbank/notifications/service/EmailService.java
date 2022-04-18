package com.example.eventbank.notifications.service;


import com.example.eventbank.notifications.dto.EmailNotificationEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;


@Log4j2
@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(@Lazy JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(EmailNotificationEvent emailNotificationEvent) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@baeldung.com");
        message.setTo(emailNotificationEvent.getEmailAddress());
        message.setSubject(emailNotificationEvent.getTitle());
        message.setText(emailNotificationEvent.getMessage());
        emailSender.send(message);

        log.info("Sending Email to {}", emailNotificationEvent.getEmailAddress());
        log.info(" - Subject: {}", emailNotificationEvent.getTitle());
        log.info(" - Message: {}", emailNotificationEvent.getMessage());
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("edpo.eventbank@gmail.com");
        mailSender.setPassword("wxncehuqnezuwaoi");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}


