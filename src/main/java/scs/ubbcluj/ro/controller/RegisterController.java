package scs.ubbcluj.ro.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.service.RegisterService;
import scs.ubbcluj.ro.utils.event.AfterLoginEvent;


public class RegisterController {
    private RegisterService service;
    private EventHandler<AfterLoginEvent> eventHandler;

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextField name;

    private User user;

    public void setService(RegisterService service) {
        this.service = service;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEventHandler(EventHandler<AfterLoginEvent> eventHandler) {
        this.eventHandler = eventHandler;
    }

    @FXML
    private void handleRegister(ActionEvent actionEvent) {
        service.register(username.getText(), password.getText(), name.getText());
        User found = service.getOne(username.getText(), password.getText());
        user.setID(found.getID());
        user.setName(found.nameProperty().getValue());
        eventHandler.handle(null);
    }
}
