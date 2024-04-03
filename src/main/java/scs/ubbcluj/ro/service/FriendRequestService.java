package scs.ubbcluj.ro.service;

import scs.ubbcluj.ro.domain.FriendRequest;
import scs.ubbcluj.ro.domain.Message;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.FriendRequestRepository;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.utils.FriendshipRequestStatus;
import scs.ubbcluj.ro.utils.Tuple;
import scs.ubbcluj.ro.utils.event.ChangeEventType;
import scs.ubbcluj.ro.utils.event.FriendRequestChangeEvent;
import scs.ubbcluj.ro.utils.observer.Observable;
import scs.ubbcluj.ro.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestService implements Observable<FriendRequestChangeEvent> {

    FriendRequestRepository repository;
    Repository<Long, User> userRepository;

    public FriendRequestService(FriendRequestRepository repository, Repository<Long, User> userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public void addFriendRequest(String id1, String id2) {
        Result result = getResult(id1, id2);
        if (result == null) return;

        User from = userRepository.findOne(result.tuple().getLeft()).orElse(null);
        User to = userRepository.findOne(result.tuple().getRight()).orElse(null);

        if (from == null || to == null) return;

        var friendRequest = new FriendRequest(from, to, LocalDateTime.now(), FriendshipRequestStatus.Pending);
        repository.save(friendRequest);
        notifyObservers(new FriendRequestChangeEvent(ChangeEventType.ADD, friendRequest));
    }

    public void removeFriendRequest(String id1, String id2) {
        Result result = getResult(id1, id2);
        if (result == null) return;
        var friendRequest = repository.findOne(result.tuple);

        if (friendRequest.isEmpty()) return;

        repository.delete(result.tuple);
        notifyObservers(new FriendRequestChangeEvent(ChangeEventType.DELETE, friendRequest.get()));
    }

    public void updateFriendRequest(String id1, String id2, String status) {
        Result result = getResult(id1, id2);
        if (result == null) return;

        User from = userRepository.findOne(result.tuple().getLeft()).orElse(null);
        User to = userRepository.findOne(result.tuple().getRight()).orElse(null);

        if (from == null || to == null) return;

        FriendshipRequestStatus friendshipRequestStatus = switch (status.toLowerCase()) {
            case "approved" -> FriendshipRequestStatus.Approved;
            case "rejected" -> FriendshipRequestStatus.Rejected;
            default -> FriendshipRequestStatus.Pending;
        };

        var friendRequest = new FriendRequest(from, to, null, friendshipRequestStatus);
        repository.update(friendRequest);
        notifyObservers(new FriendRequestChangeEvent(ChangeEventType.UPDATE, friendRequest));
    }

    public List<FriendRequest> findUserFriendRequests(String id) {
        if (!id.strip().matches("[0-9]+")) {
            System.out.println("ID is not valid");
            return null;
        }

        List<FriendRequest> list = new ArrayList<>();
        repository.findForOne(Long.parseLong(id)).forEach(list::add);
        return list;
    }

    public List<FriendRequest> findAll() {
        List<FriendRequest> list = new ArrayList<>();
        repository.findAll().forEach(list::add);
        return list;
    }

    private Result getResult(String id1, String id2) {
        if (!id1.strip().matches("[0-9]+")) {
            System.out.println("ID1 is not valid");
            return null;
        }

        if (!id2.strip().matches("[0-9]+")) {
            System.out.println("ID2 is not valid");
            return null;
        }

        return new Result(new Tuple<>(Long.parseLong(id1), Long.parseLong(id2)));
    }

    private final List<Observer<FriendRequestChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<FriendRequestChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendRequestChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendRequestChangeEvent t) {
        observers.forEach(o -> o.update(t));
    }

    private record Result(Tuple<Long, Long> tuple) {
    }
}
