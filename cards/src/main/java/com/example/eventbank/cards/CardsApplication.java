package com.example.eventbank.cards;

import com.example.eventbank.cards.dto.PaymentRequest;
import com.example.eventbank.cards.service.CardPaymentService;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class CardsApplication {


    public static void main(String[] args) {
        SpringApplication.run(CardsApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("EventBank Cards Service"));
    }
}
