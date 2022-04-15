# ADR 5 - Persistance

## Context

Our banking system needs to persist the data. Implementing this persistence is a very important for a real system, however also a lot of work. However, this part of the system is quite independent from the event driven architecture.

## Decision

We decided to implement only in-memory persistence for the moment because we want to focus on the event driven architecture aspects.

## Result

If we do performance that or the like the results might be biased because the persistance is not realistic. However, we think it's better to focus on the most important aspects of the architecture for now.
