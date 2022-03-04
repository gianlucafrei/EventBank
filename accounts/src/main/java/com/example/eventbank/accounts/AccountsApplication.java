package com.example.eventbank.accounts;

import com.example.eventbank.accounts.dto.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.function.Consumer;
import java.util.function.Supplier;


@SpringBootApplication
@EnableScheduling
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

}
