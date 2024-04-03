package scs.ubbcluj.ro.service;

import scs.ubbcluj.ro.domain.Message;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.MessageRepository;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.utils.event.ChangeEventType;
import scs.ubbcluj.ro.utils.event.MessageChangeEvent;
import scs.ubbcluj.ro.utils.observer.Observable;
import scs.ubbcluj.ro.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageService implements Observable<MessageChangeEvent> {
    private MessageRepository repository;
    private Repository<Long, User> userRepository;

    public MessageService(MessageRepository repository, Repository<Long, User> userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public void addMessage(String groupid, String from, List<String> to, String message, String reply)
    {
        if(!from.strip().matches("[0-9]+") || !groupid.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return;
        }

        if(reply == null) reply = "-1";

        List<User> users = new ArrayList<>();
        for (String s : to) {
            if(!s.strip().matches("[0-9]+"))
            {
                System.out.println("ID is not valid");
                return;
            }

            users.add(userRepository.findOne(Long.parseLong(s)).orElse(null));
        }

        User fromUser = userRepository.findOne(Long.parseLong(from)).orElse(null);
        Message msg = new Message(999L, fromUser,
                users, message, LocalDateTime.now(), repository.findOne(Long.parseLong(reply)).orElse(null));
        msg.setGroupID(Long.parseLong(groupid));
        repository.save(msg);
        notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, msg));
    }

    public void removeMessage(String msgID)
    {
        if(!msgID.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return;
        }

        Long longMsgID = Long.parseLong(msgID);

        var msg = repository.findOne(longMsgID).orElse(null);
        repository.delete(longMsgID);
        notifyObservers(new MessageChangeEvent(ChangeEventType.DELETE, msg));
    }

    public void updateMessage(String msgID, String message)
    {
        if(!msgID.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return;
        }

        Long longMsgID = Long.parseLong(msgID);

        var msg = repository.findOne(longMsgID);

        msg.ifPresent(o -> o.setMessage(message));
        msg.ifPresent(o -> repository.update(o));
        msg.ifPresent(o -> notifyObservers(new MessageChangeEvent(ChangeEventType.UPDATE, msg.get())));
    }

    public List<Message> getAllMessages() {
        List<Message> list = new ArrayList<>();
        repository.findAll().forEach(list::add);
        return list;
    }

    public List<Message> getAllMessagesInGroup(Long groupID) {
        List<Message> list = new ArrayList<>();
        repository.findAllInGroup(groupID).forEach(list::add);
        return list;
    }

    public Optional<Long> getGroupID(String userID1, String userID2)
    {
        if(!userID1.strip().matches("[0-9]+") || !userID2.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return Optional.empty();
        }

        return repository.findGroup(Long.parseLong(userID1), Long.parseLong(userID2));
    }

    private final List<Observer<MessageChangeEvent>> observers = new ArrayList<>();
    @Override
    public void addObserver(Observer<MessageChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }

}
