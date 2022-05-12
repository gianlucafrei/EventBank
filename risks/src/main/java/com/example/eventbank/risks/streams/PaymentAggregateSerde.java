package com.example.eventbank.risks.streams;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

@Log4j2
public class PaymentAggregateSerde implements Serde<PaymentsAggregate> {

    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Serializer<PaymentsAggregate> serializer() {

        return (s, paymentsAggregate) -> {
            try {
                return om.writeValueAsBytes(paymentsAggregate);
            } catch (JsonProcessingException e) {
                log.warn("Error while serializing value");
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Deserializer<PaymentsAggregate> deserializer() {

        return (s, bytes) -> {
            try {

                return om.readValue(bytes, PaymentsAggregate.class);
            } catch (IOException e) {
                log.warn("Error while deserializing value");
                throw new RuntimeException(e);
            }
        };
    }
}
