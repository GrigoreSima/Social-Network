package scs.ubbcluj.ro.domain;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {

    private ID id;

    public Entity(ID id) {
        this.id = id;
    }

    /**
     * ID Getter for Entity
     * @return the id of the object
     */
    public ID getID() {
        return id;
    }

    /**
     * ID Setter for Entity
     * @param id id to set to the object
     */
    public void setID(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
