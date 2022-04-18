package com.example.eventbank.cards.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Data
public class Card {

    private String cardId;
    private String cardNumber;
    private String holderName;
    private String expirationDate;
    private int pinCode;

    public Card(String firstName, String lastName) {
        this.cardId = UUID.randomUUID().toString();
        this.cardNumber = generateCardNumber();
        this.holderName = firstName + " " + lastName;
        this.expirationDate = generateExpirationDate();
        this.pinCode = generatePinCode();
    }

    private int generatePinCode() {
        return new Random().nextInt(10000);
    }

    private String generateExpirationDate() {
        DateTimeFormatter DateFor = DateTimeFormatter.ofPattern("MM/yyyy");

        return DateFor.format(LocalDate.now().plusYears(5));
    }

    private String generateCardNumber() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int digit = new Random().nextInt(10);
            builder.append(digit);
            if (i % 4 == 3 && i != 15) {
                builder.append(" ");
            }
        }

        return builder.toString();
    }
}
