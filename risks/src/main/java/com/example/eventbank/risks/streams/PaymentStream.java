package com.example.eventbank.risks.streams;

import com.example.eventbank.risks.dto.Message;
import com.example.eventbank.risks.dto.PaymentResultEvent;
import com.example.eventbank.risks.dto.PaymentResultEventSerDe;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.SessionWindowedKStream;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

@Log4j2
@Service
public class PaymentStream {

    public static final int WINDOW_SIZE_SECONDS = 10;

    private Path stateDirectory;
    private String bootstrapServers = "localhost:29092";

    @PostConstruct
    public void init() {

        String inputTopic = "payment-reply-events";

        try {
            this.stateDirectory = Files.createTempDirectory("kafka-streams");
        } catch (final IOException e) {
            throw new UncheckedIOException("Cannot create temporary directory", e);
        }

        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "paymentRiskProcessor-1");
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        settings.put(StreamsConfig.STATE_DIR_CONFIG, this.stateDirectory.toAbsolutePath().toString());

        settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, PaymentResultEventSerDe.class.getName());
        settings.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, MessageTimestampExtractor.class.getName());

        // Here we create the streaming topology

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Message<PaymentResultEvent>> messageStream = builder.stream(inputTopic);

        SessionWindowedKStream<String, Message<PaymentResultEvent>> windowedStream = messageStream
                .groupBy((key, msg) -> msg.getData().getDebtorId())
                .windowedBy(SessionWindows.with(Duration.ofSeconds(WINDOW_SIZE_SECONDS)));

        windowedStream.aggregate(
                () -> new PaymentsAggregate(),
                (key, value, agg) -> PaymentsAggregate.add(key, agg, value),
                (key, a, b) -> PaymentsAggregate.merge(key, a, b),
                Materialized.with(Serdes.String(), new PaymentAggregateSerde()))
                .toStream()
                .filter((key, agg) -> agg!=null && agg.getEvents().size() >= 5)
                .peek((key, agg) -> {
                    // Handle payment peeks
                    if(agg.getEvents().size() < 10){
                        // Block the card, this is super fast
                        log.info("Account: '{}': Warning you made {} payments in a short time",
                                agg.getAccountName(), agg.numberOfPayments(), WINDOW_SIZE_SECONDS);
                    }
                    else {
                        // Let's send a message to the user
                        log.info("Account: '{}': Warning you made {} in a short time. We will block your card to secure your account",
                                agg.getAccountName(), agg.numberOfPayments(), WINDOW_SIZE_SECONDS);
                    }
                });

        final Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, settings);
        streams.start();
    }
}
