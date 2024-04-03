package scs.ubbcluj.ro.repository.DB;

import scs.ubbcluj.ro.domain.FriendRequest;
import scs.ubbcluj.ro.domain.User;
import scs.ubbcluj.ro.repository.FriendRequestRepository;
import scs.ubbcluj.ro.repository.Repository;
import scs.ubbcluj.ro.utils.FriendshipRequestStatus;
import scs.ubbcluj.ro.utils.Tuple;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestDBRepository extends FriendRequestRepository {

    private final String url;
    private final String username;
    private final String password;

    public FriendRequestDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> id) {
        if (id == null)
            throw new IllegalArgumentException("ID must be not null !");

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friend_requests WHERE \"from\" = ? AND \"to\" = ?");
            PreparedStatement userStatement = connection.prepareStatement("SELECT * FROM users WHERE id = ?"))
        {
            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }

            resultSet.next();

            Long from = resultSet.getLong("from");
            Long to = resultSet.getLong("to");
            LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
            String status = resultSet.getString("status");

            userStatement.setLong(1, from);
            ResultSet userResultSet = userStatement.executeQuery();

            if(!userResultSet.isBeforeFirst()) {
                return Optional.empty();
            }
            userResultSet.next();

            User userFrom = new User(from,
                    userResultSet.getString("first_name") + " "
                            + userResultSet.getString("last_name"));


            userStatement.setLong(1, to);
            userResultSet = userStatement.executeQuery();

            if(!userResultSet.isBeforeFirst()) {
                return Optional.empty();
            }
            userResultSet.next();

            User userTo = new User(to,
                    userResultSet.getString("first_name") + " "
                            + userResultSet.getString("last_name"));

            FriendshipRequestStatus friendshipRequestStatus = switch (status) {
                case "Approved" -> FriendshipRequestStatus.Approved;
                case "Rejected" -> FriendshipRequestStatus.Rejected;
                default -> FriendshipRequestStatus.Pending;
            };

            FriendRequest friendRequest = new FriendRequest(userFrom, userTo, date, friendshipRequestStatus);

            return Optional.of(friendRequest);
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Iterable<FriendRequest> findForOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friend_requests WHERE \"to\" = ? AND status = 'Pending'");
            PreparedStatement userStatement = connection.prepareStatement("SELECT * FROM users WHERE id = ?"))
        {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst()) {
                return new ArrayList<>();
            }

            List<FriendRequest> friendRequestList = new ArrayList<>();

            while(resultSet.next()) {
                Long from = resultSet.getLong("from");
                Long to = resultSet.getLong("to");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String status = resultSet.getString("status");

                userStatement.setLong(1, from);
                ResultSet userResultSet = userStatement.executeQuery();

                if (!userResultSet.isBeforeFirst()) {
                    continue;
                }
                userResultSet.next();

                User userFrom = new User(from,
                        userResultSet.getString("first_name") + " "
                                + userResultSet.getString("last_name"));


                userStatement.setLong(1, to);
                userResultSet = userStatement.executeQuery();

                if (!userResultSet.isBeforeFirst()) {
                    continue;
                }
                userResultSet.next();

                User userTo = new User(to,
                        userResultSet.getString("first_name") + " "
                                + userResultSet.getString("last_name"));

                FriendshipRequestStatus friendshipRequestStatus = switch (status.toLowerCase()) {
                    case "approved" -> FriendshipRequestStatus.Approved;
                    case "rejected" -> FriendshipRequestStatus.Rejected;
                    default -> FriendshipRequestStatus.Pending;
                };

                friendRequestList.add(new FriendRequest(userFrom, userTo, date, friendshipRequestStatus));
            }
            return friendRequestList;
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM friend_requests");
            PreparedStatement userStatement = connection.prepareStatement("SELECT * FROM users WHERE id = ?"))
        {
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.isBeforeFirst()) {
                return new ArrayList<>();
            }

            List<FriendRequest> friendRequestList = new ArrayList<>();

            while(resultSet.next()) {
                Long from = resultSet.getLong("from");
                Long to = resultSet.getLong("to");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String status = resultSet.getString("status");

                userStatement.setLong(1, from);
                ResultSet userResultSet = userStatement.executeQuery();

                if (!userResultSet.isBeforeFirst()) {
                    continue;
                }
                userResultSet.next();

                User userFrom = new User(from,
                        userResultSet.getString("first_name") + " "
                                + userResultSet.getString("last_name"));


                userStatement.setLong(1, to);
                userResultSet = userStatement.executeQuery();

                if (!userResultSet.isBeforeFirst()) {
                    continue;
                }
                userResultSet.next();

                User userTo = new User(to,
                        userResultSet.getString("first_name") + " "
                                + userResultSet.getString("last_name"));

                FriendshipRequestStatus friendshipRequestStatus = switch (status.toLowerCase()) {
                    case "approved" -> FriendshipRequestStatus.Approved;
                    case "rejected" -> FriendshipRequestStatus.Rejected;
                    default -> FriendshipRequestStatus.Pending;
                };

                friendRequestList.add(new FriendRequest(userFrom, userTo, date, friendshipRequestStatus));
            }
            return friendRequestList;
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {

        if(entity == null)
            throw new IllegalArgumentException("Entity must not be null");

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO friend_requests (\"from\", \"to\", date, status) VALUES (?, ?, ?, ?);"))
        {
            statement.setLong(1, entity.getFrom().getID());
            statement.setLong(2, entity.getTo().getID());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setString(4, entity.getStatus().toString());

            return statement.executeUpdate() == 0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> id) {
        if(id == null)
            throw new IllegalArgumentException("ID must not be null");

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("DELETE FROM friend_requests WHERE \"from\" = ? AND \"to\" = ?;"))
        {
            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());

            var entity = findOne(id);
            return statement.executeUpdate() == 0 ? entity : Optional.empty();
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }


    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity must not be null");

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("UPDATE friend_requests SET status = ? WHERE \"from\" = ? AND \"to\" = ?;"))
        {
            statement.setString(1, entity.getStatus().toString());
            statement.setLong(2, entity.getID().getLeft());
            statement.setLong(3, entity.getID().getRight());

            return statement.executeUpdate() == 0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException exception)
        {
            throw new RuntimeException(exception);
        }
    }
}
