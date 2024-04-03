package scs.ubbcluj.ro.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scs.ubbcluj.ro.controller.MessagesController;
import scs.ubbcluj.ro.controller.UsersController;
import scs.ubbcluj.ro.domain.Message;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.DB.MessageDBRepository;
import scs.ubbcluj.ro.repository.DB.UserDBRepository;
import scs.ubbcluj.ro.repository.MessageRepository;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.service.MessageService;
import scs.ubbcluj.ro.service.UserService;
import scs.ubbcluj.ro.utils.validators.UserValidator;

import java.io.IOException;

public class MessageApplication extends Application {

    private Repository<Long, User> userRepository;
    private MessageRepository messageRepository;
    private MessageService messageService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        userRepository = new UserDBRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password",
                new UserValidator());

        messageRepository = new MessageDBRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password");

        messageService = new MessageService(messageRepository, userRepository);

        initView(primaryStage);
        primaryStage.setTitle("Messages");
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader messageLoader = new FXMLLoader();
        messageLoader.setLocation(getClass().getResource("views/messages-view.fxml"));
        AnchorPane messageLayout = messageLoader.load();
        primaryStage.setScene(new Scene(messageLayout));

        MessagesController messagesController = messageLoader.getController();
        messagesController.setService(messageService);
    }
}
