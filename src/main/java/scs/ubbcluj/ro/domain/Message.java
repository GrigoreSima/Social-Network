package scs.ubbcluj.ro.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    private Long groupID;
    private final User from;
    private final List<User> to;
    private String message;
    private final LocalDateTime date;
    private final Message reply;

    public Message(Long aLong, User from, List<User> to, String message, LocalDateTime date) {
        super(aLong);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = null;
    }

    public Message(Long aLong, User from, List<User> to, String message, LocalDateTime date, Message reply) {
        super(aLong);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }

    public StringProperty idProperty() {
        StringProperty property = new SimpleStringProperty();
        property.setValue(String.valueOf(getID()));
        return property;
    }
    public StringProperty fromProperty() {
        return getFrom().nameProperty();
    }
    public StringProperty messageProperty() {
        StringProperty property = new SimpleStringProperty();
        property.setValue(getMessage());
        return property;
    }

    public StringProperty dateProperty() {
        StringProperty property = new SimpleStringProperty();
        property.setValue(getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy  hh:mm a")));
        return property;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public User getFrom() {
        return from;
    }

    public List<User> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Message getReply() {
        return reply;
    }

    @Override
    public String toString() {
        return "Message{" +
                "groupID=" + groupID +
                ", from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", reply=" + reply +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Message message1 = (Message) o;
        return Objects.equals(getGroupID(), message1.getGroupID()) && Objects.equals(getFrom(), message1.getFrom()) && Objects.equals(getTo(), message1.getTo()) && Objects.equals(getMessage(), message1.getMessage()) && Objects.equals(getDate(), message1.getDate()) && Objects.equals(getReply(), message1.getReply());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGroupID(), getFrom(), getTo(), getMessage(), getDate(), getReply());
    }
}
