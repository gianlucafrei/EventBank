package com.example.eventbank.accounts.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewAccountDto {

    String accountId;
    Integer minimalBalance;

}
