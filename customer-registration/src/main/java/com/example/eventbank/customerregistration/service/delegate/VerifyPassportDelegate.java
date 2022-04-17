package com.example.eventbank.customerregistration.service.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("VerifyPassportDelegate")
@Slf4j
@RequiredArgsConstructor
public class VerifyPassportDelegate implements JavaDelegate {

    private static final String SWISS_PASSPORT = "X\\d{7,}";

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String passportNr = delegateExecution.getVariable("passportNr").toString();

        log.info("Passport verification for task {}", delegateExecution.getCurrentActivityId());
        log.info("Validating Passport Nr: {}", passportNr);

        Pattern pattern = Pattern.compile(SWISS_PASSPORT);
        Matcher matcher = pattern.matcher(passportNr);
        delegateExecution.setVariable("passportValid", matcher.matches());
    }
}
