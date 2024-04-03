package scs.ubbcluj.ro.domain;

import java.util.Objects;

public class MessageGroup extends Entity<Long>{
    String groupName;

    public MessageGroup(Long aLong, String groupName) {
        super(aLong);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "MessageGroup{" +
                "groupName='" + groupName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MessageGroup that = (MessageGroup) o;
        return Objects.equals(getGroupName(), that.getGroupName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGroupName());
    }
}
