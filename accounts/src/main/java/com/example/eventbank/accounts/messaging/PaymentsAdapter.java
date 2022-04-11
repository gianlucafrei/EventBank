package com.example.eventbank.accounts.messaging;

import com.example.eventbank.accounts.service.AccountsService;
import com.example.eventbank.accounts.service.interf.PaymentCommand;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class PaymentsAdapter {

    @Autowired
    private AccountsService accountsService;

    public void consumePaymentMessage(PaymentEvent paymentEvent){

        log.info("Consuming new payment event: {}", paymentEvent);

        PaymentCommand paymentCommand = new PaymentCommand(
                paymentEvent.getPaymentId(),
                paymentEvent.getSourceAccount(),
                paymentEvent.getDestinationAccount(),
                paymentEvent.getAmount(),
                Optional.empty()
        );

        accountsService.executePayment(paymentCommand);
    }
}
