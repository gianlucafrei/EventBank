package com.example.eventbank.customerregistration.messaging;

import com.example.eventbank.customerregistration.dto.Message;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Log4j2
public class MessagingAdapter {

    @Autowired
    PaymentsAdapter paymentsAdapter;

    @Bean
    public Consumer<Message> globalTest() {
        return message -> consumeMessage(message);
    }

    public void consumeMessage(Message message){

        log.info("New Message: {}", message);

        try{

            if("paymentEvent".equals(message.getType())){

                PaymentEvent event = PaymentEvent.fromData(message.getData());
                paymentsAdapter.consumePaymentMessage(event);
            }
        }catch (Exception ex){
            log.error("Error while processing incoming message", ex);
        }

    }
}
