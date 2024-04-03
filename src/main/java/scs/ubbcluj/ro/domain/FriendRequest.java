package scs.ubbcluj.ro.domain;

import scs.ubbcluj.ro.utils.FriendshipRequestStatus;
import scs.ubbcluj.ro.utils.Tuple;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendRequest extends Entity<Tuple<Long, Long>>{
    private User from, to;
    private LocalDateTime date;
    private FriendshipRequestStatus status;

    public FriendRequest(User from, User to, LocalDateTime date, FriendshipRequestStatus status) {
        super(new Tuple<>(from.getID(), to.getID()));
        this.from = from;
        this.to = to;
        this.date = date;
        this.status = status;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public FriendshipRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipRequestStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "from=" + from +
                ", to=" + to +
                ", date=" + date +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(getFrom(), that.getFrom()) && Objects.equals(getDate(), that.getDate()) && getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFrom(), getDate(), getStatus());
    }
}
