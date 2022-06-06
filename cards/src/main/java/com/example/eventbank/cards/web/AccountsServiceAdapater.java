package com.example.eventbank.cards.web;

import com.example.eventbank.cards.dto.ExchangeRateEvent;
import com.example.eventbank.cards.dto.Message;
import com.example.eventbank.cards.dto.PaymentRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;

@Component("accountServiceAdapter")
@Log4j2
public class AccountsServiceAdapater {

    @Autowired
    private StreamBridge streamBridge;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String accountServiceUri = "http://localhost:8081";
    private final String currencyServiceUri = "http://localhost:8086";

    public void reserveAmount(String account, Integer amount, String paymentId, String currency) throws Exception {

        int amountChf = amountInChf(amount, currency);
        // Make a json object with the account and amount
        HashMap<String, Object> body = new HashMap<>();
        body.put("amount", amountChf);
        body.put("paymentId", paymentId);

        String bodyStr = objectMapper.writeValueAsString(body);
        URI uri = URI.create(
                accountServiceUri + "/accounts/" + UriUtils.encodePath(account, "UTF-8") + "/reservations");

        // Send http request to the account service
        // Using the java http client library
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMillis(1000))
                .POST(HttpRequest.BodyPublishers.ofString(bodyStr))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("Error reserving amount. Status code {} uri {}", response.statusCode(), uri);
            throw new RuntimeException("Error reserving amount for payment");
        } else {
            log.info("RESERVED {} CHF on account {}", amountChf, account);
        }
    }

    private int amountInChf(Integer amount, String currency) throws Exception{

        if (currency.equalsIgnoreCase("CHF")) return amount;

        URI uri = URI.create(currencyServiceUri + "/rates/" + UriUtils.encodePath(currency, "UTF-8"));

        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMillis(1000))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("Error converting amount. Status code {} uri {}", response.statusCode(), uri);
            throw new RuntimeException("Error reserving amount for payment -> currency conversion failed");
        }

        objectMapper.findAndRegisterModules();
        ExchangeRateEvent exchangeRateEvent = objectMapper.readValue(response.body(), ExchangeRateEvent.class);

        return (int) (amount * exchangeRateEvent.getRate());
    }

    public void sendPaymentEvent(PaymentRequestEvent paymentRequestEvent) {

        Message message = new Message<>("paymentEvent", paymentRequestEvent);
        streamBridge.send("payment-out-0", message);
        log.info("Sent paymentEvent to payment-out-0: {}", paymentRequestEvent);
    }
}
