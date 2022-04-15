package com.example.eventbank.accounts.messaging;

import com.example.eventbank.accounts.dto.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Log4j2
public class MessagingAdapter {

    @Autowired
    private PaymentsAdapter paymentsAdapter;
    private ObjectMapper om = new ObjectMapper();

    @Bean
    public Consumer<Message> globalTest() {

        return message -> consumeMessage(message);
    }

    public void consumeMessage(Message message){

        log.info("New Message: {}", message);

        try{

            if("paymentEvent".equals(message.getType())){

                String json = om.writeValueAsString(message.getData());
                PaymentEvent event =  om.readValue(json, PaymentEvent.class);
                paymentsAdapter.consumePaymentMessage(event);
            }
        }catch (Exception ex){
            log.error("Error while processing incoming message", ex);
        }

    }
}
