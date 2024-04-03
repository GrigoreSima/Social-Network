package scs.ubbcluj.ro.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User extends Entity<Long>{

    private static Long entities_no = 0L;
    private List<String> name;
    private final List<User> friends;

    public User(String name) {
        super(++entities_no);
        this.name = nameParser(name);
        this.friends = new ArrayList<>(5);
    }

    public User(Long id, String name) {
        super(id);
        ++entities_no;
        this.name = nameParser(name);
        this.friends = new ArrayList<>(5);
    }

    /**
     * @param name the name and forename for the user (separated by spaces)
     * @return List of name + forename
     */
    public List<String> nameParser(String name)
    {
        return Arrays.stream(name.split(" ")).toList();
    }

    /**
     * @return List of name + forename
     */
    // getters and setters
    public List<String> getName() {
        return name;
    }

    public StringProperty idProperty() {
        StringProperty property = new SimpleStringProperty();
        property.setValue(String.valueOf(getID()));
        return property;
    }

    public StringProperty nameProperty() {
        StringProperty property = new SimpleStringProperty();
        property.setValue(String.join(" ", name));
        return property;
    }

    /**
     * @param name the name and forename for the user (separated by spaces)
     */
    public void setName(String name) {
        this.name = nameParser(name);
    }

    /**
     * @return List of friends of the user
     */
    public List<User> getFriends() {
        return friends;
    }


    /**
     * @param friend user to add as a friend
     */
    public void addFriend(User friend)
    {
        if(friend == null) return;

        if(!friends.contains(friend))
            friends.add(friend);
    }

    /**
     * @param friend user to remove from friends
     */
    public void removeFriend(User friend)
    {
        friends.remove(friend);
    }

    //others
    @Override
    public String toString() {
        return "[id]: " + getID() + " | " +
        "[name]: " + String.join(" ", name);
    }
}
