package com.example.eventbank.customerregistration.service;

import com.example.eventbank.customerregistration.dto.Message;
import com.example.eventbank.customerregistration.util.VariablesUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.rest.dto.message.MessageCorrelationResultDto;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final RuntimeService runtimeService;

    public MessageCorrelationResult correlateMessage(Message<?> message, String messageName) {
        try {
            log.info("Consuming message {} : {}", messageName, message.getData());

            MessageCorrelationBuilder messageCorrelationBuilder = runtimeService.createMessageCorrelation(messageName);

            if (message.getData() != null) {
                messageCorrelationBuilder.setVariables(VariablesUtil.toVariableMap(message.getData()));
            }

            MessageCorrelationResult messageResult = messageCorrelationBuilder
                    .processInstanceBusinessKey(message.getCorrelationId())
                    .correlateWithResult();

            String messageResultJson = new ObjectMapper()
                    .writeValueAsString(MessageCorrelationResultDto.fromMessageCorrelationResult(messageResult));

            log.info("Correlation successful. Process Instance Id: {}", messageResultJson);
            log.info("Correlation key used: {}", message.getCorrelationId());

            return messageResult;
        } catch (MismatchingMessageCorrelationException e) {
            log.error("Issue when correlating the message: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unknown issue occurred", e);
        }
        return null;
    }
}
