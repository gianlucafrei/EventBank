package com.example.eventbank.risks.dto;

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
public class PaymentResultEventSerDe implements Serde<Message<PaymentResultEvent>> {

    private final ObjectMapper om;

    public PaymentResultEventSerDe() {

        om = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public Message<PaymentResultEvent> deserialize(String serializedString) throws IOException {

        return om.readValue(serializedString, new TypeReference<Message<PaymentResultEvent>>() {});

    }

    @Override
    public Serializer<Message<PaymentResultEvent>> serializer() {

        return new Serializer<Message<PaymentResultEvent>>() {
            @Override
            public byte[] serialize(String s, Message<PaymentResultEvent> paymentResultEventMessage) {
                try {
                    return om.writeValueAsBytes(paymentResultEventMessage);
                } catch (JsonProcessingException e) {
                    log.error(e);
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public Deserializer<Message<PaymentResultEvent>> deserializer() {
        return new Deserializer<Message<PaymentResultEvent>>() {
            @Override
            public Message<PaymentResultEvent> deserialize(String s, byte[] bytes) {

                try {
                    String msg = new String(bytes, StandardCharsets.UTF_8);
                    return PaymentResultEventSerDe.this.deserialize(msg);
                } catch (IOException e) {
                    log.error(e);
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
