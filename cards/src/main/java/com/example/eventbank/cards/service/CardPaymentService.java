package com.example.eventbank.cards.service;

import com.example.eventbank.cards.dto.PaymentEvent;
import com.example.eventbank.cards.dto.PaymentRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Log4j2
public class CardPaymentService {

    @Autowired
    AccountsServiceAdapater accountsServiceAdapater;

    public PaymentEvent processPayment(PaymentRequest paymentRequest) throws Exception {

        // Return 401 status if payment is not authorized
        if(! checkPaymentAuthorization(paymentRequest)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Payment is not authorized");
        }

        // Create payment event
        PaymentEvent paymentEvent = new PaymentEvent(
                paymentRequest.getSourceAccount(),
                paymentRequest.getDestinationAccount(),
                paymentRequest.getAmount());

        // Reserve amount if payment is risky
        if(isRiskyPayment(paymentRequest)){

            accountsServiceAdapater.reserveAmount(
                    paymentRequest.getSourceAccount(),
                    paymentEvent.getAmount());
        }

        // Send payment asynchronously
        accountsServiceAdapater.sendPaymentEvent(paymentEvent);

        return paymentEvent;
    }

    private boolean isRiskyPayment(PaymentRequest paymentRequest){

        return paymentRequest.getAmount() > 1000;
    }

    // Method for checking payment authorization
    private boolean checkPaymentAuthorization(PaymentRequest paymentRequest){

        // Mock: If authorizationCode is null return false else default to true
        return paymentRequest.getAuthorizationCode() != null;
    }
}
