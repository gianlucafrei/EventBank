package com.example.eventbank.currencies.streams;


import com.example.eventbank.currencies.dto.serdes.MessageSerDe;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


@Log4j2
@Service
public class CurrencyStream {

    private Path stateDirectory;
    @Getter
    private KafkaStreams streams;

    private static final String BOOTSTRAP_SERVERS = "localhost:29092";

    @PostConstruct
    public void init() {
        try {
            this.stateDirectory = Files.createTempDirectory("kafka-streams");
        } catch (final IOException e) {
            throw new UncheckedIOException("Cannot create temporary directory", e);
        }

        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "paymentRiskProcessor-1");
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        settings.put(StreamsConfig.STATE_DIR_CONFIG, this.stateDirectory.toAbsolutePath().toString());

        settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, MessageSerDe.class.getName());
        settings.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, MessageTimestampExtractor.class.getName());


        final Topology topology = CurrencyTopology.build();

        streams = new KafkaStreams(topology, settings);
        streams.start();
    }
}
