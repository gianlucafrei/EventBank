package com.example.eventbank.accounts.service.interf;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
public class AllAccountsQueryResult {

    private Set<BalanceQueryResult> accounts;

}
