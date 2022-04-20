package com.example.eventbank.cards.web;

import com.example.eventbank.cards.dto.PaymentRequest;
import com.example.eventbank.cards.service.CardPaymentService;
import com.example.eventbank.cards.service.ReservationFailedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class PaymentController {

    @Autowired
    CardPaymentService cardPaymentService;

    @PostMapping("/payment")
    public ResponseEntity payment(@RequestBody PaymentRequest paymentRequest) {

        // Return 400 status if payment is not valid
        if (!validatePayment(paymentRequest)) {
            return new ResponseEntity("Payment is not valid", HttpStatus.BAD_REQUEST);
        }

        try {
            cardPaymentService.processPayment(paymentRequest);
        }
        catch (ReservationFailedException e){

            return new ResponseEntity("Payment blocked, Sorry :(", HttpStatus.I_AM_A_TEAPOT);
        }
        catch (Exception e) {

            log.error("Unhandled exception while processing request {}", e.getMessage());
            return new ResponseEntity("internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity("Success", HttpStatus.ACCEPTED);
    }

    // Validate payment
    private boolean validatePayment(PaymentRequest paymentRequest) {

        // Check if amount is not smaller than 0
        if (paymentRequest.getAmount() < 0) {
            return false;
        }

        // Check if source account is not null
        if (paymentRequest.getSourceAccount() == null) {
            return false;
        }

        // Check if destination account is not null
        return paymentRequest.getDestinationAccount() != null;
    }

}
