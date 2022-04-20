# ADR 7 - Onboarding Communication

## Context

In adr 6 we decided that the customer onboarding workflow uses an orchestration pattern for coordination. Thus, 
the orchestrator (customer-registration) microservice needs to communicate with the card, accounts and notification 
microservices. Each of these communications can be done either synchronously or asynchronously. 

## Decision

We decided to do the communication with the accounts microservice synchronous and the communications with the 
card and notification microservices asynchronous. The main reason for the synchronous communication with accounts is to
allow an easier error handling in the workflow. To promote performance and decoupling the communication to the 
notification service is asynchronous and additionally "fire and forget". In the card microservice we additionally wait
for a success reply before continuing the workflow. Here we need the added reliability.


## Result

During communication with the accounts service the thread will be blocking when waiting for an answer. However,
the creation of an account is fast process. Hence, this drawback is acceptable. The main and additional workflows can 
be directly decided from the messaging reply. 

The workflow from the card creation is dependent on an additional message from the card service. Hence, alternative
workflows, retries and timeouts from errors are more difficult to manage. 

The messaging to the notification service will be sent out without any guarantees. This means we don't know if the 
notification will ever be executed. 