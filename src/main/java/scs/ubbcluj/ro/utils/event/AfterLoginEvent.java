package scs.ubbcluj.ro.utils.event;

import javafx.event.EventType;

public class AfterLoginEvent extends javafx.event.Event implements Event{

    public AfterLoginEvent(EventType<? extends javafx.event.Event> eventType) {
        super(eventType);
    }
}
