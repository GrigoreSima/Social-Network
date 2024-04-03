package scs.ubbcluj.ro.repository.DB;

import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.Paging.Page;
import scs.ubbcluj.ro.repository.Paging.PageImplementation;
import scs.ubbcluj.ro.repository.Paging.Pageable;
import scs.ubbcluj.ro.repository.Paging.PagingRepository;
import scs.ubbcluj.ro.utils.validators.Validator;

import java.sql.*;
import java.util.*;

public class UserDBPagingRepository extends UserDBRepository implements PagingRepository<Long, User> {

    public UserDBPagingRepository(String url, String username, String password, Validator<User> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(super.getUrl(), super.getUsername(), super.getPassword());
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users ORDER BY id OFFSET ? LIMIT ? "))
        {
            statement.setInt(1, pageable.getPageNumber() * pageable.getPageSize());
            statement.setInt(2, pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(firstName + " " + lastName);
                user.setID(id);
                users.add(user);

            }
            return new PageImplementation<>(pageable, users.stream());
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }
}
