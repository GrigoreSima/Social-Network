package scs.ubbcluj.ro.repository;

import scs.ubbcluj.ro.domain.FriendRequest;
import scs.ubbcluj.ro.utils.Tuple;

import java.util.Optional;

public abstract class FriendRequestRepository implements Repository<Tuple<Long, Long>, FriendRequest>{
    @Override
    public abstract Optional<FriendRequest> findOne(Tuple<Long, Long> tuple);

    public abstract Iterable<FriendRequest> findForOne(Long id);

    @Override
    public abstract Iterable<FriendRequest> findAll();

    @Override
    public abstract Optional<FriendRequest> save(FriendRequest entity);

    @Override
    public abstract Optional<FriendRequest> delete(Tuple<Long, Long> tuple);

    @Override
    public abstract Optional<FriendRequest> update(FriendRequest entity);
}
