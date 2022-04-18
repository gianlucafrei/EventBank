package com.example.eventbank.customerregistration.adapter.out.messaging;

import com.example.eventbank.customerregistration.dto.Message;
import com.example.eventbank.customerregistration.service.interf.IssueCardEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IssueCardProducer {

    private final StreamBridge streamBridge;


    public void issueCard(IssueCardEvent issueCardEvent, String correlationId) {
        Message<IssueCardEvent> message = new Message<>("issueCardEvent", issueCardEvent)
                .setCorrelationId(correlationId);

        streamBridge.send("issueCard-out-0", message);
        log.info("Sent issue card event to issueCard-out-0: {}", issueCardEvent);
    }
}
