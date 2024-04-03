package scs.ubbcluj.ro.utils.event;

import scs.ubbcluj.ro.domain.User;

public class UsersChangeEvent implements Event{
    private final ChangeEventType type;
    private final User data;
    private User oldData;

    public UsersChangeEvent(ChangeEventType type, User data) {
        this.type = type;
        this.data = data;
    }

    public UsersChangeEvent(ChangeEventType type, User data, User oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }
}
