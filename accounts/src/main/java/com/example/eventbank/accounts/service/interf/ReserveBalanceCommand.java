package com.example.eventbank.accounts.service.interf;

import com.example.eventbank.accounts.eventLog.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReserveBalanceCommand extends Event {

    private String accountId;
    private Integer amount;
}
