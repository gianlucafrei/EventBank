package com.example.eventbank.exchangerate.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRateEvent {

    String currencyFrom;
    String currencyTo;
    Double rate;
}
