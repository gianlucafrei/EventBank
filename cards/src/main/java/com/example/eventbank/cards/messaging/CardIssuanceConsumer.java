package com.example.eventbank.cards.messaging;


import com.example.eventbank.cards.dto.CardIssueRequest;
import com.example.eventbank.cards.dto.Message;
import com.example.eventbank.cards.service.CardIssuanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardIssuanceConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CardIssuanceService cardIssuanceService;


    @KafkaListener(topics = "issue-card-message-topic")
    public void startCardIssuing(Message<?> message) {

        try {
            String json = objectMapper.writeValueAsString(message.getData());
            log.info("Start Card Issuing: {}", json);
            CardIssueRequest cardIssueRequest = objectMapper.readValue(json, CardIssueRequest.class);
            cardIssuanceService.issueCard(cardIssueRequest, message.getCorrelationId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
