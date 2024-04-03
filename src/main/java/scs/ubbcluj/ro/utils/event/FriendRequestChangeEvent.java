package scs.ubbcluj.ro.utils.event;

import scs.ubbcluj.ro.domain.FriendRequest;
import scs.ubbcluj.ro.domain.Message;

public class FriendRequestChangeEvent implements Event{

    private final ChangeEventType type;
    private final FriendRequest data;
    private FriendRequest oldData;

    public FriendRequestChangeEvent(ChangeEventType type, FriendRequest data) {
        this.type = type;
        this.data = data;
    }

    public FriendRequestChangeEvent(ChangeEventType type, FriendRequest data, FriendRequest oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public FriendRequest getData() {
        return data;
    }

    public FriendRequest getOldData() {
        return oldData;
    }
}
