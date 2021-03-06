package com.example.eventbank.cards.service;

import com.example.eventbank.cards.dto.PaymentRequestEvent;
import com.example.eventbank.cards.dto.PaymentRequest;
import com.example.eventbank.cards.dto.PaymentResultEvent;
import com.example.eventbank.cards.web.AccountsServiceAdapater;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Log4j2
public class CardPaymentSaga {

    private static final int MAX_RETRIES = 5;
    private final String paymentId;
    private final PaymentRequest paymentRequest;
    private final AccountsServiceAdapater accountsServiceAdapater;
    private CardPaymentSagaStatus status;
    private PaymentRequestEvent paymentRequestEvent;
    private Instant start, end;
    private int numberOfRetries = 0;

    public CardPaymentSaga(PaymentRequest request, AccountsServiceAdapater accountsServiceAdapater) {
        this.paymentRequest = request;
        this.accountsServiceAdapater = accountsServiceAdapater;
        this.paymentId = UUID.randomUUID().toString();
    }

    public boolean isOpen() {

        return this.status != CardPaymentSagaStatus.SUCCESS && this.status != CardPaymentSagaStatus.ERROR;
    }

    public boolean isFailed() {

        return this.status == CardPaymentSagaStatus.ERROR;
    }

    public void retry() throws Exception {


        if (numberOfRetries >= MAX_RETRIES) {
            this.status = CardPaymentSagaStatus.ERROR;
            finish();
            log.warn("Saga maxed out in retries, paymentId={} This must be investigated manually", this.numberOfRetries, this.paymentId);
            return;
        }

        this.numberOfRetries += 1;


        log.info("Saga Retry No. {}, PaymentId={}", this.numberOfRetries, this.paymentId);

        // We retry only the payment execution step
        if (this.status == CardPaymentSagaStatus.AWAITING_PAYMENT) {

            executePayment(this.paymentRequestEvent);
        }
    }

    public PaymentRequestEvent startExecution() throws Exception {

        this.start = Instant.now();

        // Create payment event
        this.paymentRequestEvent = new PaymentRequestEvent(
                this.paymentId,
                paymentRequest.getSourceAccount(),
                paymentRequest.getDestinationAccount(),
                paymentRequest.getAmount(),
                paymentRequest.getCurrency());
        try {

            // Reserve amount only if payment is risky
            boolean isRisky = isRiskyPayment(paymentRequest);
            if (isRisky) {

                this.status = CardPaymentSagaStatus.RESERVING_AMOUNT;
                reserveAmount(paymentRequestEvent);
            }

        } catch (Exception ex) {
            log.warn("Payment saga failed: {}", ex);
            throw new ReservationFailedException(ex);
        }

        // Send payment event
        executePayment(paymentRequestEvent);
        this.status = CardPaymentSagaStatus.AWAITING_PAYMENT;
        return paymentRequestEvent;
    }

    public void receivedPaymentResult(PaymentResultEvent paymentResultEvent) {

        // Set status to success if payment result is successful
        // Otherwise we would here need to undo saga steps that already have been done

        if (paymentResultEvent.isSuccess()) {
            log.info("Payment succeeded after {} retries. PaymentId={}", this.numberOfRetries, this.paymentId);
            finish();
            this.status = CardPaymentSagaStatus.SUCCESS;
        } else {
            this.status = CardPaymentSagaStatus.ERROR;
            finish();
            log.warn("Received payment result with negative status for payment id={}: {}", paymentId, paymentResultEvent.getMessage());
        }
    }

    private boolean isRiskyPayment(PaymentRequest paymentRequest) {

        return paymentRequest.getAmount() >= 1000;
    }

    private void reserveAmount(PaymentRequestEvent paymentRequestEvent) throws Exception {

        accountsServiceAdapater.reserveAmount(
                paymentRequestEvent.getSourceAccount(),
                paymentRequestEvent.getAmount(),
                this.paymentId,
                paymentRequestEvent.getCurrency()
        );
    }

    private void executePayment(PaymentRequestEvent paymentRequestEvent) throws Exception {

        accountsServiceAdapater.sendPaymentEvent(paymentRequestEvent);
    }

    private void finish() {

        this.end = Instant.now();
        Duration duration = Duration.between(this.start, this.end);

        log.info("Saga ended after {}ms, status={}, paymentid={}", duration.toMillis(), this.status, this.paymentId);
    }

    public enum CardPaymentSagaStatus {
        OPEN, RESERVING_AMOUNT, AWAITING_PAYMENT, SUCCESS, ERROR
    }
}
