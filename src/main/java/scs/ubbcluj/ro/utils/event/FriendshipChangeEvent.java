package scs.ubbcluj.ro.utils.event;

import scs.ubbcluj.ro.domain.Friendship;

public class FriendshipChangeEvent implements Event {
    private final ChangeEventType type;
    private final Friendship data;
    private Friendship oldData;

    public FriendshipChangeEvent(ChangeEventType type, Friendship data) {
        this.type = type;
        this.data = data;
    }

    public FriendshipChangeEvent(ChangeEventType type, Friendship data, Friendship oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Friendship getData() {
        return data;
    }

    public Friendship getOldData() {
        return oldData;
    }
}
