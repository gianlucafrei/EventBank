package com.example.eventbank.cards.messaging;


import com.example.eventbank.cards.dto.IssueCardRequest;
import com.example.eventbank.cards.dto.Message;
import com.example.eventbank.cards.service.CardIssuanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardIssuanceConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CardIssuanceService cardIssuanceService;


    @Bean
    public Consumer<Message<?>> issueCard() {
        return this::startCardIssuing;
    }


    public void startCardIssuing(Message<?> message) {

        try {
            String json = objectMapper.writeValueAsString(message.getData());
            log.info("Start Card Issuing: {}", json);
            IssueCardRequest issueCardRequest = objectMapper.readValue(json, IssueCardRequest.class);
            cardIssuanceService.issueCard(issueCardRequest, message.getCorrelationId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
