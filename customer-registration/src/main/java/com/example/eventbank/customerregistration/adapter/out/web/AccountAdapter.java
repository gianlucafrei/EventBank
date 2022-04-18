package com.example.eventbank.customerregistration.adapter.out.web;

import com.example.eventbank.customerregistration.service.interf.CreateAccountCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Component
@RequiredArgsConstructor
@Slf4j
public class AccountAdapter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value(value = "${account.uri}")
    private String accountUri;

    public void createAccount(CreateAccountCommand createAccountCommand) throws Exception {

        String json = objectMapper.writeValueAsString(createAccountCommand);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(accountUri + "/"))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Accounts creation response: {}", response);

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Account creation failed");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Http Request failed");
        }
    }

    public void removeAccount(String accountId) {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(accountUri + "/" + accountId))
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Accounts deletion response: {}", response);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Http Request failed");
        }
    }
}
