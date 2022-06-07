package com.example.eventbank.cards.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateEvent {

    String currencyFrom;
    String currencyTo;
    Double rate;
}
