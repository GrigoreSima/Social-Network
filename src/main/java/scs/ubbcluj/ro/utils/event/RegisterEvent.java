package scs.ubbcluj.ro.utils.event;

import javafx.event.EventType;

public class RegisterEvent extends javafx.event.Event implements Event{

    public RegisterEvent(EventType<? extends javafx.event.Event> eventType) {
        super(eventType);
    }
}
