package com.example.eventbank.exchangerate;


import com.example.eventbank.exchangerate.dto.ExchangeRateEvent;
import com.example.eventbank.exchangerate.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;


@Component
@Log4j2
@RequiredArgsConstructor
public class ExchangeRateProducer {

    private final StreamBridge streamBridge;

    public void sendExchangeRate(ExchangeRateEvent exchangeRateEvent) {
        Message message = new Message<>("exchangeRateEvent", exchangeRateEvent);
        streamBridge.send("exchange-rate-out-0", message);
        log.info("Sent new exchange rate to exchange-rate-out-0: {}", exchangeRateEvent);
    }
}
