package com.example.eventbank.currencies.dto.converters;

import com.example.eventbank.currencies.dto.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class MessageDeSerializer implements Deserializer<Message<?>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message<?> deserialize(String topic, byte[] data) {
        try {
            objectMapper.findAndRegisterModules();
            return objectMapper.readValue(new String(data), Message.class);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
