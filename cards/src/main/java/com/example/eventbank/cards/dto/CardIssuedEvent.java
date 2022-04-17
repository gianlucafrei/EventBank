package com.example.eventbank.cards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardIssuedEvent {

    private String accountId;
    private String cardId;

}
