package com.example.eventbank.currencies.web;

import com.example.eventbank.currencies.dto.ExchangeRateEvent;
import com.example.eventbank.currencies.dto.Message;
import com.example.eventbank.currencies.streams.CurrencyStream;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@Log4j2
@RestController
@AllArgsConstructor
public class CurrencyController {

    private CurrencyStream currencyStream;

    @GetMapping("/")
    public String helloMessage() {
        return "Hello from Risks Service";
    }


    @GetMapping("/rates")
    public List<ExchangeRateEvent> exchangeRates() {

        List<ExchangeRateEvent> rates = new ArrayList<>();

        KeyValueIterator<String, Message<ExchangeRateEvent>> range = getStore().all();
        while (range.hasNext()) {
            KeyValue<String, Message<ExchangeRateEvent>> next = range.next();
            log.info("GET - Rate for {}, {}", next.key, next.value.getData());
            rates.add(next.value.getData());
        }
        range.close();

        return rates;
    }


    @GetMapping("/rates/{currencyFrom}")
    public ExchangeRateEvent exchangeRateFrom(@PathVariable String currencyFrom) {

        ExchangeRateEvent rate = new ExchangeRateEvent();

        KeyValueIterator<String, Message<ExchangeRateEvent>> range = getStore().all();
        while (range.hasNext()) {
            KeyValue<String, Message<ExchangeRateEvent>> next = range.next();
            if (next.value.getData().getCurrencyFrom().equalsIgnoreCase(currencyFrom)) {
                log.info("GET - Rate for {}, {}", next.key, next.value.getData());
                rate = next.value.getData();
                break;
            }
        }
        range.close();

        return rate;
    }

    private ReadOnlyKeyValueStore<String, Message<ExchangeRateEvent>> getStore() {
        return currencyStream.getStreams().store(
                StoreQueryParameters.fromNameAndType(
                        // state store name
                        "exchange-rate",
                        // state store type
                        QueryableStoreTypes.keyValueStore()));
    }
}
