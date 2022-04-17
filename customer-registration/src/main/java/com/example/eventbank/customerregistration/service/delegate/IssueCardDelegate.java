package com.example.eventbank.customerregistration.service.delegate;

import com.example.eventbank.customerregistration.adapter.out.messaging.IssueCardProducer;
import com.example.eventbank.customerregistration.dto.Message;
import com.example.eventbank.customerregistration.service.interf.IssueCardCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;


@Service("IssueCardDelegate")
@Slf4j
@RequiredArgsConstructor
public class IssueCardDelegate implements JavaDelegate {

    private final IssueCardProducer issueCardProducer;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        Message<IssueCardCommand> message = new Message<>("IssueCardEvent", new IssueCardCommand(
                delegateExecution.getVariable("firstName").toString(),
                delegateExecution.getVariable("lastName").toString()
        ));
        message.setCorrelationId(delegateExecution.getProcessBusinessKey());
        issueCardProducer.issueCard(message);
    }
}
