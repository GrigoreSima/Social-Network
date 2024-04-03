package scs.ubbcluj.ro.repository.DB;

import scs.ubbcluj.ro.domain.Message;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.MessageRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Types.BIGINT;

public class MessageDBRepository extends MessageRepository {
    private final String url;
    private final String username;
    private final String password;

    public MessageDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<Message> findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null !");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE id = ?");
             PreparedStatement receiverStatement = connection.prepareStatement(
                     """
                        SELECT u.id, u.first_name, u.last_name FROM users_in_groups ug JOIN
                               users u ON ug.userid = u.id WHERE groupid = ?
                        """))
        {
            statement.setLong(1, id);

            // Gets the message
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }

            resultSet.next();

            long groupID = resultSet.getLong("groupid");
            Long userID = resultSet.getLong("from");
            String messageText = resultSet.getString("message");
            LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
            Message reply = this.findOne(resultSet.getLong("reply")).orElse(null);

            // Gets message's receivers
            receiverStatement.setLong(1, groupID);
            resultSet = receiverStatement.executeQuery();

            List<User> receivers = new ArrayList<>();
            User from = null;
            while (resultSet.next())
            {
                Long receiverID = resultSet.getLong("id");
                String receiverFirstName = resultSet.getString("first_name");
                String receiverLastName = resultSet.getString("last_name");

                if(!userID.equals(receiverID)) receivers.add(new User(receiverID, receiverFirstName + " " + receiverLastName));
                else from = new User(receiverID, receiverFirstName + " " + receiverLastName);
            }

            Message message = new Message(id, from, receivers, messageText, date, reply);
            message.setGroupID(groupID);

            return Optional.of(message);
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public Optional<Long> findGroup(List<Long> users) {

        StringBuilder statementText = new StringBuilder("""
                SELECT groupid FROM message_groups mg JOIN users_in_groups ON mg.id = users_in_groups.groupid
                WHERE group_name = '' AND  userid IN (""");

        for (Long user : users) {
            if(!users.get(users.size()-1).equals(user)) statementText.append("?, ");
            else statementText.append("? ");
        }
        statementText.append(") GROUP BY groupid HAVING count(*) = ").append(users.size()).append(" ;");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(statementText.toString()))
        {
            for (int i = 0; i < users.size(); i++) {
                statement.setLong(i+1, users.get(i));
            }

            // Gets the message
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }

            resultSet.next();

            Long groupID = resultSet.getLong("groupid");

            return Optional.of(groupID);
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<Long> findGroup(Long userID1, Long userID2) {
        if (userID1 == null && userID2 == null)
            throw new IllegalArgumentException("ID must be not null !");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     """
                        SELECT groupid FROM message_groups mg JOIN users_in_groups ON mg.id = users_in_groups.groupid
                        WHERE group_name = '' AND  userid IN (?, ?) GROUP BY groupid HAVING count(*) = 2;
                        """))
        {
            statement.setLong(1, userID1);
            statement.setLong(2, userID2);

            // Gets the message
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }

            resultSet.next();

            Long groupID = resultSet.getLong("groupid");

            return Optional.of(groupID);
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Iterable<Message> findAll() {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages ORDER BY date");
             PreparedStatement receiverStatement = connection.prepareStatement(
                     """
                        SELECT u.id, u.first_name, u.last_name FROM users_in_groups ug JOIN
                               users u ON ug.userid = u.id WHERE groupid = ?
                        """))
        {
            // Gets the message
            ResultSet resultSet = statement.executeQuery();
            List<Message> messageList = new ArrayList<>();

            while(resultSet.next())
            {
                Long messageID = resultSet.getLong("id");
                long groupID = resultSet.getLong("groupid");
                Long userID = resultSet.getLong("from");
                String messageText = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Message reply = this.findOne(resultSet.getLong("reply")).orElse(null);

                // Gets message's receivers
                receiverStatement.setLong(1, groupID);
                ResultSet resultSet2 = receiverStatement.executeQuery();

                List<User> receivers = new ArrayList<>();
                User from = null;
                while (resultSet2.next())
                {
                    Long receiverID = resultSet2.getLong("id");
                    String receiverFirstName = resultSet2.getString("first_name");
                    String receiverLastName = resultSet2.getString("last_name");

                    if(!userID.equals(receiverID)) receivers.add(new User(receiverID, receiverFirstName + " " + receiverLastName));
                    else from = new User(receiverID, receiverFirstName + " " + receiverLastName);
                }

                Message message = new Message(messageID, from, receivers, messageText, date, reply);
                message.setGroupID(groupID);
                messageList.add(message);
            }

            return messageList;
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public Iterable<Message> findAllInGroup(Long groupID) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE groupid = ? ORDER BY date");
             PreparedStatement receiverStatement = connection.prepareStatement(
                     """
                        SELECT u.id, u.first_name, u.last_name FROM users_in_groups ug JOIN
                               users u ON ug.userid = u.id WHERE groupid = ?
                        """))
        {
            // Gets the message
            statement.setLong(1, groupID);
            ResultSet resultSet = statement.executeQuery();
            List<Message> messageList = new ArrayList<>();

            while(resultSet.next())
            {
                Long messageID = resultSet.getLong("id");
                Long userID = resultSet.getLong("from");
                String messageText = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Message reply = this.findOne(resultSet.getLong("reply")).orElse(null);

                // Gets message's receivers
                receiverStatement.setLong(1, groupID);
                ResultSet resultSet2 = receiverStatement.executeQuery();

                List<User> receivers = new ArrayList<>();
                User from = null;
                while (resultSet2.next())
                {
                    Long receiverID = resultSet2.getLong("id");
                    String receiverFirstName = resultSet2.getString("first_name");
                    String receiverLastName = resultSet2.getString("last_name");

                    if(!userID.equals(receiverID)) receivers.add(new User(receiverID, receiverFirstName + " " + receiverLastName));
                    else from = new User(receiverID, receiverFirstName + " " + receiverLastName);
                }

                Message message = new Message(messageID, from, receivers, messageText, date, reply);
                message.setGroupID(groupID);
                messageList.add(message);
            }

            return messageList;
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<Message> save(Message entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must be not null !");

        String insertSQL = "INSERT INTO messages (groupid, \"from\", message, date, reply)  VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL))
        {
            statement.setLong(1, entity.getGroupID());
            statement.setLong(2, entity.getFrom().getID());
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));
            if(entity.getReply() != null) statement.setLong(5, entity.getReply().getID());
            else statement.setNull(5, BIGINT);

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<Message> delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null !");

        String insertSQL = "DELETE FROM messages WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL))
        {
            statement.setLong(1, id);

            var entity = findOne(id);

            int response = statement.executeUpdate();
            return response == 0 ? entity : Optional.empty();

        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<Message> update(Message entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity must be not null !");

        String insertSQL = "UPDATE messages SET groupid = ?, \"from\" = ?, message = ?, date = ?, reply = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL))
        {

            var user = findOne(entity.getID());
            if(user.isEmpty()) return Optional.of(entity);

            statement.setLong(1, entity.getGroupID());
            statement.setLong(2, entity.getFrom().getID());
            statement.setString(3, entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));
            if(entity.getReply() != null) statement.setLong(5, entity.getReply().getID());
            else statement.setNull(5, BIGINT);


            statement.setLong(6, entity.getID());


            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }
}
