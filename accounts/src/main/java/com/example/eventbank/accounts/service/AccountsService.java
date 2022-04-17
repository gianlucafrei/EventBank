package com.example.eventbank.accounts.service;

import com.example.eventbank.accounts.dto.Message;
import com.example.eventbank.accounts.eventLog.EventLog;
import com.example.eventbank.accounts.messaging.PaymentResultEvent;
import com.example.eventbank.accounts.service.interf.*;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AccountsService {

    private final EventLog eventLog;
    private final AccountServiceState state = new AccountServiceState();
    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    public AccountsService(EventLog eventLog) {
        this.eventLog = eventLog;
    }

    public void createNewAccount(NewAccountCommand command) throws Exception {

        TriConsumer<NewAccountCommand, AccountServiceState, String> processor = this::createNewAccountProcessor;
        eventLog.createEvent(command, state).withProcessor(processor).processAndPersist();

        log.info("New Account created: {}", command);
    }

    private void createNewAccountProcessor(NewAccountCommand newAccountCommand, AccountServiceState state, String eventId) throws IllegalStateException {

        if (state.accounts.containsKey(newAccountCommand.getAccountId())) {
            throw new IllegalStateException("Account already exists");
        }

        Account account = new Account(newAccountCommand.getMinimalBalance());
        state.accounts.put(newAccountCommand.getAccountId(), account);
    }

    public void executePayment(PaymentCommand command) {

        TriConsumer<PaymentCommand, AccountServiceState, String> processor = this::paymentProcessor;

        PaymentResultEvent resultEvent;
        try {
            eventLog.createEvent(command, state)
                    .withIdempotence(command.getPaymentId())
                    .withProcessor(processor)
                    .processAndPersist();

            resultEvent = new PaymentResultEvent(
                    command.getPaymentId(),
                    true,
                    "Payment stored",
                    command.getDebtorId(),
                    command.getCreditorId());

        } catch (Exception ex) {
            log.warn("Exception during payment processing {}", ex);

            resultEvent = new PaymentResultEvent(
                    command.getPaymentId(),
                    false,
                    "Payment Failed: " + ex.getMessage(),
                    command.getDebtorId(),
                    command.getCreditorId());
        }

        Message message = new Message<>("paymentResultEvent", resultEvent);
        streamBridge.send("payment-out-0", message);
        log.info("Sent paymentResultEvent to payment-out-0 via StreamBridge");
    }

    private void paymentProcessor(PaymentCommand paymentCommand, AccountServiceState state, String eventId) {

        if (!state.accounts.containsKey(paymentCommand.getCreditorId())) {
            throw new IllegalStateException("Creditor account does not exist");
        }

        if (!state.accounts.containsKey(paymentCommand.getDebtorId())) {
            throw new IllegalStateException("Debitor account does not exist");
        }

        Account debitor = state.accounts.get(paymentCommand.getDebtorId());
        Account creditor = state.accounts.get(paymentCommand.getCreditorId());

        // Execute Payment
        int availableBalance = debitor.availableBalance();
        if (availableBalance >= paymentCommand.getAmount()) {

            debitor.balance -= paymentCommand.getAmount();
            creditor.balance += paymentCommand.getAmount();

        } else {
            throw new InsufficientAccountLimitException();
        }
    }

    public void reserveAmount(ReserveBalanceCommand command) {

        TriConsumer<ReserveBalanceCommand, AccountServiceState, String> processor = this::reserveAmountProcessor;
        eventLog.createEvent(command, state).withProcessor(processor).processAndPersist();
    }

    private void reserveAmountProcessor(ReserveBalanceCommand reserveBalanceCommand, AccountServiceState state, String eventId) {

        if (!state.accounts.containsKey(reserveBalanceCommand.getAccountId())) {
            throw new AccountDoesNotExistException();
        }

        Account account = state.accounts.get(reserveBalanceCommand.getAccountId());
        int availableBalance = account.availableBalance();

        if (availableBalance > reserveBalanceCommand.getAmount()) {

            Reservation reservation = new Reservation(reserveBalanceCommand.getAmount());
            account.addReservation(reservation);
        } else {

            throw new InsufficientAccountLimitException();
        }
    }

    public BalanceQueryResult queryBalance(BalanceQuery query) {

        if (!state.accounts.containsKey(query.getAccountId())) {
            throw new IllegalStateException("Account id does not exist");
        }

        Account account = state.accounts.get(query.getAccountId());
        BalanceQueryResult result = new BalanceQueryResult(query.getAccountId(), account.getBalance(), account.availableBalance());

        log.debug("Processed balance query: {} Result: {}", query, result);
        return result;
    }

    public AllAccountsQueryResult queryAllAccounts(AllAccountsQuery query) {

        Set<BalanceQueryResult> results = state.accounts.entrySet()
                .stream().map(entry -> new BalanceQueryResult(entry.getKey(), entry.getValue().getBalance(), entry.getValue().availableBalance()))
                .collect(Collectors.toSet());
        return new AllAccountsQueryResult(results);
    }

    private static class AccountServiceState {

        private final HashMap<String, Account> accounts = new HashMap<>();

    }

    @Data
    private static class Account {

        private Integer balance;
        private Integer minimalBalance;
        private Set<Reservation> reservations;

        public Account(Integer minimalBalance) {

            this.balance = 0;
            this.minimalBalance = minimalBalance;
            this.reservations = new HashSet<>();
        }

        public Integer availableBalance() {

            return balance - minimalBalance - totalReservedAmount();
        }

        public Integer totalReservedAmount() {

            return reservations.stream().mapToInt(r -> r.amount).sum();
        }

        public void addReservation(Reservation reservation) {

            this.reservations.add(reservation);
        }
    }

    @Data
    private static class Reservation {

        private Integer amount;
        private String id;

        public Reservation(Integer amount) {
            this.amount = amount;
            this.id = UUID.randomUUID().toString();
        }
    }
}
