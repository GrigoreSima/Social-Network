package scs.ubbcluj.ro.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import scs.ubbcluj.ro.domain.Message;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.service.MessageService;
import scs.ubbcluj.ro.utils.event.MessageChangeEvent;
import scs.ubbcluj.ro.utils.observer.Observer;

import java.util.Optional;

public class MessagesController implements Observer<MessageChangeEvent> {

    MessageService service;
    ObservableList<Message> model = FXCollections.observableArrayList();

    @FXML
    private TableView<Message> tableView;
    @FXML
    private TableColumn<Message, String> tableColumnMessageID;
    @FXML
    private TableColumn<User, String> tableColumnFrom;
    @FXML
    private TableColumn<Message, String> tableColumnMessage;
    @FXML
    private TableColumn<Message, String> tableColumnDate;
    @FXML
    private TextField userID1;
    @FXML
    private TextField userID2;

    public void setService(MessageService service) {
        this.service = service;
        service.addObserver(this);
        model.setAll(service.getAllMessages());
        tableView.setEditable(true);
    }

    @FXML
    public void initialize()
    {
        tableColumnMessageID.setCellValueFactory(new PropertyValueFactory<>("id"));

        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<>("from"));

        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        tableColumnMessage.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumnMessage.setOnEditCommit(event -> service.updateMessage(
                event.getTableView().getItems().get(event.getTablePosition().getRow()).getID().toString(),
                event.getNewValue()));

        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableView.setItems(model);
    }


    public void handleShowGroupMessages(KeyEvent actionEvent) {

        if(userID1.getText().isEmpty() || userID2.getText().isEmpty()) {
            model.setAll(service.getAllMessages());
            return;
        }

        Optional<Long> groupID = service.getGroupID(userID1.getText(), userID2.getText());
        groupID.ifPresent(grID -> model.setAll(service.getAllMessagesInGroup(grID)));
        if(groupID.isEmpty()) model.clear();

    }

    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        tableView.refresh();
    }

}
