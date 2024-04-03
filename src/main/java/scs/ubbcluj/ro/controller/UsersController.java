package scs.ubbcluj.ro.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.service.UserService;
import scs.ubbcluj.ro.utils.event.UsersChangeEvent;
import scs.ubbcluj.ro.utils.observer.Observer;

public class UsersController implements Observer<UsersChangeEvent> {
    UserService service;
    ObservableList<User> model = FXCollections.observableArrayList();
    ObservableList<String> pageSizeOptions = FXCollections.observableArrayList();

    Integer maxPages;

    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnID;
    @FXML
    TableColumn<User, String> tableColumnName;
    @FXML
    TableColumn<User, Void> tableColumnOption;
    @FXML
    TextField nameInput;

    @FXML
    private Button previousPage;
    @FXML
    private TextField pageNumber;
    @FXML
    private Button nextPage;

    @FXML
    private ComboBox<String> pageSize;


    public void setService(UserService service) {
        this.service = service;
        service.addObserver(this);
        model.setAll(service.getAllUsers("1", "5"));

        int size = service.getAllUsers().size();
        if(size % 5 > 0) maxPages = size / 5 + 1;
        else maxPages = size / 5;

        tableView.setEditable(true);

        pageSizeOptions.addAll("5", "10", "15", "20", "25");
        pageSize.setItems(pageSizeOptions);
        pageSize.getSelectionModel().selectFirst();
    }

    @FXML
    public void initialize()
    {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));

        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnName.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumnName.setOnEditCommit(event -> service.updateUserName(
                event.getTableView().getItems().get(event.getTablePosition().getRow()).getID().toString(),
                event.getNewValue()));

        deleteButtonFactory();
        tableView.setItems(model);
    }

    private void deleteButtonFactory() {
        var deleteBtnFactory = new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {

                return new TableCell<>()
                {
                    private final Button btn = new Button("DELETE");

                    {
                        btn.setOnAction(x -> service.removeUser(
                                String.valueOf(
                                        tableView.getItems().get(getIndex()).getID())
                                )
                        );
                        btn.prefWidthProperty().bind(this.widthProperty());
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setAlignment(Pos.CENTER);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        tableColumnOption.setCellFactory(deleteBtnFactory);
    }

    @FXML
    private void handleAddUser(ActionEvent actionEvent) {
        service.addUser(nameInput.getText());
        nameInput.setText("");
    }

    @FXML
    private void handlePreviousPage(ActionEvent actionEvent) {
        Integer pageNo = Integer.parseInt(pageNumber.getText()) - 1;

        if(pageNo < 1) return;

        pageNumber.setText(pageNo.toString());
        update(null);
    }
    @FXML
    private void handlePageNumber(ActionEvent actionEvent) {
        Integer pageNo = Integer.parseInt(pageNumber.getText());
        if(pageNo < 1 | pageNo > maxPages) return;

        update(null);
    }
    @FXML
    private void handleNextPage(ActionEvent actionEvent) {
        Integer pageNo = Integer.parseInt(pageNumber.getText()) + 1;

        if(pageNo > maxPages) return;

        pageNumber.setText(pageNo.toString());
        update(null);
    }
    @FXML
    private void handlePageSize(ActionEvent actionEvent) {
        maxPages = service.getAllUsers().size() / Integer.parseInt(pageSize.getValue()) + 1;
        pageNumber.setText("1");
        update(null);
    }

    @Override
    public void update(UsersChangeEvent usersChangeEvent) {
        model.clear();
        model.setAll(service.getAllUsers(pageNumber.getText(), pageSize.getValue()));

        int size = service.getAllUsers().size();
        if(size % 5 > 0) maxPages = service.getAllUsers().size() / Integer.parseInt(pageSize.getValue()) + 1;
        else maxPages = service.getAllUsers().size() / Integer.parseInt(pageSize.getValue());

        tableView.setItems(model);
        tableView.refresh();
    }

}
