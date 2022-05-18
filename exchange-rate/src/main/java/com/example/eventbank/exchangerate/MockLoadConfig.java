package com.example.eventbank.exchangerate;

import com.example.eventbank.exchangerate.dto.ExchangeRateEvent;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * This component creates some mock load on the system. Run the application with the
 * "mockLoad" profile to enable it.
 */
@Profile("mockLoad")
@Component
public class MockLoadConfig {

    private static Random random = new Random();

    @Autowired
    private ExchangeRateProducer exchangeRateProducer;

    @SneakyThrows
    @Scheduled(fixedRate = 3000)
    public void mockLoad(){

        // EUR-CHF
        ExchangeRateEvent eur = new ExchangeRateEvent(
                "EUR",
                "CHF",
                roundDecimals(getRandomNumber(1.0, 1.2), 2)
        );
        exchangeRateProducer.sendExchangeRate(eur);

        // USD-CHF
        ExchangeRateEvent usd = new ExchangeRateEvent(
                "USD",
                "CHF",
                roundDecimals(getRandomNumber(0.9, 1.1), 2)
        );
        exchangeRateProducer.sendExchangeRate(usd);

        // KRW-CHF
        ExchangeRateEvent krw = new ExchangeRateEvent(
                "KRW",
                "CHF",
                roundDecimals(getRandomNumber(0.0006, 0.0008), 6)
        );
        exchangeRateProducer.sendExchangeRate(krw);
    }

    public static double getRandomNumber(double min, double max) {
        return (random.nextDouble() * (max - min)) + min;
    }

    private static double roundDecimals(double number, int decimals) {
        return Math.round(number * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }
}
