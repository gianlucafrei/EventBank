package com.example.eventbank.accounts.service.interf;

import com.example.eventbank.accounts.eventLog.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@AllArgsConstructor
@Data
public class PaymentCommand extends Event {

    private String debtorId;
    private String creditorId;
    private Integer amount;
    private Optional<String> reservationId;
}
