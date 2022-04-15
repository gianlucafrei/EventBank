# ADR 1 - Service Granularity

## Context

Our bank has three main functional requirements: Keeping track of client accounts, process card payments, and notify clients of their payments via push message. Those three functional requirements differ vastly in their non-functional requirements: The accounting must be auditable and always atomic, the card payments must also be atomic but also very responsive and resilient to failures. However, the push notifications don't need to be as robust as the other functionalities.

## Decision

We decided to split the bank into three main services:

- **Accounts**: Keeps track of client accounts and provides auditable information.
- **Cards**: Processes card payments handles authorization and provide fast responses to the caller.
- **Notifications**: Notifies clients of their payments via push message.

## Result

This three services are implemented in different microservice which is quite a natural way to split the different concerns. Furthermore the system is much more resilient because any of those services can fail and the other functionalities can still be used. For card payments the cards service is a single point of failure however this separation makes it possible to scale the payments independently from the accounts service which enables this architecture to scale even though the core banking part (accounts) would be much harder so scale due to the high transactional requirements it has.
