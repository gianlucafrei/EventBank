package com.example.eventbank.customerregistration.adapter.out.messaging;

import com.example.eventbank.customerregistration.dto.Message;
import com.example.eventbank.customerregistration.service.interf.IssueCardEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IssueCardProducer {

    private final KafkaTemplate<String, Message<?>> kafkaTemplate;

    public void issueCard(Message<IssueCardEvent> message) {
        log.info("Send issue card event: {}", message.getData().toString());
        kafkaTemplate.send("issue-card-message-topic", message);
    }
}
