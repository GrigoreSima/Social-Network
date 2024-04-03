package scs.ubbcluj.ro.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.service.LoginService;
import scs.ubbcluj.ro.utils.event.AfterLoginEvent;
import scs.ubbcluj.ro.utils.event.RegisterEvent;

import java.util.Arrays;


public class LoginController {
    private LoginService service;
    private EventHandler<?> registerEventHandler, afterLoginEventHandler;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label labelError;

    private User user;


    public void setService(LoginService service) {
        this.service = service;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAfterLoginventHandler(EventHandler<?> eventHandler) {
        this.afterLoginEventHandler = eventHandler;
    }
    public void setRegisterEventHandler(EventHandler<?> eventHandler) {
        this.registerEventHandler = eventHandler;
    }

    @FXML
    private void handleLogin(ActionEvent actionEvent)
    {
        labelError.setVisible(false);
        if(username.getText().isEmpty() || password.getText().isEmpty())
            return;

        if(!service.login(username.getText(), password.getText()))
        {
            labelError.setVisible(true);
            return;
        }

        User found = service.getOne(username.getText(), password.getText());
        user.setID(found.getID());
        user.setName(found.nameProperty().getValue());
        afterLoginEventHandler.handle(null);
    }

    @FXML
    private void handleRegister(MouseEvent mouseEvent) {
        registerEventHandler.handle(null);
    }
}
