package com.example.eventbank.cards.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardRoster {

    private static final CardRoster cardRoster = new CardRoster();

    private final Map<String, List<Card>> accountCards;

    private CardRoster() {
        this.accountCards = new HashMap<>();
    }

    public static CardRoster getCardRoster() {
        return cardRoster;
    }

    public Card createCardForAccount(String accountId, String firstName, String lastName) {
        List<Card> cards = accountCards.containsKey(accountId) ? accountCards.get(accountId) : new ArrayList<>();
        Card card = new Card(firstName, lastName);
        cards.add(card);
        accountCards.put(accountId, cards);

        return card;
    }
}
