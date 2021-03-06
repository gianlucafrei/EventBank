package com.example.eventbank.accounts.messaging;

import com.example.eventbank.accounts.service.AccountsService;
import com.example.eventbank.accounts.service.interf.PaymentCommand;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@Log4j2
public class PaymentsAdapter {

    @Autowired
    private AccountsService accountsService;
    Random rd = new Random();


    public void consumePaymentMessage(PaymentExtendedEvent paymentExtendedEvent){

        // We trow a coin and if it's head we just ignore the message to test the retries
        /*if(rd.nextBoolean()){

            log.info("Let's skipp this message: {}", paymentEvent);
            return;
        }*/

        log.info("Consuming new payment event: {}", paymentExtendedEvent);
        PaymentCommand paymentCommand = new PaymentCommand(
                paymentExtendedEvent.getPaymentId(),
                paymentExtendedEvent.getSourceAccount(),
                paymentExtendedEvent.getDestinationAccount(),
                paymentExtendedEvent.getAmount(),
                Optional.empty()
        );

        accountsService.executePayment(paymentCommand);
    }
}
