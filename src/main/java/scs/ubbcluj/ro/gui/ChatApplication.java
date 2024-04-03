package scs.ubbcluj.ro.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scs.ubbcluj.ro.controller.ChatController;
import scs.ubbcluj.ro.domain.MessageGroup;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.DB.MessageDBRepository;
import scs.ubbcluj.ro.repository.DB.MessageGroupDBRepository;
import scs.ubbcluj.ro.repository.DB.UserDBRepository;
import scs.ubbcluj.ro.repository.MessageRepository;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.service.MessageGroupService;
import scs.ubbcluj.ro.service.MessageService;
import scs.ubbcluj.ro.utils.validators.UserValidator;

import java.io.IOException;

public class ChatApplication extends Application {

    User user;
    private Repository<Long, User> userRepository;
    private MessageRepository repository;
    private Repository<Long, MessageGroup> messageGroupRepository;

    private MessageService service;
    private MessageGroupService messageGroupService;


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

        repository = new MessageDBRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password");

        messageGroupRepository = new MessageGroupDBRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password");

        service = new MessageService(repository, userRepository);
        messageGroupService = new MessageGroupService(messageGroupRepository);

        this.user = userRepository.findOne(1L).orElse(null);

        if(user == null) return;

        initView(primaryStage);
        primaryStage.setTitle("User: " + user.nameProperty().getValue());
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader chatLoader = new FXMLLoader();
        chatLoader.setLocation(getClass().getResource("views/chat-view.fxml"));
        AnchorPane chatLayout = chatLoader.load();
        primaryStage.setScene(new Scene(chatLayout));

        ChatController chatController = chatLoader.getController();
        chatController.setService(this.service);
        chatController.setUser(this.user);
        chatController.setMessageGroupService(this.messageGroupService);

        chatController.init();
    }
}
