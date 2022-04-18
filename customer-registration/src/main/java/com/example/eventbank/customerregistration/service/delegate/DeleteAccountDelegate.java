package com.example.eventbank.customerregistration.service.delegate;

import com.example.eventbank.customerregistration.adapter.out.web.AccountAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;


@Service("CreateAccountDelegate")
@Slf4j
@RequiredArgsConstructor
public class DeleteAccountDelegate implements JavaDelegate {

    private final AccountAdapter accountAdapter;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        String accountId = delegateExecution.getVariable("accountId").toString();

        accountAdapter.removeAccount(accountId);
    }
}
