package com.example.eventbank.customerregistration.web;

import com.example.eventbank.customerregistration.consumer.MessageService;
import com.example.eventbank.customerregistration.dto.CamundaMessageDto;
import com.example.eventbank.customerregistration.dto.RegistrationProcessDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController()
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final static String MESSAGE_START = "MessageApply";

    private final KafkaTemplate<String, CamundaMessageDto> kafkaTemplate;
    private final MessageService messageService;

    @PostMapping("/apply")
    public ResponseEntity<String> applyForRegistration(@RequestBody RegistrationProcessDto registrationProcessDto) {

        String correlationId = UUID.randomUUID().toString();

        CamundaMessageDto camundaMessageDto = CamundaMessageDto.builder()
                .correlationId(correlationId).dto(registrationProcessDto).build();

        try {
            messageService.correlateMessage(camundaMessageDto, MESSAGE_START);

            return new ResponseEntity<>(correlationId, HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            return new ResponseEntity<>("There appears to be an issue with the application", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/intermediate")
    public void correlateIntermediateMessage(@RequestBody String correlationId) {
        kafkaTemplate.send("intermediate-message-topic", CamundaMessageDto.builder().correlationId(correlationId).build());
    }

}
