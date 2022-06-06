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
import java.util.LinkedHashMap;
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
    public List<LinkedHashMap<String, ?>> exchangeRates() {

        List<LinkedHashMap<String, ?>> rates = new ArrayList<>();

        KeyValueIterator<String, Message<LinkedHashMap<String, ?>>> range = getStore().all();
        while (range.hasNext()) {
            KeyValue<String, Message<LinkedHashMap<String, ?>>> next = range.next();
            log.info("KEY: {}, ER: {}", next.key, next.value);
            rates.add(next.value.getData());
        }
        range.close();

        return rates;
    }


    @GetMapping("/rates/{currencyFrom}")
    public LinkedHashMap<String, ?> exchangeRateFrom(@PathVariable String currencyFrom) {

        LinkedHashMap<String, ?> rate = new LinkedHashMap<>();

        KeyValueIterator<String, Message<LinkedHashMap<String, ?>>> range = getStore().all();
        while (range.hasNext()) {
            KeyValue<String, Message<LinkedHashMap<String, ?>>> next = range.next();
            if (next.value.getData().containsValue(currencyFrom)) {
                log.info("KEY: {}, ER: {}", next.key, next.value);
                rate = next.value.getData();
                break;
            }
        }
        range.close();

        return rate;
    }

    private ReadOnlyKeyValueStore<String, Message<LinkedHashMap<String, ?>>> getStore() {
        return currencyStream.getStreams().store(
                StoreQueryParameters.fromNameAndType(
                        // state store name
                        "exchange-rate",
                        // state store type
                        QueryableStoreTypes.keyValueStore()));
    }
}
