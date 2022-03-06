package com.example.eventbank.notifications;

import com.example.eventbank.notifications.dto.Message;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.function.Consumer;

@Log4j2
@SpringBootApplication
@EnableScheduling
public class NotificationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationsApplication.class, args);
    }

    @Bean
    public Consumer<Message> global() {
        return message -> consumeMessage(message);
    }

    public void consumeMessage(Message message){

        System.out.println("received " + message);

        if("paymentEvent".equals(message.getType())){

            String pushNotification = String.format("New card transaction");
            log.info("Push notification: " + pushNotification);
        }
    }
}
