package scs.ubbcluj.ro.service;

import scs.ubbcluj.ro.domain.Friendship;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.utils.Tuple;
import scs.ubbcluj.ro.utils.event.ChangeEventType;
import scs.ubbcluj.ro.utils.event.FriendshipChangeEvent;
import scs.ubbcluj.ro.utils.observer.Observable;
import scs.ubbcluj.ro.utils.observer.Observer;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class FriendshipService implements Observable<FriendshipChangeEvent> {

    private final Repository<Tuple<Long, Long>, Friendship> repository;

    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> repository) {
        this.repository = repository;
    }


    /**
     * Adds a friendship between the users
     *
     * @param id1 id of the first user
     * @param id2 id of the second user
     */
    public void addFriend(String id1, String id2) {
        Result result = getResult(id1, id2);
        if (result == null) return;
        var friendship = new Friendship(result.tuple, LocalDateTime.now());
        repository.save(friendship);
        notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD, friendship));
    }

    /**
     * Removes a friendship between the users
     *
     * @param id1 id of the first user
     * @param id2 id of the second user
     */
    public void removeFriend(String id1, String id2) {
        Result result = getResult(id1, id2);
        if (result == null) return;

        var friendship = repository.findOne(result.tuple);

        if (friendship.isEmpty()) return;

        repository.delete(result.tuple);
        notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, friendship.get()));
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

    List<Observer<FriendshipChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.forEach(o -> o.update(t));
    }

    private record Result(Tuple<Long, Long> tuple) {
    }


    /**
     * Returns the list of friends for a user
     *
     * @param id id of the user
     * @return list of user's friends
     */
    public List<User> showFriendsFrom(String id, List<User> users, String month) {
        if (!id.strip().matches("[0-9]+") && !month.strip().matches("[0-9]+")) {
            System.out.println("ID or month is not valid");
            return new ArrayList<>();
        }

        if (users.isEmpty()) return users;

        users.removeIf(x ->
        {
            var friendship = repository.findOne(new Tuple<>(Long.parseLong(id), x.getID()));
            return friendship.isEmpty() || !friendship.get().getDatetime().getMonth().equals(Month.of(Integer.parseInt(month)));
        });

        return users;
    }


    /**
     * DFS for a graph of Users
     *
     * @param list    list of tuples<User, Integer> (Graph)
     * @param element starting point for DFS
     */
    private void DFS(List<Tuple<User, Integer>> list, User element) {
        list.get(list.indexOf(new Tuple<>(element, 0))).setRight(1); // visited

        for (var adj : element.getFriends())
            if (list.contains(new Tuple<>(adj, 0)))
                DFS(list, adj);

    }

//    /**
//     * Gets the number of communities in the repository
//     * @return the number of communities in the repository
//     */
//    public int getNoOfCommunities()
//    {
//        var list = new ArrayList<Tuple<User, Integer>>();
//        repository.findAll()
//                .forEach(x -> list.add(new Tuple<>(x, 0)));
//
//        int no = 0;
//        for(var elem : list)
//            if(elem.getRight() == 0)
//            {
//                no++;
//                DFS(list, elem.getLeft());
//            }
//
//        return no;
//    }
//
//    /**
//     * Gets the list of users from the biggest community
//     * @return the list of members of the biggest community
//     */
//    public List<User> biggestCommunity()
//    {
//        var list = new ArrayList<Tuple<User, Integer>>();
//        repository.findAll().forEach(x -> list.add(new Tuple<>(x, 0)));
//
//        var communities = new ArrayList<Tuple<User, Integer>>(3);
//
//        for(var elem : list)
//            if(elem.getRight() == 0)
//            {
//                DFS(list, elem.getLeft());
//
//                int no = 0;
//                for(var element : list)
//                    if(element.getRight() == 1)
//                        no++;
//
//                Integer sum = 0;
//                for(var community : communities)
//                    sum += community.getRight();
//
//                communities.add(new Tuple<>(elem.getLeft(), no - sum));
//            }
//
//        var comparator = new ComparatorTupleUserInt();
//        communities.sort(comparator);
//
//        list.forEach(x -> x.setRight(0));
//
//        DFS(list, communities.getFirst().getLeft());
//
//        var result = new ArrayList<User>();
//        list.stream()
//                .filter(x -> x.getRight() == 1)
//                .forEach(x -> result.add(x.getLeft()));
//
//        return result;
//    }
}
