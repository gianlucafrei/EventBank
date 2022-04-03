package com.example.eventbank.accounts.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentEvent {

    private String sourceAccount;
    private String destinationAccount;
    private Integer amount;

    public static PaymentEvent fromData(Object data) throws Exception {

        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(data);
        return om.readValue(json, PaymentEvent.class);
    }
}
