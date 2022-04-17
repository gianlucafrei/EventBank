package com.example.eventbank.customerregistration.service.interf;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateAccountCommand {

    private String accountId;
    private Integer minimalBalance;
}
