package com.example.eventbank.cards.service;

import com.example.eventbank.cards.dto.PaymentEvent;
import com.example.eventbank.cards.dto.PaymentRequest;
import com.example.eventbank.cards.dto.PaymentResultEvent;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

@Getter
@Log4j2
public class CardPaymentSaga {

    private static final int MAX_RETRIES = 5;

    public enum CardPaymentSagaStatus {
        OPEN, RESERVING_AMOUNT, AWAITING_PAYMENT, SUCCESS, ERROR
    }

    private CardPaymentSagaStatus status;
    private PaymentRequest paymentRequest;
    private AccountsServiceAdapater accountsServiceAdapater;
    private PaymentEvent paymentEvent;
    private final String paymentId;

    private int numberOfRetries = 0;

    public CardPaymentSaga(PaymentRequest request, AccountsServiceAdapater accountsServiceAdapater){
        this.paymentRequest = request;
        this.accountsServiceAdapater = accountsServiceAdapater;
        this.paymentId = UUID.randomUUID().toString();
    }

    public boolean isOpen() {

        return this.status != CardPaymentSagaStatus.SUCCESS && this.status != CardPaymentSagaStatus.ERROR;
    }

    public void retry() throws Exception {

        if(numberOfRetries >= MAX_RETRIES){
            this.status = CardPaymentSagaStatus.ERROR;
            return;
        }

        this.numberOfRetries += 1;

        // We retry only the payment execution step
        if(this.status == CardPaymentSagaStatus.AWAITING_PAYMENT){

            executePayment(this.paymentEvent);
        }
    }


    public PaymentEvent startExecution(){

        // Create payment event
        this.paymentEvent = new PaymentEvent(
                this.paymentId,
                paymentRequest.getSourceAccount(),
                paymentRequest.getDestinationAccount(),
                paymentRequest.getAmount());

        try{

            // Reserve amount only if payment is risky
            boolean isRisky = isRiskyPayment(paymentRequest);
            if(isRisky){

                this.status = CardPaymentSagaStatus.RESERVING_AMOUNT;
                reserveAmount(paymentEvent);
            }

            // Send payment event
            executePayment(paymentEvent);
            this.status = CardPaymentSagaStatus.AWAITING_PAYMENT;
            return paymentEvent;
        }
        catch (Exception ex){
            log.warn("Payment saga failed: {}", ex);
        }
        return paymentEvent;
    }

    public void receivedPaymentResult(PaymentResultEvent paymentResultEvent) {

        // Set status to success if payment result is successful
        // Otherwise we would here need to undo saga steps that already have been done

        if(paymentResultEvent.isSuccess()){
            this.status = CardPaymentSagaStatus.SUCCESS;
        }
        else{
            this.status = CardPaymentSagaStatus.ERROR;
            log.warn("Received payment result with negative status for payment id={}: {}", paymentId, paymentResultEvent.getMessage());
        }
    }

    private boolean isRiskyPayment(PaymentRequest paymentRequest){

        return paymentRequest.getAmount() > 1000;
    }

    private void reserveAmount(PaymentEvent paymentEvent) throws Exception {

        accountsServiceAdapater.reserveAmount(paymentEvent.getSourceAccount(), paymentEvent.getAmount(), this.paymentId);
    }

    private void executePayment(PaymentEvent paymentEvent) throws Exception{

        accountsServiceAdapater.sendPaymentEvent(paymentEvent);
    }
}
