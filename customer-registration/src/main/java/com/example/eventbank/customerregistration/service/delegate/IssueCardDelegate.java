package com.example.eventbank.customerregistration.service.delegate;

import com.example.eventbank.customerregistration.adapter.out.messaging.IssueCardProducer;
import com.example.eventbank.customerregistration.service.interf.IssueCardEvent;
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

        IssueCardEvent issueCardEvent = new IssueCardEvent(
                delegateExecution.getVariable("firstName").toString(),
                delegateExecution.getVariable("lastName").toString(),
                delegateExecution.getVariable("accountId").toString()
        );

        issueCardProducer.issueCard(issueCardEvent, delegateExecution.getProcessBusinessKey());
    }
}
