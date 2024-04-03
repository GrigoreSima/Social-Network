package scs.ubbcluj.ro.ui;

import scs.ubbcluj.ro.service.Service;
import scs.ubbcluj.ro.utils.validators.InputException;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class UI {
     Service service;

    public UI(Service service) {
        this.service = service;
    }

    /**
     * @param line the command
     * @throws InputException if command is exit
     */
    private void parser(String line) throws InputException
    {
        var command = line;

        if(line.equals("exit")) throw new InputException("Stopping the program...");
        line += " ";

        command = line.substring(0, line.indexOf(" "));


        switch (command.toLowerCase())
        {
            case "adduser":
                service.getUserService().addUser(line.substring(line.indexOf(" ")+1));
                break;

            case "removeuser":
                service.getUserService().removeUser(line.split(" ")[1]);
                break;

            case "showfriends":
                printList(service.getUserService().showFriends(line.split(" ")[1]));
                break;

            case "showfriendsfrom":
                printList(service.getFriendshipService().showFriendsFrom(line.split(" ")[1], service.getUserService().showFriends(line.split(" ")[1]), line.split(" ")[2]));
                break;

            case "addfriend":
                service.getFriendshipService().addFriend(line.split(" ")[1], line.split(" ")[2]);
                break;

            case "removefriend":
                service.getFriendshipService().removeFriend(line.split(" ")[1], line.split(" ")[2]);
                break;

//            case "communities":
//                System.out.println("The number of communities is: " + service.getFriendshipService().getNoOfCommunities());
//                break;
//
//            case "biggest_community":
//                System.out.println("The biggest community is: ");
//                printList(service.getFriendshipService().biggestCommunity());
//                break;

            case "list":
                printList(service.getUserService().getAllUsers());
                break;

            default:
                throw new InputException("The command is not valid !");
        }

    }

    /**
     * @param list Prints every element in the list
     */
    private void printList(List<?> list)
    {
        list.forEach(System.out::println);

        if(list.isEmpty()) System.out.println("The list of users is empty !");
    }

    /**
     * Prints the menu
     */
    private void menu()
    {
        System.out.println("\tCommands: ");
        System.out.println("\t\taddUser [NAME]");
        System.out.println("\t\tremoveUser [USERID]");
        System.out.println("\t\tshowFriends [USERID]");
        System.out.println("\t\tshowFriendsFrom [USERID] [MONTH]");
        System.out.println("\t\taddFriend [USERID1] [USERID2]");
        System.out.println("\t\tremoveFriend [USERID1] [USERID2]");
//        System.out.println("\t\tcommunities");
//        System.out.println("\t\tbiggest_community");
        System.out.println("\t\texit");
    }

    /**
     * Starts the UI
     */
    public void start()
    {
        var scanner = new Scanner(System.in);
        while(true)
        {
            menu();
            try
            {
                parser(scanner.nextLine());
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                if(Objects.equals(e.getMessage(), "Stopping the program..."))
                    break;
            }

        }
        scanner.close();
    }

}
