package com.example.eventbank.currencies.dto.serdes;

import com.example.eventbank.currencies.dto.Message;
import com.example.eventbank.currencies.dto.PaymentRequestEvent;
import org.apache.kafka.common.serialization.Serde;

public class MessageSerdes {

    public static Serde<Message<PaymentRequestEvent>> PaymentRequestEvent() {
        return new PaymentRequestEventSerde();
    }
}
