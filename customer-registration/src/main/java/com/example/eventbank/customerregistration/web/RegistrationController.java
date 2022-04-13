package com.example.eventbank.customerregistration.web;

import com.example.eventbank.customerregistration.web.dto.CamundaMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final KafkaTemplate<String, CamundaMessageDto> kafkaTemplate;

    @PostMapping("/apply")
    public void applyForRegistration(@RequestBody CamundaMessageDto camundaMessageDto) {

        kafkaTemplate.send("start-process-message-topic", camundaMessageDto);
        /*
        try{
            kafkaTemplate.send("start-process-message-topic", camundaMessageDto);

            return new ResponseEntity<>("Received application", HttpStatus.ACCEPTED);
        }catch (Exception ex){
            return new ResponseEntity<>("There appears to be an issue with the application", HttpStatus.NOT_ACCEPTABLE);
        }
         */
    }

    @PostMapping("/intermediate")
    public void correlateIntermediateMessage(@RequestBody String correlationId) {
        kafkaTemplate.send("intermediate-message-topic", CamundaMessageDto.builder().correlationId(correlationId).build());
    }

}
