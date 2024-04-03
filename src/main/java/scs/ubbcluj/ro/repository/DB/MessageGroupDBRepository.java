package scs.ubbcluj.ro.repository.DB;

import scs.ubbcluj.ro.domain.Message;
import scs.ubbcluj.ro.domain.MessageGroup;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageGroupDBRepository implements Repository<Long, MessageGroup> {

    private final String url;
    private final String username;
    private final String password;

    public MessageGroupDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<MessageGroup> findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null !");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM message_groups WHERE id = ?"))
        {
            statement.setLong(1, id);

            // Gets the message
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }

            resultSet.next();

            long groupID = resultSet.getLong("id");
            String groupName = resultSet.getString("group_name");

            MessageGroup group = new MessageGroup(groupID, groupName);

            return Optional.of(group);
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Iterable<MessageGroup> findAll() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM message_groups"))
        {
            ResultSet resultSet = statement.executeQuery();

            List<MessageGroup> list = new ArrayList<>();
            while(resultSet.next()) {
                long groupID = resultSet.getLong("id");
                String groupName = resultSet.getString("group_name");

                list.add(new MessageGroup(groupID, groupName));
            }

            return list;
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<MessageGroup> save(MessageGroup entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO message_groups (group_name) VALUES (?);"))
        {
            statement.setString(1, entity.getGroupName());

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<MessageGroup> delete(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM message_groups WHERE id = ?;"))
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
    public Optional<MessageGroup> update(MessageGroup entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE message_groups SET group_name = ? WHERE id = ?;"))
        {
            statement.setString(1, entity.getGroupName());
            statement.setLong(2, entity.getID());

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }
}
