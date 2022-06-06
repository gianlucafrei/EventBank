package com.example.eventbank.currencies.dto.serdes;

import com.example.eventbank.currencies.dto.Message;
import com.example.eventbank.currencies.dto.PaymentRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Log4j2
public class PaymentRequestEventSerde implements Serde<Message<PaymentRequestEvent>> {

    private final ObjectMapper om;

    public PaymentRequestEventSerde() {
        om = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public Message<PaymentRequestEvent> deserialize(String serializedString) throws IOException {

        return om.readValue(serializedString, new TypeReference<Message<PaymentRequestEvent>>() {
        });
    }

    @Override
    public Serializer<Message<PaymentRequestEvent>> serializer() {

        return (s, eventMessage) -> {
            try {
                // Message<PaymentRequestEvent> message = new Message<>("paymentEvent", eventMessage);
                return om.writeValueAsBytes(eventMessage);
            } catch (JsonProcessingException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Deserializer<Message<PaymentRequestEvent>> deserializer() {
        return (s, bytes) -> {
            try {
                String msg = new String(bytes, StandardCharsets.UTF_8);
                return PaymentRequestEventSerde.this.deserialize(msg);
            } catch (IOException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        };
    }
}
