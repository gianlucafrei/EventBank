package com.example.eventbank.cards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;

public class Message<T> {

    private final String source = "eventbank-accounts";
    private final String datacontenttype = "application/json";
    private final String specversion = "1.0";
    // Cloud Events attributes (https://github.com/cloudevents/spec/blob/v1.0/spec.md)
    private String type;
    private String id = UUID.randomUUID().toString(); // unique id of this message
    @JsonFormat(shape = JsonFormat.Shape.STRING) // ISO-8601 compliant format
    private Instant time = Instant.now();
    private T data;
    private String correlationId;

    public Message() {
    }

    public Message(String type, T payload) {
        this.type = type;
        this.data = payload;
    }

    @Override
    public String toString() {
        return "Message [type=" + type + ", id=" + id + ", time=" + time + ", " +
                "data=" + data + "]";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getSource() {
        return source;
    }

    public String getDatacontenttype() {
        return datacontenttype;
    }

    public String getSpecversion() {
        return specversion;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Message<T> setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }
}




