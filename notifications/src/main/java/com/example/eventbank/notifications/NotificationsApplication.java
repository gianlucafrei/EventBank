package com.example.eventbank.notifications;

import com.example.eventbank.notifications.dto.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.function.Consumer;

@SpringBootApplication
@EnableScheduling
public class NotificationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationsApplication.class, args);
    }


    @Bean
    public Consumer<Message<?>> globalConsumer() {
        return message -> System.out.println("received " + message);
    }
}
