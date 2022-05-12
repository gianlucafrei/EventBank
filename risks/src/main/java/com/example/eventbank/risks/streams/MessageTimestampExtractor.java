package com.example.eventbank.risks.streams;

import com.example.eventbank.risks.dto.Message;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

@Log4j2
public class MessageTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long l) {

        Object value = consumerRecord.value();

        if(Message.class.isInstance(value)){

            Message msg = (Message) value;
            return msg.getTime().toEpochMilli();
        }

        log.warn("Unknown message type: {}", consumerRecord.getClass().getCanonicalName());
        return 0;
    }
}
