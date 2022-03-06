package com.example.eventbank.cards.web;

import com.example.eventbank.cards.dto.Message;
import com.example.eventbank.cards.dto.PaymentEvent;
import com.example.eventbank.cards.dto.PaymentRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class PaymentController {

    @Autowired
    private StreamBridge streamBridge;

    @PostMapping("/payment")
    public String payment(PaymentRequest paymentRequest){

        // Create payment event
        PaymentEvent paymentEvent = new PaymentEvent(
                paymentRequest.getSourceAccount(),
                paymentRequest.getDestinationAccount(),
                paymentRequest.getAmount());

        // Send event
        Message message = new Message<PaymentEvent>("paymentEvent", paymentEvent);
        streamBridge.send("global-out-0", message);
        log.info("Sent paymentEvent to global-out-0 via StreamBridge");

        return "Success";
    }
}
