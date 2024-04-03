package scs.ubbcluj.ro.repository.DB;

import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.utils.validators.ValidationException;
import scs.ubbcluj.ro.utils.validators.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class UserDBRepository implements Repository<Long, User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<User> findOne(Long id) throws IllegalArgumentException {

        if (id == null)
            throw new IllegalArgumentException("ID must be not null !");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
             PreparedStatement friendsStatement = connection.prepareStatement(
                     """
                             SELECT u.*
                             FROM friendships f JOIN users u ON f.user_id2 = u.id
                             WHERE f.user_id1 = ?
                             UNION
                             SELECT u.*
                             FROM friendships f JOIN users u ON f.user_id1 = u.id
                             WHERE f.user_id2 = ?
                         """);)
        {
            statement.setLong(1, id);
            friendsStatement.setLong(1, id);
            friendsStatement.setLong(2, id);

            // Gets the user
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst())
                return Optional.empty();

            resultSet.next();

            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            User user = new User(firstName + " " + lastName);
            user.setID(id);

            // Gets user's friends
            resultSet = friendsStatement.executeQuery();
            while (resultSet.next())
            {
                Long friendID = resultSet.getLong("id");
                String friendFirstName = resultSet.getString("first_name");
                String friendLastName = resultSet.getString("last_name");
                user.addFriend(new User(friendID, friendFirstName + " " + friendLastName));
            }

            return Optional.of(user);
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public Iterable<User> findAll() {

        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery();)
        {
            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(firstName + " " + lastName);
                user.setID(id);
                users.add(user);

            }
            return users;
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public Optional<User> save(User entity) throws IllegalArgumentException, ValidationException {

        if (entity == null)
            throw new IllegalArgumentException("Entity must be not null !");

        validator.validate(entity);

        String insertSQL = "INSERT INTO users(first_name, last_name) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL);)
        {
            statement.setString(2, entity.getName().get(entity.getName().size()-1));

            StringBuilder first_name = new StringBuilder();
            for (String s : entity.getName()) {
                if(!Objects.equals(s, entity.getName().get(entity.getName().size()-1)))
                    first_name.append(s).append(" ");
            }

            statement.setString(1, first_name.toString().strip());

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public Optional<User> delete(Long id) {

        if (id == null)
            throw new IllegalArgumentException("ID must be not null !");

        String insertSQL = "DELETE FROM users WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL);)
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
    public Optional<User> update(User entity) throws IllegalArgumentException, ValidationException {
        if(entity == null)
            throw new IllegalArgumentException("Entity must be not null !");

        validator.validate(entity);

        String insertSQL = "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(insertSQL);)
        {

            var user = findOne(entity.getID());
            if(user.isEmpty()) return Optional.of(entity);

            StringBuilder first_name = new StringBuilder();
            for (String s : entity.getName()) {
                if(!Objects.equals(s, entity.getName().get(entity.getName().size()-1)))
                    first_name.append(s).append(" ");
            }
            statement.setString(1, first_name.toString().strip());
            statement.setString(2, entity.getName().get(entity.getName().size()-1));
            statement.setLong(3, entity.getID());

            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    protected String getUrl() {
        return url;
    }

    protected String getUsername() {
        return username;
    }

    protected String getPassword() {
        return password;
    }

    protected Validator<User> getValidator() {
        return validator;
    }
}
