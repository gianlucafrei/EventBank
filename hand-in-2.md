# Hand In 2

## Project Description

We extended our existing Event Bank project with two new features which are bases on Kafka stream processing. The first feature is **Payment Detection** which implements stateful stream processing to detect sudden pikes in transaction events. The second feature is **Foreign Currency Transaction** which adds information from a stream of exchange rates to the transactions. This features implements stateless stream processing, interactive lookups and stream joins.

As in the first part of the project each team member had his focus area. Jonathan mainly worked on the implementation of the **Foreign Currency Transaction** feature and Gian-Luca worked in the implementation of the **Payment Detection**. However, also like in part 1 we planned both features together and discussed the trade-offs and implementation hurdles in the complete project team.

## Payment Detection

The payment detection is implemented with stateful stream processing. It acts in a semi-passive ways, meaning if just consumes payment conformation events and if it detects a sudden spike in the number of transactions it will emit an warning. The code for this feature can be found in the risks service (`/risks`).

Although the streaming topology is quite small is brings a lot of complexity with it which we want to describe here. We decided to implement a relatively simple way to detect fraudulent payments: The risks service emits a warning if a certain number of transaction follow closely after each other. For demo purposes we choose a threshold of 5 transaction that occur with less than 10 seconds between them. In a real world bank payment fraud detection is even more complex and takes much more data points into consideration.

##### Topology

To implement this feature we use the following stream processing topology:

1. **Input Stream** We consume the payment result events that are emitted by the accounts service. Since payment can be processed with a certain delay we use a custom TimeStampExtractor to extract the original payment time which is stored in the event message.
2. **Group by AccountId**: From the payment event we extract the account id of the debtor and group the events by this id.
3. **Session Window of 10s** We use a Kafka Stream session window to group the event in a temporal manner. The session window works well for our use case because it open a new window with every event unless another window was opened less than 10s ago. This gives us the nice property that we group the events that are close to each. Compared to a rolling window the size of the session window is flexible and grows if more events are coming in.
4. **Aggregate to List**: We want to collect all close transaction for detailed analysis. For this we implemented a customer aggregate function that adds all events to a list and returns the list.
5. **Filter n>5**: Next we filter out the aggregates that are interesting for us. We only want to keep the aggregates that have more than 5 events in them. But of course we could also implement much more complex filters here. 
6. **For Each**: As a final step we emit a warning for each aggregate that has more than 5 events. The other banking services are responsible for handling this warning. They could for instance block the card or send an email to the customer.


##### Tradeoffs

The describes topology comes with one major tradeoff. Because we first aggregate the events to a list and in a next step do the filtering we run into an interesting problem. Kafka streams only closes the session window if is sees no more events for that account in for the session grace period which is 10s in our case. Kafka also only forward the aggregate to the next step if the window is closed. This results in the effect that for example after 5 payments occured and we could issue the warning our system waits 10s because maybe it there will be another payment. However, due to our use case this is not needed because we should directly emit the warning. Furthermore is opens up a possibility for an interesting attack: If an attacker uses the card every 10s forever the system will emit the warning because the session window is never closed.

We see two similar ways to solve this problem. First we see that the aggregate function is called as soon as the payment is added to the session window. Compared the the filter function Kafka Streams does not wait here until the window is closed. The reason for this is most likely performance optimization by Kafka Streams because aggregates can be expensive and therefore it makes sense to start with the computation as soon as as possible. We could exploit this by using the code from the aggregate function for the filtering and to emit the warning. Warnings would then be emitted much faster and we also don't need to wait until the window is closed. The same behavior could also be achieved with a customer processor using the Kafka Streams Processor API.

Both approaches come with some similar drawbacks: The current streaming topology is very self explaining and if one takes a look on the documentation they can clearly see what is going on which the different aggregate and filter steps. If we implemented the filtering within the aggregate function the topology would end up after the aggregate. Aggregations are usually without side-effects so this would be a very odd way to implement this. In the documentation we would need to show very explicitly that this aggregation has side-effects by design. Furthermore it is not documented by Kafka Streams that the aggregate function is called as soon as a new element is added to the window (At least we did not find it). To rely on this undocumented behavior would be a very bad practice and our code could start failing is Kafka Streams changes its internal implementation. Were the first reason could be discussed the second with the undocumented behavior is much more grave.

Due to this we decided to stay with our current *naive* implementation. Maybe the best way to implement this would be so implement a new window type that is similar to the session window but provides the processing guarantees that we need for the following steps. However, we quickly looked into it and decided that it would be a lot of work and we would not be able to implement the filter step in a reasonable time. Especially if we take into consideration that our implementation must work with different streaming an computing partitions. However, analyzing this problem was very interesting and both of us learned a lot about Kafka Streams internals and limitations.
