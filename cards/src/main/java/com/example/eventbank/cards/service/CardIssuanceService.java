package com.example.eventbank.cards.service;


import com.example.eventbank.cards.domain.Card;
import com.example.eventbank.cards.domain.CardRoster;
import com.example.eventbank.cards.dto.CardIssueRequest;
import com.example.eventbank.cards.dto.CardIssuedEvent;
import com.example.eventbank.cards.dto.Message;
import com.example.eventbank.cards.messaging.CardIssuanceProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardIssuanceService {

    private final CardIssuanceProducer cardIssuanceProducer;

    public void issueCard(CardIssueRequest cardIssueRequest, String correlationId) {
        Card card = CardRoster.getCardRoster().createCardForAccount(
                cardIssueRequest.getAccountId(),
                cardIssueRequest.getFirstName(),
                cardIssueRequest.getLastName()
        );
        notifyCardIssued(card.getCardId(), correlationId);
    }

    @Async
    void notifyCardIssued(String cardId, String correlationId) {
        CardIssuedEvent cardIssuedEvent = new CardIssuedEvent(cardId);
        Message<CardIssuedEvent> message = new Message<>("CardIssuedEvent", cardIssuedEvent)
                .setCorrelationId(correlationId);
        cardIssuanceProducer.notifyCardIssued(message);
    }
}
