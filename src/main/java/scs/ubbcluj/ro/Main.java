package scs.ubbcluj.ro;

import scs.ubbcluj.ro.domain.FriendRequest;
import scs.ubbcluj.ro.domain.Friendship;
import scs.ubbcluj.ro.domain.Message;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.DB.FriendRequestDBRepository;
import scs.ubbcluj.ro.repository.DB.FriendshipDBRepository;
import scs.ubbcluj.ro.repository.DB.MessageDBRepository;
import scs.ubbcluj.ro.repository.DB.UserDBRepository;
import scs.ubbcluj.ro.repository.Paging.PagingRepository;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.service.FriendshipService;
import scs.ubbcluj.ro.service.MessageService;
import scs.ubbcluj.ro.service.Service;
import scs.ubbcluj.ro.service.UserService;
import scs.ubbcluj.ro.ui.UI;
import scs.ubbcluj.ro.utils.FriendshipRequestStatus;
import scs.ubbcluj.ro.utils.Tuple;
import scs.ubbcluj.ro.utils.validators.UserValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Main {
    public static void main(String[] args) {

        var userRepository = new UserDBRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password",
                new UserValidator());
//        var friendshipRepository = new FriendshipDBRepository(
//                "jdbc:postgresql://localhost:5432/socialnetwork",
//                "grigoresima",
//                "password",
//                new UserValidator());
//
//        var ui = getUI(userRepository, friendshipRepository);
//        ui.start();

//        var msgRepository = new MessageDBRepository(
//                "jdbc:postgresql://localhost:5432/socialnetwork",
//                "grigoresima",
//                "password");
//
//        MessageService service = new MessageService(msgRepository, userRepository);
//        List<String> list = new ArrayList<>();
//        list.add("10");
//        service.addMessage("123", "5", list, "Testare 1",null);
//        service.removeMessage("123");


    }

    private static UI getUI(PagingRepository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository) {
        var service = new Service(
                new UserService(userRepository),
                new FriendshipService(friendshipRepository)
        );

        return new UI(service);
    }

    private static void initRepo(Repository<Long, User> repository) {
        repository.save(new User("Calita Ana Maria"));
        repository.save(new User("Petru Alin"));
        repository.save(new User("Popa Doru"));
        repository.save(new User("Vancea Doinita"));
        repository.save(new User("Budeanu Roxana"));
        repository.save(new User("Fatu Daniel"));
        repository.save(new User("Parvu Geta"));
        repository.save(new User("Ionescu Octavian"));
        repository.save(new User("Alexandrescu Narcis"));
        repository.save(new User("Marginean Daniel"));
        repository.save(new User("Moga Livia"));
        repository.save(new User("Grigore Nicolae"));
    }
    private static void initService(Service service) {
        service.getFriendshipService().addFriend("1", "2");
        service.getFriendshipService().addFriend("1", "3");
        service.getFriendshipService().addFriend("2", "3");
        service.getFriendshipService().addFriend("1", "5");
        service.getFriendshipService().addFriend("7", "5");
        service.getFriendshipService().addFriend("7", "1");

        service.getFriendshipService().addFriend("12", "11");
        service.getFriendshipService().addFriend("9", "12");
        service.getFriendshipService().addFriend("8", "11");

        service.getFriendshipService().addFriend("6", "10");
    }

}