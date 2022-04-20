package com.example.eventbank.cards;

import com.example.eventbank.cards.dto.PaymentRequest;
import com.example.eventbank.cards.service.CardPaymentSaga;
import com.example.eventbank.cards.service.CardPaymentService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Log4j2
@SpringBootTest
class CardsLoadTest {

    final int numberOfPayments = 1000;

    @Autowired
    CardPaymentService paymentService;

    @Test
    void loadTest() throws InterruptedException {


        int sum = IntStream.range(0, numberOfPayments).parallel()
                .map(i -> {
                    try {
                        testSinglePayment(i);
                        return 1;
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();

        assertEquals(numberOfPayments, sum);

        long sagasCount = paymentService.cardPaymentSagas.values().size();
        long openSagasCount = Long.MAX_VALUE;
        long failedSagasCount = Long.MAX_VALUE;

        while (openSagasCount > 0) {

            openSagasCount = paymentService.cardPaymentSagas.values().stream().filter(CardPaymentSaga::isOpen).count();
            failedSagasCount = paymentService.cardPaymentSagas.values().stream().filter(CardPaymentSaga::isFailed).count();
            log.info("All payments sent: TotalSagas={} OpenSagas={} failed={}", sagasCount, openSagasCount, failedSagasCount);
            Thread.sleep(100);
        }

        log.info("Test Finished: TotalSagas={} OpenSagas={} failed={}", sagasCount, openSagasCount, failedSagasCount);
    }

    private void testSinglePayment(int number) throws Exception {

        PaymentRequest paymentRequest = new PaymentRequest("account1", "account2", 1, "");
        paymentService.processPayment(paymentRequest);

        Thread.sleep(50);
    }

}
