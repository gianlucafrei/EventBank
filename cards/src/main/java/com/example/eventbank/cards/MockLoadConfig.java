package com.example.eventbank.cards;

import com.example.eventbank.cards.dto.PaymentRequest;
import com.example.eventbank.cards.service.CardPaymentService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This component creates some mock load on the system. Run the application with the
 * "mockLoad" profile to enable it.
 */
@Profile("mockLoad")
@Component
public class MockLoadConfig {

    @Autowired
    private CardPaymentService cardPaymentService;

    @SneakyThrows
    @Scheduled(fixedRate = 1000)
    public void mockLoad(){

        PaymentRequest request = new PaymentRequest("account1", "account2", 0, "");
        cardPaymentService.processPayment(request);

    }
}
