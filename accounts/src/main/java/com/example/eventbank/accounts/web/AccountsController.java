package com.example.eventbank.accounts.web;

import com.example.eventbank.accounts.service.AccountsService;
import com.example.eventbank.accounts.service.interf.*;
import com.example.eventbank.accounts.web.dto.NewAccountDto;
import com.example.eventbank.accounts.web.dto.PaymentDto;
import com.example.eventbank.accounts.web.dto.ReservationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity createReservation(@PathVariable String id, @RequestBody ReservationDto reservationDto){

        ReserveBalanceCommand command = new ReserveBalanceCommand(id, reservationDto.getAmount());
        
        try{

            accountsService.reserveAmount(command);

            return new ResponseEntity("Amount reserved", HttpStatus.OK);
        }catch (AccountDoesNotExistException ex){
            return new ResponseEntity("Account does not exist", HttpStatus.PRECONDITION_FAILED);
        }catch (InsufficientAccountLimitException ex){
            return new ResponseEntity("Insufficient limit", HttpStatus.PRECONDITION_FAILED);
        }
    }

}
