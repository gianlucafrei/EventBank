# ADR 2 - Payment Flow

## Context

The card service receives payment requests from card a payment network provider. (i.e. MasterCard, Visa, etc.) Those payment providers have very strict requirements for the card payment to be successful, most importantly the request must be handles very fast. In practice, we would have hard timers that would trigger a failure if the request was not handled within a certain time frame.

On the other hand we have also some requirements for the card service to be reliable and resilient to failures. i.e. if we make a transaction of the accounting service we must make sure that the card payment is also successful. Vice versa if the accounting service fails we must make sure that the card payment also fails.

## Decision

We decided that we split up the card payment into two parts. First we reserve the amount on the client account and only at a later point we actually charge execute the payment.

Furthermore, we decided to go for a risk-based approach were we reserve the amount only if the payment is considered as risky. This is done by the card service which speeds up the card payment process and also reduces the risk of a card payment failure.

Reserving the amount is done transactional with an HTTP request to the accounting service. The actual payment is done synchronically via an event.

## Result

Because the payment is done asynchronously the card payment is much faster. Furthermore, because we split the payment into a reservation and actual execution the process is much more resilient. In the case that for example the reservation succeeds but the card service fails we just have an open reservation which can be cleaned up much easier that if we processed the actual payments.

In the case that the reservation fails the card payment will fail as well.

Then we also have the case that the payment was considered as not risky but then the actual payment fails at a later stage. This could be for example the case if the client account has insufficient funds. In this case we have a business problem because we cannot undo the card transaction. In this case we have to contact the client manually and inform him that the card payment failed.

## Remark

In an actual bank system it is usually not possible to delete a payment transaction but only making a reverse transaction. Also, because a transaction has usually the consequence because it is usually done to another bank via an inter-banking payment network. A simple reservation however, can be reversed much easier.
