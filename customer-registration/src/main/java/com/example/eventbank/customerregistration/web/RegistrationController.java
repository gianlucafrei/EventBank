package com.example.eventbank.customerregistration.web;

import com.example.eventbank.customerregistration.web.dto.CamundaMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final KafkaTemplate<String, CamundaMessageDto> kafkaTemplate;

    @PostMapping("/")
    public ResponseEntity<String> applyForRegistration(@RequestBody CamundaMessageDto camundaMessageDto){

        try{
            kafkaTemplate.send("start-process-message-topic", camundaMessageDto);

            return new ResponseEntity<>("Received application", HttpStatus.ACCEPTED);
        }catch (Exception ex){
            return new ResponseEntity<>("There appears to be an issue with the application", HttpStatus.NOT_ACCEPTABLE);
        }
    }
    
    @PostMapping("/intermediate")
    public void correlateIntermediateMessage(@RequestBody String correlationId){
        kafkaTemplate.send("intermediate-message-topic", CamundaMessageDto.builder().correlationId(correlationId).build());
    }

}
