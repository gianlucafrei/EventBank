package com.example.eventbank.cards.service;


import com.example.eventbank.cards.domain.Card;
import com.example.eventbank.cards.domain.CardRoster;
import com.example.eventbank.cards.dto.CardIssuedEvent;
import com.example.eventbank.cards.dto.IssueCardRequest;
import com.example.eventbank.cards.messaging.CardIssuanceProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardIssuanceService {

    private final CardIssuanceProducer cardIssuanceProducer;

    public void issueCard(IssueCardRequest issueCardRequest, String correlationId) {
        Card card = CardRoster.getCardRoster().createCardForAccount(
                issueCardRequest.getLastName(),
                issueCardRequest.getFirstName(),
                issueCardRequest.getAccountId()
        );
        notifyCardIssued(issueCardRequest.getAccountId(), card.getCardId(), correlationId);
    }

    @Async
    void notifyCardIssued(String accountId, String cardId, String correlationId) {
        CardIssuedEvent cardIssuedEvent = new CardIssuedEvent(accountId, cardId);
        cardIssuanceProducer.notifyCardIssued(cardIssuedEvent, correlationId);
    }
}
