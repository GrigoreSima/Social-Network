package scs.ubbcluj.ro.utils.event;

import scs.ubbcluj.ro.domain.MessageGroup;

public class MessageGroupChangeEvent implements Event {
    private final ChangeEventType type;
    private final MessageGroup data;
    private MessageGroup oldData;

    public MessageGroupChangeEvent(ChangeEventType type, MessageGroup data) {
        this.type = type;
        this.data = data;
    }

    public MessageGroupChangeEvent(ChangeEventType type, MessageGroup data, MessageGroup oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public MessageGroup getData() {
        return data;
    }

    public MessageGroup getOldData() {
        return oldData;
    }
}
