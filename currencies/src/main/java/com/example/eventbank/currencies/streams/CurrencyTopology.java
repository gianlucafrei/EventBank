package com.example.eventbank.currencies.streams;


import com.example.eventbank.currencies.dto.ExchangeRateEvent;
import com.example.eventbank.currencies.dto.Message;
import com.example.eventbank.currencies.dto.PaymentRequestEvent;
import com.example.eventbank.currencies.dto.serdes.ExchangeRateEventSerde;
import com.example.eventbank.currencies.dto.serdes.PaymentRequestEventSerde;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;


@Log4j2
public class CurrencyTopology {

    private static final String PAYMENT_INPUT_TOPIC = "payment-request-events";
    private static final String EXCHANGE_RATE_INPUT_TOPIC = "exchange-rate-events";

    public static Topology build() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Message<PaymentRequestEvent>> paymentStream = builder.stream(PAYMENT_INPUT_TOPIC,
                Consumed.with(Serdes.String(), new PaymentRequestEventSerde()));

        KStream<String, Message<ExchangeRateEvent>> erStream = builder.stream(EXCHANGE_RATE_INPUT_TOPIC,
                Consumed.with(Serdes.String(), new ExchangeRateEventSerde()));

        //KTable<String, Message<ExchangeRateEvent>> exchangeRateTable = builder
        //        .table(EXCHANGE_RATE_INPUT_TOPIC, Materialized.as("exchange-rate"));

        paymentStream.peek((k, v) -> log.info("Streaming -> key: {}, value: {}", k, v.getData()));

        erStream.peek((k, v) -> log.info("Streaming -> key: {}, value: {}", k, v.getData()));

        KTable<String, Message<ExchangeRateEvent>> erTable = erStream
                .selectKey((k, v) -> v.getData().getCurrencyFrom())
                .toTable(Materialized.as("exchange-rate"));




        return builder.build();
    }
}
