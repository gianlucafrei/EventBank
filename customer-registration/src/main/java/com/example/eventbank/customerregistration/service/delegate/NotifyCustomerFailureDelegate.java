package com.example.eventbank.customerregistration.service.delegate;

import com.example.eventbank.customerregistration.adapter.out.messaging.NotificationProducer;
import com.example.eventbank.customerregistration.service.interf.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;


@Service("NotifyCustomerFailureDelegate")
@Slf4j
@RequiredArgsConstructor
public class NotifyCustomerFailureDelegate implements JavaDelegate {

    private final NotificationProducer notificationProducer;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        EmailNotificationEvent emailNotificationEvent = new EmailNotificationEvent(
                delegateExecution.getVariable("emailAddress").toString(),
                "Application to EventBank",
                "Dear " + delegateExecution.getVariable("lastName") + ", \n" +
                        "unfortunately there was an issue with your Passport. Please contact us for more details."
        );

        notificationProducer.sendEmailNotification(emailNotificationEvent);
    }
}
