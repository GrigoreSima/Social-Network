package scs.ubbcluj.ro.service;

public class Service {

    private UserService userService;
    private FriendshipService friendshipService;

    public Service(UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    public UserService getUserService() {
        return userService;
    }

    public FriendshipService getFriendshipService() {
        return friendshipService;
    }
}
