package com.example.eventbank.cards.service;

import com.example.eventbank.cards.dto.PaymentEvent;
import com.example.eventbank.cards.dto.PaymentRequest;
import com.example.eventbank.cards.dto.PaymentResultEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CardPaymentService {

    @Autowired
    AccountsServiceAdapater accountsServiceAdapater;

    public Map<String, CardPaymentSaga> cardPaymentSagas = new ConcurrentHashMap<>();

    public PaymentEvent processPayment(PaymentRequest paymentRequest) throws Exception {

        // Return 401 status if payment is not authorized
        if(! checkPaymentAuthorization(paymentRequest)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Payment is not authorized");
        }

        // Now we create a new Sage
        CardPaymentSaga saga = new CardPaymentSaga(paymentRequest, accountsServiceAdapater);

        // Store sage and start execution
        cardPaymentSagas.put(saga.getPaymentId(), saga);
        return saga.startExecution();
    }

    public void handlePaymentResultEvent(PaymentResultEvent paymentResultEvent){

        if(! cardPaymentSagas.containsKey(paymentResultEvent.getPaymentId())){
            log.warn("Received payment result but have no payment sage matching");
            return;
        }

        CardPaymentSaga saga = cardPaymentSagas.get(paymentResultEvent.getPaymentId());

        saga.receivedPaymentResult(paymentResultEvent);
    }

    @Scheduled(fixedRate = 1000)
    public void checkOpenPaymentSagas(){

        List<CardPaymentSaga> openSagas = cardPaymentSagas.values().stream()
                .filter(s -> s.isOpen())
                .collect(Collectors.toList());

        log.debug("Check open sagas: we have {} open sagas", openSagas.size());

        openSagas.forEach(s -> {
            try {
                s.retry();
            } catch (Exception ex) {
                log.warn("Exception during saga retry: {}", ex);
            }
        });
    }

    // Method for checking payment authorization
    private boolean checkPaymentAuthorization(PaymentRequest paymentRequest){

        // Mock: If authorizationCode is null return false else default to true
        return paymentRequest.getAuthorizationCode() != null;
    }
}
