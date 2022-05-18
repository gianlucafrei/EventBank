package com.example.eventbank.currencies.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRateEvent {

    String currencyFrom;
    String currencyTo;
    Double rate;
}
