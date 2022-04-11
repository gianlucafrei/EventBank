package com.example.eventbank.cards.service;

import com.example.eventbank.cards.dto.Message;
import com.example.eventbank.cards.dto.PaymentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;

@Service
@Log4j2
public class AccountsServiceAdapater {

    @Autowired
    private StreamBridge streamBridge;
    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();

    private String accountServiceUri = "http://localhost:8081";

    public void reserveAmount(String account, Integer amount, String paymentId) throws Exception{

        // Make a json object with the account and amount
        HashMap<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("paymentId", paymentId);

        String bodyStr = objectMapper.writeValueAsString(body);
        URI uri = URI.create(
                accountServiceUri + "/accounts/" + UriUtils.encodePath(account, "UTF-8") +"/reservations");

        // Send http request to the account service
        // Using the java http client library
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMillis(100))
                .POST(HttpRequest.BodyPublishers.ofString(bodyStr))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200){

            log.error("Error reserving amount. Status code {} uri {}", response.statusCode(), uri);
            throw new RuntimeException("Error reserving amount for payment");
        }
    }

    public void sendPaymentEvent(PaymentEvent paymentEvent){

        Message message = new Message<>("paymentEvent", paymentEvent);
        streamBridge.send("payment-out-0", message);
        log.info("Sent paymentEvent to payment-out-0 via StreamBridge");
    }
}
