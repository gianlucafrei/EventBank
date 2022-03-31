package com.example.eventbank.accounts.service.interf;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BalanceQueryResult {

    private String accountId;
    private Integer accountBalance;
    private Integer availableBalance;
}
