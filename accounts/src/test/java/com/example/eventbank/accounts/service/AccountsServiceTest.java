package com.example.eventbank.accounts.service;

import com.example.eventbank.accounts.eventLog.EventLog;
import com.example.eventbank.accounts.service.interf.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountsServiceTest {



    @Test
    public void testPayment(){

        // Arrange
        EventLog eventLog = new EventLog();
        AccountsService service = new AccountsService(eventLog);
        service.createNewAccount(new NewAccountCommand("account1", -100));
        service.createNewAccount(new NewAccountCommand("account2", 0));

        // ACT
        service.executePayment(new PaymentCommand("somepaymentid","account1", "account2", 20, Optional.empty()));

        // Assert
        BalanceQueryResult account1 = service.queryBalance(new BalanceQuery("account1"));
        BalanceQueryResult account2 = service.queryBalance(new BalanceQuery("account2"));
        assertEquals(-20, account1.getAccountBalance());
        assertEquals(20, account2.getAccountBalance());
    }

    @Test
    public void testReservation(){

        // Arrange
        EventLog eventLog = new EventLog();
        AccountsService service = new AccountsService(eventLog);
        service.createNewAccount(new NewAccountCommand("account1", -100));
        service.createNewAccount(new NewAccountCommand("account2", 0));

        // ACT
        service.reserveAmount(new ReserveBalanceCommand("account1", 50));

        // This should work
        service.executePayment(new PaymentCommand("somepaymentid","account1", "account2", 40, Optional.empty()));

        // But not a second time
        assertThrows(InsufficientAccountLimitException.class, () -> {
            service.executePayment(new PaymentCommand("somepaymentid","account1", "account2", 40, Optional.empty()));
        });
    }
}