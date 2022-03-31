package com.example.eventbank.accounts.web;

import com.example.eventbank.accounts.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private StreamBridge streamBridge;

    @GetMapping("/")
    public String helloMessage(){

        Message<String> message = new Message("helloEvent", "Hello Endpoint was Called");
        streamBridge.send("global-out-0", message);
        logger.info("Hello event sent to global-out-0 via StreamBridge");

        return "Hello from Accounts Service";
    }
}
