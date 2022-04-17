package com.example.eventbank.cards.messaging;

import com.example.eventbank.cards.dto.CardIssuedEvent;
import com.example.eventbank.cards.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardIssuanceProducer {

    private final StreamBridge streamBridge;


    public void notifyCardIssued(CardIssuedEvent cardIssuedEvent, String correlationId) {
        Message<CardIssuedEvent> message = new Message<>("CardIssuedEvent", cardIssuedEvent)
                .setCorrelationId(correlationId);

        streamBridge.send("issueCard-out-0", message);
        log.info("Sent issue card event to issueCard-out-0: {}", cardIssuedEvent);
    }
}
