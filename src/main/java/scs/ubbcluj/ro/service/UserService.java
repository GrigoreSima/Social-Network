package scs.ubbcluj.ro.service;

import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.Paging.PageableImplementation;
import scs.ubbcluj.ro.repository.Paging.PagingRepository;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.utils.event.ChangeEventType;
import scs.ubbcluj.ro.utils.event.UsersChangeEvent;
import scs.ubbcluj.ro.utils.observer.Observable;
import scs.ubbcluj.ro.utils.observer.Observer;
import scs.ubbcluj.ro.utils.validators.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UserService implements Observable<UsersChangeEvent> {
    private PagingRepository<Long, User> repository;


    public UserService(PagingRepository<Long, User> repository) {
        this.repository = repository;
    }


    /**
     * Adds a user to the repository if the user does not exist
     * @param name name of the user
     */
    public void addUser(String name)
    {
        try {
            var user = new User(name);
            repository.save(user);
            notifyObservers(new UsersChangeEvent(ChangeEventType.ADD, user));
        }
        catch (ValidationException exception)
        {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Removes a user to the repository
     * @param id id of the user
     */
    public void removeUser(String id)
    {

        if(!id.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return;
        }

        Long long_id = Long.parseLong(id);

//        repository.findAll().forEach(x ->
//        {
//            var obj = repository.findOne(long_id);
//            if(obj.isPresent() && x.getFriends().contains(obj.get()))
//                x.removeFriend(obj.get());
//        });

        var usr = repository.findOne(long_id).orElse(null);

        repository.delete(long_id);

        notifyObservers(new UsersChangeEvent(ChangeEventType.DELETE, usr));
    }

//    public void updateUserID(String id, String new_id)
//    {
//        if(!id.strip().matches("[0-9]+") && !new_id.strip().matches("[0-9]+"))
//        {
//            System.out.println("ID is not valid");
//            return;
//        }
//
//        Long long_id = Long.parseLong(id);
//        var usr = repository.findOne(long_id);
//        usr.ifPresent(user -> user.setID(Long.parseLong(new_id)));
//    }

    public void updateUserName(String id, String name)
    {
        if(!id.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return;
        }

        Long long_id = Long.parseLong(id);
        var usr = repository.findOne(long_id);
        usr.ifPresent(user -> user.setName(name));
        usr.ifPresent(user -> repository.update(user));

        usr.ifPresent(user -> notifyObservers(new UsersChangeEvent(ChangeEventType.UPDATE, usr.get())));
    }

    /**
     * Returns the list of friends for a user
     * @param id id of the user
     * @return list of user's friends
     */
    public List<User> showFriends(String id)
    {
        if(!id.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return new ArrayList<>();
        }

        var user = repository.findOne(Long.parseLong(id));
        return (user.map(User::getFriends).orElse(null));
    }

    public Optional<User> getUser(String id)
    {
        if(!id.strip().matches("[0-9]+"))
        {
            System.out.println("ID is not valid");
            return Optional.empty();
        }

        var user = repository.findOne(Long.parseLong(id));
        return user;
    }
    public List<User> getAllUsers()
    {
        var list = new ArrayList<User>();
        repository.findAll().forEach(list::add);
        return list;
    }

    public List<User> getAllUsers(String pageNo, String pageSize)
    {
        var list = new ArrayList<User>();
        repository.findAll(new PageableImplementation(Integer.parseInt(pageNo) - 1, Integer.parseInt(pageSize))).getContent().forEach(list::add);
        return list;
    }


    private List<Observer<UsersChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<UsersChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UsersChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UsersChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }
}
