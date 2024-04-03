package scs.ubbcluj.ro.utils.event;

import scs.ubbcluj.ro.domain.Message;

public class MessageChangeEvent implements Event {
    private final ChangeEventType type;
    private final Message data;
    private Message oldData;

    public MessageChangeEvent(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }

    public MessageChangeEvent(ChangeEventType type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}
