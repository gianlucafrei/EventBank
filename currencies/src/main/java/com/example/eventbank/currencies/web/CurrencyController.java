package com.example.eventbank.currencies.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyController {

    @GetMapping("/")
    public String helloMessage() {
        return "Hello from Risks Service";
    }
}
