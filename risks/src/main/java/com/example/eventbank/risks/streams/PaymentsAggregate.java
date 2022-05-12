package com.example.eventbank.risks.streams;
import com.example.eventbank.risks.dto.Message;
import com.example.eventbank.risks.dto.PaymentResultEvent;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.protocol.types.Field;

import java.util.LinkedList;
import java.util.List;

@Log4j2
@Getter
public class PaymentsAggregate{

    private List<Message<PaymentResultEvent>> events = new LinkedList<>();

    /*
        Gets the name of the first account in the list
        All events should belong to the same account
     */
    public String getAccountName(){

        if(events.isEmpty())
            return null;

        return events.get(0).getData().getDebtorId();
    }

    public int numberOfPayments() {

        return events.size();
    }

    public static PaymentsAggregate add(String key, PaymentsAggregate agg, Message<PaymentResultEvent> event){

        agg.events.add(event);
        log.info("key={} add: {}", key, agg.events.size());
        return agg;
    }

    static PaymentsAggregate merge(String key, PaymentsAggregate agg1, PaymentsAggregate agg2){

        PaymentsAggregate result = new PaymentsAggregate();
        result.events.addAll(agg1.events);
        result.events.addAll(agg2.events);
        //log.info("Key={} merge: {}", key, result.events.size());
        return result;
    }

}
