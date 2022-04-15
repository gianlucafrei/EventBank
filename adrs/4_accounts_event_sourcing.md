# ADR 4 - Event Sourcing

## Context

The accounts service has very high requirements on atomaticity, reliability and auditability. In practice this requirements are so high that many banks still use mainframe computers for their core-banking systems. A distributed architecture is often impossible.

## Decision

We decided to implement the accounts service with an event-sourcing pattern. It keeps track of an central event-log which stores all state-changing events.

## Result

All changes in the accounts service are easy to audit because an corresponding event is stored in the log. If would be possible to replay the events to recover the state of the accounts service. Futhermore because this leads to an natural separation of commands and queries it would be possible to spin up copies of the accounting service that only process queries and do not process commands to still scale the system when load increases.
