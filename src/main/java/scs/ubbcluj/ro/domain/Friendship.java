package scs.ubbcluj.ro.domain;

import scs.ubbcluj.ro.utils.Tuple;

import java.time.LocalDateTime;

public class Friendship extends Entity<Tuple<Long, Long>> {

    LocalDateTime datetime;

    public Friendship(Tuple<Long, Long> longLongTuple, LocalDateTime datetime) {
        super(longLongTuple);
        this.datetime = datetime;
    }

    /**
     * Getter for datetime attribute
     * @return the datetime when the friendship was created
     */
    public LocalDateTime getDatetime() {
        return datetime;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                super.getID().getLeft().toString() +
                " - " +
                super.getID().getRight().toString() +
                " " +
                "datetime=" + datetime +
                '}';
    }
}
