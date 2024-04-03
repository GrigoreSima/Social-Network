package scs.ubbcluj.ro.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import scs.ubbcluj.ro.domain.Message;
import scs.ubbcluj.ro.domain.MessageGroup;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.service.MessageGroupService;
import scs.ubbcluj.ro.service.MessageService;
import scs.ubbcluj.ro.utils.event.MessageChangeEvent;
import scs.ubbcluj.ro.utils.observer.Observer;

import java.util.List;


public class ChatController implements Observer<MessageChangeEvent> {
    MessageService service;
    MessageGroupService messageGroupService;
    User user;

    @FXML
    private TitledPane titledPane;
    @FXML
    private GridPane gridPane;

    public void setUser(User user) {
        this.user = user;
    }

    public void setService(MessageService service) {
        this.service = service;
        service.addObserver(this);
    }

    public void setMessageGroupService(MessageGroupService messageGroupService) {
        this.messageGroupService = messageGroupService;
    }

    public void init()
    {
        loadChat();
    }

    private void loadChat()
    {
        gridPane.getChildren().clear();
        List<Message> messages = service.getAllMessagesInGroup(2L);
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            Label label = new Label(msg.getMessage());
            label.setWrapText(true);
            if(msg.getFrom().equals(this.user))
                gridPane.add(label, 1, i);
            else
                gridPane.add(label, 0, i);
        }

        var group = messageGroupService.getMessageGroup(messages.get(0).getGroupID().toString());
        if(group.isPresent() && !group.get().getGroupName().isEmpty()) titledPane.setText(group.get().getGroupName());
        else titledPane.setText(messages.get(0).getTo().get(0).nameProperty().getValue());
    }


    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        loadChat();
    }
}
