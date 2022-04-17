package com.example.eventbank.customerregistration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationProcessDto implements Serializable {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String accountId;
    private Integer minimalBalance;

    public void initAccountId() {
        this.accountId = UUID.randomUUID().toString();
    }

    public void initMinimumBalance() {
        this.minimalBalance = 0;
    }
}
