package com.example.eventbank.risks.web;

import com.example.eventbank.risks.streams.PaymentDetectionTopology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    PaymentDetectionTopology stream;

    @GetMapping("/")
    public String helloMessage() {

        return "Hello from Risks Service: This is my topology: " + stream.topology.describe().toString();

    }
}
