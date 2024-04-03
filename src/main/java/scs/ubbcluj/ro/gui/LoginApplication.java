package scs.ubbcluj.ro.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scs.ubbcluj.ro.controller.ChatListController;
import scs.ubbcluj.ro.controller.LoginController;
import scs.ubbcluj.ro.controller.RegisterController;
import scs.ubbcluj.ro.controller.UsersController;
import scs.ubbcluj.ro.domain.Friendship;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.DB.FriendRequestDBRepository;
import scs.ubbcluj.ro.repository.DB.FriendshipDBRepository;
import scs.ubbcluj.ro.repository.DB.UserDBLoginRepository;
import scs.ubbcluj.ro.repository.DB.UserDBPagingRepository;
import scs.ubbcluj.ro.repository.FriendRequestRepository;
import scs.ubbcluj.ro.repository.Paging.PagingRepository;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.service.*;
import scs.ubbcluj.ro.utils.Tuple;
import scs.ubbcluj.ro.utils.event.AfterLoginEvent;
import scs.ubbcluj.ro.utils.event.RegisterEvent;
import scs.ubbcluj.ro.utils.validators.UserValidator;

import java.io.IOException;

public class LoginApplication extends Application {

    private LoginService service;
    private EventHandler<AfterLoginEvent> AfterLoginEvent;
    private EventHandler<RegisterEvent> toRegisterEvent;


    private PagingRepository<Long, User> userRepository;
    private UserDBLoginRepository userLoginRepository;
    private UserService userService;

    private RegisterService registerService;


    User user = new User("");
    private FriendRequestRepository friendRequestRepository;
    private FriendRequestService friendRequestService;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private FriendshipService friendshipService;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        userRepository = new UserDBPagingRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password",
                new UserValidator());

        userLoginRepository = new UserDBLoginRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password",
                new UserValidator());

        userService = new UserService(userRepository);

        friendRequestRepository = new FriendRequestDBRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password");
        friendRequestService = new FriendRequestService(friendRequestRepository, userRepository);


        friendshipRepository = new FriendshipDBRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password");
        friendshipService = new FriendshipService(friendshipRepository);

        AfterLoginEvent = event -> {
            try {
                afterLogin(primaryStage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        toRegisterEvent = event -> {
            try {
                toRegister(primaryStage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        service = new LoginService(userLoginRepository);
        registerService = new RegisterService(userLoginRepository);


        initView(primaryStage);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("views/login-view.fxml"));
        AnchorPane loginLayout = loginLoader.load();
        primaryStage.setScene(new Scene(loginLayout));

        LoginController loginController = loginLoader.getController();
        loginController.setService(service);
        loginController.setAfterLoginventHandler(AfterLoginEvent);
        loginController.setRegisterEventHandler(toRegisterEvent);
        loginController.setUser(user);
    }

    private void afterLogin(Stage primaryStage) throws IOException {
        FXMLLoader chatListLoader = new FXMLLoader();
        chatListLoader.setLocation(getClass().getResource("views/chatlist-view.fxml"));
        AnchorPane chatListLayout = chatListLoader.load();
        primaryStage.setScene(new Scene(chatListLayout));

        ChatListController chatlistController = chatListLoader.getController();
        chatlistController.setService(friendRequestService);
        chatlistController.setFriendshipService(this.friendshipService);
        chatlistController.setUserService(userService);
        chatlistController.setUser(user);

        primaryStage.setTitle("User: " + user.nameProperty().getValue());
    }

    private void toRegister(Stage primaryStage) throws IOException {
        FXMLLoader registerLoader = new FXMLLoader();
        registerLoader.setLocation(getClass().getResource("views/register-view.fxml"));
        AnchorPane registerLayout = registerLoader.load();
        primaryStage.setScene(new Scene(registerLayout));
        primaryStage.setTitle("Register");


        RegisterController registerController = registerLoader.getController();
        registerController.setService(registerService);
        registerController.setEventHandler(AfterLoginEvent);
        registerController.setUser(user);
    }
}
