package com.example.eventbank.accounts.eventLog;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
@Log4j2
public class EventLog {

    private ConcurrentLinkedDeque<EventLogEntry> logEntries;

    public EventLog() {
        this.logEntries = new ConcurrentLinkedDeque<>();
    }

    public List<EventLogEntry> getEvents(){

        return logEntries.stream().collect(Collectors.toList());

    }

    public <CMD extends Event, STATE>EventLogEntryBuilder createEvent(CMD command, STATE state) {

        return new EventLogEntryBuilder<CMD, STATE>(this, command, state);
    }

    public static class EventLogEntryBuilder<CMD extends Event, STATE>{

        private EventLog eventLog;
        private STATE state;
        private CMD command;
        private TriConsumer<CMD, STATE, String> processor;

        private EventLogEntryBuilder(EventLog eventLog, CMD command, STATE state){
            this.eventLog = eventLog;
            this.command = command;
            this.state = state;
        }

        public EventLogEntryBuilder<CMD, STATE> withProcessor(TriConsumer<CMD, STATE, String> processor){
            this.processor = processor;
            return this;
        }

        public String processAndPersist(){

            EventLogEntry entry = new EventLogEntry(command);

            try{

                this.processor.accept(command, this.state, entry.eventId);
                eventLog.logEntries.push(entry);
                log.info("Event {}: Processed and persisted event: {}", entry.eventId, command);

            }catch (Exception ex){

                log.warn("Fail  {}: Processing event failed: exception={} command={}", entry.eventId, ex.toString(), command.toString());
                throw ex;
            }

            return entry.eventId;
        }
    }

    private static class EventLogEntry{

        private Event event;
        private String eventId;
        private String type;

        private EventLogEntry(Event event){
            this.event = event;
            this.eventId = UUID.randomUUID().toString();
            this.type = event.getClass().getCanonicalName();
        }
    }
}
