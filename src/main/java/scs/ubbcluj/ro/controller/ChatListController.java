package scs.ubbcluj.ro.controller;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import scs.ubbcluj.ro.domain.FriendRequest;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.service.FriendRequestService;
import scs.ubbcluj.ro.service.FriendshipService;
import scs.ubbcluj.ro.service.UserService;
import scs.ubbcluj.ro.utils.event.FriendRequestChangeEvent;
import scs.ubbcluj.ro.utils.observer.Observer;

import java.util.Objects;

public class ChatListController implements Observer<FriendRequestChangeEvent> {
    private FriendRequestService service;
    private FriendshipService friendshipService;
    private UserService userService;

    private User user;

    private ObservableList<User> users = FXCollections.observableArrayList();
    @FXML
    private ListView<User> listViewOthers;
    private ObservableList<User> friends = FXCollections.observableArrayList();
    @FXML
    private ListView<User> listViewFriends;
    private ObservableList<FriendRequest> friendRequests = FXCollections.observableArrayList();
    @FXML
    private ListView<FriendRequest> listViewFriendRequests;

    private ListView<?> inUseListView;

    public void setService(FriendRequestService service) {
        this.service = service;
        service.addObserver(this);
        this.update(null);
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
        this.update(null);
    }

    public void setUser(User user) {
        this.user = user;
        this.update(null);
    }

    @FXML
    public void initialize() {
        initListViewFriendRequests();
        initListViewFriends();
        initListViewOthers();
    }

    private void initListViewOthers() {
        listViewOthers.setItems(users);

        ContextMenu menu = new ContextMenu();
        MenuItem addFriend = new MenuItem("Be friends :)");
        addFriend.setOnAction(event -> {
            try {
                inUseListView = listViewOthers;

                service.addFriendRequest(user.getID().toString(),
                        listViewOthers.getFocusModel().getFocusedItem()
                                .getID().toString());
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        });

        menu.getItems().setAll(addFriend);

        listViewOthers.setContextMenu(menu);
        listViewOthers.setCellFactory(new Callback<>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            setText(item.nameProperty().getValue());
                            setDisabled(false);
                        } else {
                            setText("");
                            setDisabled(true);
                        }
                    }
                };
            }
        });
    }

    private void initListViewFriends() {
        listViewFriends.setItems(friends);

        ContextMenu menuFriends = new ContextMenu();
        MenuItem removeFriend = new MenuItem("Don't be friends anymore :(");
        removeFriend.setOnAction(event -> {
            try {
                inUseListView = listViewFriends;

                friendshipService.removeFriend(user.getID().toString(),
                        listViewFriends.getFocusModel().getFocusedItem()
                                .getID().toString());

                service.removeFriendRequest(listViewFriends.getFocusModel().getFocusedItem()
                        .getID().toString(), user.getID().toString());

                service.removeFriendRequest(user.getID().toString(), listViewFriends.getFocusModel().getFocusedItem()
                        .getID().toString());

            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        });

        menuFriends.getItems().setAll(removeFriend);

        listViewFriends.setContextMenu(menuFriends);
        listViewFriends.setCellFactory(new Callback<>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            setText(item.nameProperty().getValue());
                            setDisabled(false);
                        } else {
                            setText("");
                            setDisabled(true);
                        }
                    }
                };
            }
        });
    }

    private void initListViewFriendRequests() {
        listViewFriendRequests.setItems(friendRequests);

        ContextMenu menu = new ContextMenu();
        MenuItem approveItem = new MenuItem("Be friends :)");
        approveItem.setOnAction(event -> {
            inUseListView = listViewFriendRequests;

            try {
                friendshipService.addFriend(user.getID().toString(),
                        listViewFriendRequests.getFocusModel().getFocusedItem()
                                .getID().getLeft().toString());

                service.updateFriendRequest(listViewFriendRequests.getFocusModel().getFocusedItem()
                        .getID().getLeft().toString(), user.getID().toString(), "Approved");

            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        });

        MenuItem rejectItem = new MenuItem("Don't be friends :(");
        rejectItem.setOnAction(event -> {
            inUseListView = listViewFriendRequests;
            try {
                service.removeFriendRequest(listViewFriendRequests.getFocusModel().getFocusedItem()
                        .getID().getLeft().toString(), user.getID().toString());
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        });

        menu.getItems().setAll(approveItem, rejectItem);

        listViewFriendRequests.setContextMenu(menu);
        listViewFriendRequests.setCellFactory(new Callback<>() {
            @Override
            public ListCell<FriendRequest> call(ListView<FriendRequest> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(FriendRequest item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            setText(item.getFrom().nameProperty().getValue());
                            setDisabled(false);
                        } else {
                            setText("");
                            setDisabled(true);
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void changeToOthers(Event event) {
        if (userService == null || user == null) return;

        inUseListView = listViewOthers;
        listViewOthers.getItems().clear();
        users.setAll(userService.getAllUsers()
                .stream()
                .filter(usr ->
                        !Objects.equals(usr.getID(), user.getID()) && !user.getFriends().contains(usr))
                .toList());
        inUseListView.refresh();
    }

    @FXML
    private void changeToFriendsTab(Event event) {
        if (user == null) return;

        inUseListView = listViewFriends;
        listViewFriends.getItems().clear();
        friends.setAll(user.getFriends());
        inUseListView.refresh();
    }

    @FXML
    private void changeToFriendRequestTab(Event event) {
        if (service == null) return;

        inUseListView = listViewFriendRequests;
        listViewFriendRequests.getItems().clear();
        friendRequests.setAll(
                service.findUserFriendRequests(user.getID().toString()));
        inUseListView.refresh();
    }


    @Override
    public void update(FriendRequestChangeEvent friendRequestChangeEvent) {

        if (userService != null && user != null)
            user = userService.getUser(user.getID().toString()).orElse(null);

        if (inUseListView == null || inUseListView.equals(listViewOthers)) {
            changeToOthers(new Event(Event.ANY));
        } else if (inUseListView.equals(listViewFriends)) {
            changeToFriendsTab(new Event(Event.ANY));
        } else if (inUseListView.equals(listViewFriendRequests)) {
            changeToFriendRequestTab(new Event(Event.ANY));
        }
    }

}
