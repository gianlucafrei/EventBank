package com.example.eventbank.customerregistration.service.delegate;

import com.example.eventbank.customerregistration.adapter.out.web.AccountAdapter;
import com.example.eventbank.customerregistration.service.interf.CreateAccountCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;


@Service("CreateAccountDelegate")
@Slf4j
@RequiredArgsConstructor
public class CreateAccountDelegate implements JavaDelegate {

    private final AccountAdapter accountAdapter;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        CreateAccountCommand createAccountCommand = new CreateAccountCommand(
                delegateExecution.getVariable("accountId").toString(),
                (Integer) delegateExecution.getVariable("minimalBalance")
        );

        try {
            accountAdapter.createAccount(createAccountCommand);
        } catch (Exception ex) {
            log.error("Account creation failed: Throwing Error_AccountCreationFailed");
            throw new BpmnError("Error_AccountCreationFailed");
        }
    }
}
