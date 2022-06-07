package com.example.eventbank.currencies.streams;


import com.example.eventbank.currencies.dto.ExchangeRateEvent;
import com.example.eventbank.currencies.dto.Message;
import com.example.eventbank.currencies.dto.PaymentExtendedEvent;
import com.example.eventbank.currencies.dto.PaymentRequestEvent;
import com.example.eventbank.currencies.dto.serdes.ExchangeRateEventSerde;
import com.example.eventbank.currencies.dto.serdes.PaymentRequestEventSerde;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;


@Log4j2
public class CurrencyTopology {

    private static final String PAYMENT_INPUT_TOPIC = "payment-request-events";
    private static final String PAYMENT_EXTENDED_OUTPUT_TOPIC = "payment-extended-events";
    private static final String EXCHANGE_RATE_INPUT_TOPIC = "exchange-rate-events";

    public static Topology build() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Message<PaymentRequestEvent>> payment = builder.stream(PAYMENT_INPUT_TOPIC,
                Consumed.with(Serdes.String(), new PaymentRequestEventSerde()))
                .selectKey((k, v) -> v.getData().getCurrency());

        KStream<String, Message<ExchangeRateEvent>> exchangeRate = builder.stream(EXCHANGE_RATE_INPUT_TOPIC,
                Consumed.with(Serdes.String(), new ExchangeRateEventSerde()));

        payment.peek((k, v) -> log.info("PAYMENT -> {}", v.getData()));

        exchangeRate.peek((k, v) -> log.info("EXCHANGE RATE -> {}", v.getData()));

        /*
        /// Workaround to define keys and writing the exchange rate stream to a topic before reading it again as a globalKTable

        exchangeRate.selectKey((k, v) -> v.getData().getCurrencyFrom()).to("topic");

        KeyValueBytesStoreSupplier supplier = Stores.persistentKeyValueStore(
                "table"
        );

        GlobalKTable<String, Message<ExchangeRateEvent>> global = builder.globalTable("topic",
                Materialized.as(supplier));
        log.info("GLOBAL {}", global.queryableStoreName());

        /// toTable method has a problem with parameterized Message<> class -> will be a LinkedHashMap

        KTable<String, Message<ExchangeRateEvent>> erTable = exchangeRate
                .selectKey((k, v) -> v.getData().getCurrencyFrom())
                .toTable(Materialized.as("exchange-rate"));
         */

        KTable<String, Message<ExchangeRateEvent>> erTable = exchangeRate
                .groupBy((k, v) -> v.getData().getCurrencyFrom(),
                        Grouped.with(Serdes.String(), new ExchangeRateEventSerde()))
                .reduce((aggValue, newValue) -> newValue,
                        Materialized.as("exchange-rate"));


        KStream<String, Message<PaymentRequestEvent>> paymentForeign = payment.filterNot(
                (key, message) -> message.getData().getCurrency().equals("CHF")
        );

        KStream<String, Message<PaymentRequestEvent>> paymentChf = payment.filter(
                (key, message) -> message.getData().getCurrency().equals("CHF")
        );

        // Left join on payment stream with exchange table
        ValueJoiner<Message<PaymentRequestEvent>, Message<ExchangeRateEvent>, Message<PaymentExtendedEvent>>
                paymentJoiner = (p, er) -> new Message<>(
                       "paymentExtended" ,
                        PaymentExtendedEvent.builder()
                                .paymentId(p.getData().getPaymentId())
                                .sourceAccount(p.getData().getSourceAccount())
                                .destinationAccount(p.getData().getDestinationAccount())
                                .amount((int) (p.getData().getAmount() * er.getData().getRate()))
                                .originalAmount(p.getData().getAmount())
                                .currency(er.getData().getCurrencyTo())
                                .originalCurrency(p.getData().getCurrency())
                                .exchangeRate(er.getData().getRate())
                                .build()
        );

        KStream<String, Message<PaymentExtendedEvent>> paymentExtendedForeign = paymentForeign.join(erTable, paymentJoiner);

        KStream<String, Message<PaymentExtendedEvent>> paymentExtendedChf = paymentChf
                .mapValues(v -> new Message<>("paymentExtended",
                        PaymentExtendedEvent.builder()
                            .paymentId(v.getData().getPaymentId())
                            .sourceAccount(v.getData().getSourceAccount())
                            .destinationAccount(v.getData().getDestinationAccount())
                            .amount(v.getData().getAmount())
                            .originalAmount(v.getData().getAmount())
                            .currency(v.getData().getCurrency())
                            .originalCurrency(v.getData().getCurrency())
                            .exchangeRate(1.0)
                            .build()));

        KStream<String, Message<PaymentExtendedEvent>> paymentExtended = paymentExtendedForeign
                .merge(paymentExtendedChf);

        paymentExtended.peek((k, v) -> log.info("EXTENDED -> {}", v.getData()));

        paymentExtended.to(PAYMENT_EXTENDED_OUTPUT_TOPIC);

        return builder.build();
    }
}
