package com.example.eventbank.accounts;

import com.example.eventbank.accounts.eventLog.EventLog;
import com.example.eventbank.accounts.service.AccountsService;
import com.example.eventbank.accounts.service.interf.NewAccountCommand;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccountsApplication {

    @Autowired
    private AccountsService accountsService;

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("EventBank Account Service"));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {

        // Create some default accounts
        accountsService.createNewAccount(new NewAccountCommand("account1", -10_000_000));
        accountsService.createNewAccount(new NewAccountCommand("account2", -10_000_000));
        accountsService.createNewAccount(new NewAccountCommand("string", -10_000_000));
    }
}
