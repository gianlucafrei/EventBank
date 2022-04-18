# ADR 6 - Onboarding Workflow Coordination

## Context

The onboarding workflow involves multiple different microservices and communication between them. It requires
machine and manual user tasks. These vary significantly in duration and latency. Further, the state of the workflow
has to be maintained across the microservices. Additionally, also the error handling is diverse with different 
communication links, retries, timeouts, alternative processes and reverts.

The main decision is whether to use Orchestration or Choreography for the coordination of the different microservices. 

## Decision

We decided to use an orchestration pattern for the coordination because of the complexity and diversity of the customer onboarding workflow.
The coordination of the workflow will be done in a new microservice (customer-registration). State management has to be done across multiple microservices in a choreography which can
be avoided using the orchestration approach. Furthermore, we can manage the error handling in the orchestrator 
microservice instead of it propagating through multiple services.

## Result

We will implement the orchestrator microservice that is only used to orchestrate the onboarding workflow. Hence, 
no domain logic should be present in it. As a result, we have a centralized workflow and state management which 
simplifies updates, monitoring and troubleshooting. Maintaining state and alternative error workflows also simplifies 
the error handling and recoverability. In case our card issuing fails we can revert previous actions such as the 
account creation based on the state in the orchestrator and following a given alternative workflow. Hence, we decrease coupling and complexity of
these alternative workflows. 
Drawbacks of the orchestration approach are a reduced responsiveness and scalability, which is however not crucial
in the customer onboarding case. 

