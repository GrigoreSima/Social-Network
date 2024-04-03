package scs.ubbcluj.ro.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scs.ubbcluj.ro.controller.UsersController;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.DB.UserDBPagingRepository;
import scs.ubbcluj.ro.repository.DB.UserDBRepository;
import scs.ubbcluj.ro.repository.Paging.PagingRepository;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.service.UserService;
import scs.ubbcluj.ro.utils.validators.UserValidator;

import java.io.IOException;

public class UserApplication extends Application {

    private Repository<Long, User> userRepository;
    private UserService userService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        userRepository = new UserDBPagingRepository(
                "jdbc:postgresql://localhost:5432/socialnetwork",
                "grigoresima",
                "password",
                new UserValidator());

        userService = new UserService((PagingRepository<Long, User>) userRepository);

        initView(primaryStage);
        primaryStage.setTitle("CRUD Users");
        primaryStage.setWidth(600);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("views/users-view.fxml"));
        AnchorPane userLayout = userLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        UsersController usersController = userLoader.getController();
        usersController.setService(userService);

    }
}
