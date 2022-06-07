package com.example.eventbank.currencies.dto.serdes;

import com.example.eventbank.currencies.dto.Message;
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
public class MessageSerDe implements Serde<Message<?>> {

    private final ObjectMapper om;

    public MessageSerDe() {

        om = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public Message<?> deserialize(String serializedString) throws IOException {

        return om.readValue(serializedString, new TypeReference<>() {
        });

    }

    @Override
    public Serializer<Message<?>> serializer() {

        return (s, object) -> {
            try {
                return om.writeValueAsBytes(object);
            } catch (JsonProcessingException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Deserializer<Message<?>> deserializer() {
        return (s, bytes) -> {
            try {
                String msg = new String(bytes, StandardCharsets.UTF_8);
                return MessageSerDe.this.deserialize(msg);
            } catch (IOException e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        };
    }
}
