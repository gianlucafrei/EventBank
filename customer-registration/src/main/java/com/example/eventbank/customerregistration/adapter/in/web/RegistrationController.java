package com.example.eventbank.customerregistration.adapter.in.web;

import com.example.eventbank.customerregistration.dto.Message;
import com.example.eventbank.customerregistration.dto.RegistrationProcessDto;
import com.example.eventbank.customerregistration.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController()
@RequestMapping("/registration")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final static String MESSAGE_START = "MessageApply";

    private final MessageService messageService;

    @PostMapping("/apply")
    public ResponseEntity<String> applyForRegistration(@RequestBody RegistrationProcessDto registrationProcessDto) {

        String correlationId = UUID.randomUUID().toString();

        Message<RegistrationProcessDto> message = new Message<>("RegistrationProcess", registrationProcessDto)
                .setCorrelationId(correlationId);

        try {
            messageService.correlateMessage(message, MESSAGE_START);

            return new ResponseEntity<>(correlationId, HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            return new ResponseEntity<>("There appears to be an issue with the application", HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
