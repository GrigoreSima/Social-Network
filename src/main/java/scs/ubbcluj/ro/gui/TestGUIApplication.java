package scs.ubbcluj.ro.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scs.ubbcluj.ro.controller.ChatListController;
import scs.ubbcluj.ro.domain.Friendship;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.DB.*;
import scs.ubbcluj.ro.repository.FriendRequestRepository;
import scs.ubbcluj.ro.repository.Paging.PagingRepository;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.service.*;
import scs.ubbcluj.ro.utils.Tuple;
import scs.ubbcluj.ro.utils.validators.UserValidator;

import java.io.IOException;

public class TestGUIApplication extends Application {

    User user1;
    User user2;
    private Repository<Long, User> userRepository;
    private UserService userService;
    private FriendRequestRepository repository;
    private FriendRequestService service;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private FriendshipService friendshipService;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        userRepository = new UserDBRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password",
                new UserValidator());

        userService = new UserService((PagingRepository<Long, User>) userRepository);


        repository = new FriendRequestDBRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password");
        service = new FriendRequestService(repository, userRepository);


        friendshipRepository = new FriendshipDBRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password");
        friendshipService = new FriendshipService(friendshipRepository);


        this.user1 = userRepository.findOne(10L).orElse(null);

        if(user1 == null) return;

        this.user2 = userRepository.findOne(5L).orElse(null);

        if(user2 == null) return;

        Stage user1Stage = new Stage();
        initView(user1Stage, user1);

        Stage user2Stage = new Stage();
        initView(user2Stage, user2);
    }

    private void initView(Stage primaryStage, User user) throws IOException {

        FXMLLoader testLoader = new FXMLLoader();
        testLoader.setLocation(getClass().getResource("views/chatlist-view.fxml"));
        AnchorPane testLayout = testLoader.load();
        primaryStage.setScene(new Scene(testLayout));

        ChatListController chatlistController = testLoader.getController();
        chatlistController.setService(this.service);
        chatlistController.setFriendshipService(this.friendshipService);
        chatlistController.setUserService(userService);
        chatlistController.setUser(user);

        primaryStage.setTitle("User: " + user.nameProperty().getValue());
        primaryStage.show();
    }
}
