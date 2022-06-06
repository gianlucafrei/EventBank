package com.example.eventbank.currencies.dto;


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
