package com.example.eventbank.accounts.web;

import com.example.eventbank.accounts.service.AccountsService;
import com.example.eventbank.accounts.service.interf.*;
import com.example.eventbank.accounts.web.dto.NewAccountDto;
import com.example.eventbank.accounts.web.dto.PaymentDto;
import com.example.eventbank.accounts.web.dto.ReservationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController()
@RequestMapping("/accounts")
public class AccountsController {

    @Autowired
    private AccountsService accountsService;

    @GetMapping("/")
    public AllAccountsQueryResult listAccounts(){

        AllAccountsQuery query = new AllAccountsQuery();
        AllAccountsQueryResult result = accountsService.queryAllAccounts(query);
        return result;
    }

    @PostMapping("/")
    public void createNewAccount(@RequestBody NewAccountDto newAccountDto){

        NewAccountCommand command = new NewAccountCommand(newAccountDto.getAccountId(), newAccountDto.getMinimalBalance());
        accountsService.createNewAccount(command);
    }

    @GetMapping("/{id}")
    public BalanceQueryResult getBalance(@PathVariable String id){

        BalanceQuery query = new BalanceQuery(id);
        BalanceQueryResult result = accountsService.queryBalance(query);
        return result;
    }

    @PostMapping("/{id}/payments")
    public void createPayment(@PathVariable String id, @RequestBody PaymentDto paymentDto){

        PaymentCommand command = new PaymentCommand(
                id,
                paymentDto.getCreditorId(),
                paymentDto.getAmount(),
                Optional.ofNullable(paymentDto.getReservationId()));

        accountsService.executePayment(command);
    }

    @PostMapping("/{id}/reservations")
    public void createReservation(@PathVariable String id, @RequestBody ReservationDto reservationDto){

        ReserveBalanceCommand command = new ReserveBalanceCommand(id, reservationDto.getAmount());
        accountsService.reserveAmount(command);
    }

}
