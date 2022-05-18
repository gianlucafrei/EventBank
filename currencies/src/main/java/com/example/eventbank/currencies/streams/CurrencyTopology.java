package com.example.eventbank.currencies.streams;


import com.example.eventbank.currencies.dto.Message;
import com.example.eventbank.currencies.dto.PaymentResultEvent;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;


@Log4j2
public class CurrencyTopology {

    private static final String PAYMENT_INPUT_TOPIC = "payment-request-events";
    private static final String EXCHANGE_RATE_INPUT_TOPIC = "exchange-rate-events";

    public static Topology build() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Message<PaymentResultEvent>> paymentStream = builder.stream(PAYMENT_INPUT_TOPIC);
        // KStream<String, Message<PaymentResultEvent>> erStream = builder.stream(EXCHANGE_RATE_INPUT_TOPIC);

        paymentStream.peek((k, v) -> {
            log.info("Streaming: {}", v.getData());
        });


        return builder.build();
    }
}
