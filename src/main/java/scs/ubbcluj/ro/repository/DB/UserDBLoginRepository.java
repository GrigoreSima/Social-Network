package scs.ubbcluj.ro.repository.DB;

import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.utils.validators.ValidationException;
import scs.ubbcluj.ro.utils.validators.Validator;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;

public class UserDBLoginRepository extends UserDBRepository{

    private final SecretKey key;
    private final String keyValue = "U2m_=?@2dac2;1-]'?";
    private final String cryptingAlgorithm = "Blowfish";

    public UserDBLoginRepository(String url, String username, String password, Validator<User> validator) {
        super(url, username, password, validator);
        key = new SecretKeySpec(keyValue.getBytes(), cryptingAlgorithm);
    }

    public Optional<User> findOne(String username, String password) throws IllegalArgumentException {
        try (Connection connection = DriverManager.getConnection(super.getUrl(), super.getUsername(), super.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? and password = ?");
             PreparedStatement friendsStatement = connection.prepareStatement(
                     """
                                     
                                             SELECT u.*
                                     FROM friendships f JOIN users u ON f.user_id2 = u.id
                                     WHERE f.user_id1 = ?
                                     UNION
                                     SELECT u.*
                                     FROM friendships f JOIN users u ON f.user_id1 = u.id
                                     WHERE f.user_id2 = ?
                             """))
        {
            statement.setString(1, username);

            Cipher cipher = Cipher.getInstance(cryptingAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            byte[] pass =  cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

            statement.setBytes(2, pass);

            // Gets the user
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.isBeforeFirst())
                return Optional.empty();

            resultSet.next();

            Long id = resultSet.getLong("id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            User user = new User(firstName + " " + lastName);
            user.setID(id);

            friendsStatement.setLong(1, id);
            friendsStatement.setLong(2, id);

            // Gets user's friends
            resultSet = friendsStatement.executeQuery();
            while (resultSet.next()) {
                Long friendID = resultSet.getLong("id");
                String friendFirstName = resultSet.getString("first_name");
                String friendLastName = resultSet.getString("last_name");
                user.addFriend(new User(friendID, friendFirstName + " " + friendLastName));
            }

            return Optional.of(user);
        } catch (
                SQLException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                BadPaddingException | InvalidKeyException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Optional<User> save(User entity, String username, String password) throws IllegalArgumentException, ValidationException {

        if (entity == null)
            throw new IllegalArgumentException("Entity must be not null !");

        super.getValidator().validate(entity);

        String insertSQL = "INSERT INTO users(first_name, last_name, username, password) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(super.getUrl(), super.getUsername(), super.getPassword());
             PreparedStatement statement = connection.prepareStatement(insertSQL);)
        {
            statement.setString(2, entity.getName().get(entity.getName().size()-1));

            StringBuilder first_name = new StringBuilder();
            for (String s : entity.getName()) {
                if(!Objects.equals(s, entity.getName().get(entity.getName().size()-1)))
                    first_name.append(s).append(" ");
            }

            statement.setString(1, first_name.toString().strip());

            statement.setString(3, username);

            Cipher cipher = Cipher.getInstance(cryptingAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            byte[] pass =  cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            statement.setBytes(4, pass);



            int response = statement.executeUpdate();
            return response == 0 ? Optional.of(entity) : Optional.empty();

        } catch (
                SQLException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                BadPaddingException | InvalidKeyException exception) {
            throw new RuntimeException(exception);
        }

    }

    public SecretKey getKey() {
        return key;
    }
}