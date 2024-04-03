package scs.ubbcluj.ro.service;

import scs.ubbcluj.ro.domain.Message;
import scs.ubbcluj.ro.domain.MessageGroup;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.utils.event.ChangeEventType;
import scs.ubbcluj.ro.utils.event.MessageChangeEvent;
import scs.ubbcluj.ro.utils.event.MessageGroupChangeEvent;
import scs.ubbcluj.ro.utils.observer.Observable;
import scs.ubbcluj.ro.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageGroupService implements Observable<MessageGroupChangeEvent>  {
    private Repository<Long, MessageGroup> repository;


    public MessageGroupService(Repository<Long, MessageGroup> repository) {
        this.repository = repository;
        this.observers = new ArrayList<>();
    }

    public void addMessageGroup(String groupName)
    {
        MessageGroup group = new MessageGroup(999L, groupName);
        repository.save(group);
        notifyObservers(new MessageGroupChangeEvent(ChangeEventType.ADD, group));
    }

    public void removeMessageGroup(String groupID)
    {
        if(!groupID.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return;
        }

        Long longGroupID = Long.parseLong(groupID);
        var messageGroup = repository.findOne(longGroupID).orElse(null);
        repository.delete(longGroupID);
        notifyObservers(new MessageGroupChangeEvent(ChangeEventType.DELETE, messageGroup));
    }

    public void updateMessageGroup(String groupID, String groupName)
    {
        if(!groupID.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return;
        }

        Long longGroupID = Long.parseLong(groupID);
        var messageGroup = repository.findOne(longGroupID);
        messageGroup.ifPresent(msg -> msg.setGroupName(groupName));
        messageGroup.ifPresent(msg -> repository.update(msg));
        messageGroup.ifPresent(msg -> notifyObservers(new MessageGroupChangeEvent(ChangeEventType.DELETE, msg)));
    }

    public List<MessageGroup> getAllMessageGroups() {
        List<MessageGroup> list = new ArrayList<>();
        repository.findAll().forEach(list::add);
        return list;
    }

    public Optional<MessageGroup> getMessageGroup(String groupID) {
        if(!groupID.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return Optional.empty();
        }

        return repository.findOne(Long.parseLong(groupID));
    }

    private final List<Observer<MessageGroupChangeEvent>> observers;
    @Override
    public void addObserver(Observer<MessageGroupChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageGroupChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageGroupChangeEvent t) {
        observers.forEach(o -> o.update(t));
    }
}
