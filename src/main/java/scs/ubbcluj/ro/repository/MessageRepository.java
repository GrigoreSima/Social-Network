package scs.ubbcluj.ro.repository;

import scs.ubbcluj.ro.domain.Message;
import scs.ubbcluj.ro.domain.User;

import java.util.List;
import java.util.Optional;

public abstract class MessageRepository implements Repository<Long, Message>{

    @Override
    public abstract Optional<Message> findOne(Long aLong);

//    public abstract Optional<Long> findGroup(List<Long> users);
    public abstract Optional<Long> findGroup(Long userID1, Long userID2);
    @Override
    public abstract Iterable<Message> findAll();

    public abstract Iterable<Message> findAllInGroup(Long groupID);

    @Override
    public abstract Optional<Message> save(Message entity);

    @Override
    public abstract Optional<Message> delete(Long aLong);

    @Override
    public abstract Optional<Message> update(Message entity);
}
