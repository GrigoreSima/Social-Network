package scs.ubbcluj.ro.repository.DB;

import scs.ubbcluj.ro.domain.Friendship;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.utils.Tuple;
import scs.ubbcluj.ro.utils.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.lang.Long.max;
import static java.lang.Long.min;

public class FriendshipDBRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private final String url;
    private final String username;
    private final String password;

    public FriendshipDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> tuple) {
        if (tuple.getLeft() == null || tuple.getRight() == null)
            throw new IllegalArgumentException("ID must be not null !");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE user_id1 = ? and user_id2 = ?");)
        {
            statement.setLong(1, min(tuple.getLeft(), tuple.getRight()));
            statement.setLong(2, max(tuple.getLeft(), tuple.getRight()));

            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst())
                return Optional.empty();

            resultSet.next();

            Long user_id1 = resultSet.getLong("user_id1");
            Long user_id2 = resultSet.getLong("user_id2");
            LocalDateTime datetime = resultSet.getTimestamp("friends_from").toLocalDateTime();
            Friendship friendship = new Friendship(new Tuple<>(user_id1, user_id2), datetime);

            return Optional.of(friendship);
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }


    @Override
    public Iterable<Friendship> findAll() {

        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery();)
        {
            while (resultSet.next())
            {
                Long user_id1 = resultSet.getLong("user_id1");
                Long user_id2 = resultSet.getLong("user_id2");
                LocalDateTime datetime = resultSet.getTimestamp("friends_from").toLocalDateTime();
                Friendship friendship = new Friendship(new Tuple<>(user_id1, user_id2), datetime);
                friendships.add(friendship);

            }
            return friendships;
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must be not null !");

        String insertSQL = "INSERT INTO friendships (user_id1, user_id2, friends_from) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL);)
        {
            statement.setLong(1, min(entity.getID().getLeft(), entity.getID().getRight()));
            statement.setLong(2, max(entity.getID().getLeft(), entity.getID().getRight()));
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDatetime()));

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> tuple) {
        if (tuple.getLeft() == null || tuple.getRight() == null)
            throw new IllegalArgumentException("ID must be not null !");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE user_id1 = ? and user_id2 = ?");)
        {
            statement.setLong(1, min(tuple.getLeft(), tuple.getRight()));
            statement.setLong(2, max(tuple.getLeft(), tuple.getRight()));

            var entity = findOne(tuple);

            int response = statement.executeUpdate();
            return response == 0 ? entity : Optional.empty();

        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity must be not null !");

        String insertSQL = "UPDATE friendships SET friends_from = ? WHERE user_id1 = ? and user_id2 = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL);)
        {

            var friendship = findOne(entity.getID());
            if(friendship.isEmpty()) return Optional.of(entity);

            statement.setTimestamp(1, Timestamp.valueOf(entity.getDatetime()));
            statement.setLong(2, min(entity.getID().getLeft(), entity.getID().getRight()));
            statement.setLong(3, max(entity.getID().getLeft(), entity.getID().getRight()));

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }
}
