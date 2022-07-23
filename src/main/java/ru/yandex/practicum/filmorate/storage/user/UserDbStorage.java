package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage implements UserStorage {
    private static final String SQL_ADD_USER = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String SQL_UPDATE_USER_FRIENDS_STATUS = "DELETE FROM friends_status WHERE to_user_id = ? OR from_user_id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String SQL_GET_USERS = "SELECT * FROM users";
    private static final String SQL_GET_USER = "SELECT * FROM users WHERE user_id = ?";
    private static final String SQL_GET_USER_FRIENDS = "SELECT * FROM friends_status f " +
            "LEFT JOIN users u ON f.to_user_id = u.user_id " +
            "WHERE from_user_id = ?";
    private static final String SQL_GET_USER_FRIENDS_MUTUAL = "SELECT * FROM friends_status f " +
            "LEFT JOIN users u ON f.from_user_id = u.user_id " +
            "WHERE to_user_id = ? AND accepted = ?";
    private static final String SQL_HAS_MUTUAL_CONNECTION = "SELECT * FROM friends_status WHERE (from_user_id = ? AND to_user_id = ? AND accepted = ?) " +
            "OR (from_user_id = ? AND to_user_id = ? AND accepted = ?) ";
    private static final String SQL_HAS_CONNECTION = "SELECT * FROM friends_status WHERE (from_user_id = ? AND to_user_id = ?) " +
            "OR (from_user_id = ? AND to_user_id = ?) ";
    private static final String SQL_ADD_CONNECTION = "INSERT INTO friends_status(to_user_id, from_user_id, accepted) " +
            "values (?, ?, ?)";
    private static final String SQL_DELETE_CONNECTION = "DELETE FROM friends_status WHERE (from_user_id = ? AND to_user_id = ?) OR " +
            "(from_user_id = ? AND to_user_id = ?)";
    private static final String SQL_ADD_FRIEND = "UPDATE friends_status SET accepted = ? WHERE (to_user_id = ? AND from_user_id = ?) " +
            "OR (to_user_id = ? AND from_user_id = ?)";
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_ADD_USER, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        long userId = keyHolder.getKey().longValue();
        user.setId(userId);
        return user;
    }

    @Override
    public Optional<User> updateUser(User user) {
        boolean isUpdated = jdbcTemplate.update(SQL_UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()) > 0;
        if (isUpdated) {
            jdbcTemplate.update(SQL_UPDATE_USER_FRIENDS_STATUS,
                    user.getId(), user.getId());
        }
        return isUpdated ? Optional.of(user) : Optional.empty();
    }

    @Override
    public boolean deleteUser(long id) {
        boolean isDeleted = jdbcTemplate.update(SQL_DELETE_USER, id) > 0;
        jdbcTemplate.update("DELETE FROM friends_status WHERE to_user_id = ? OR from_user_id = ?", id, id);
        return isDeleted;
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query(SQL_GET_USERS, this::mapRowToUser);
    }

    @Override
    public Optional<User> getUser(long id) {
        List<User> result = jdbcTemplate.query(SQL_GET_USER, this::mapRowToUser, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<User> getUserFriends(long id) {
        List<User> users = jdbcTemplate.query(SQL_GET_USER_FRIENDS, this::mapRowToUser, id);
        users.addAll(jdbcTemplate.query(SQL_GET_USER_FRIENDS_MUTUAL, this::mapRowToUser, id, true));
        return users;
    }

    private boolean hasMutualConnection(long id, long friendId) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(SQL_HAS_MUTUAL_CONNECTION, id, friendId,
                true, friendId, id, true);
        return sqlRowSet.next();
    }

    private boolean hasConnection(long id, long friendId) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(SQL_HAS_CONNECTION, id, friendId, friendId, id);
        return sqlRowSet.next();
    }

    private boolean addConnection(long id, long friendId) {
        return jdbcTemplate.update(SQL_ADD_CONNECTION, friendId, id, false) > 0;
    }

    private boolean deleteConnection(long id, long friendId) {
        return jdbcTemplate.update(SQL_DELETE_CONNECTION, id, friendId, friendId, id) > 0;
    }

    @Override
    public boolean addFriend(long id, long friendId) {
        if (hasConnection(id, friendId)) {
            return jdbcTemplate.update(SQL_ADD_FRIEND, true, id, friendId, friendId, id) > 0;
        } else {
            return addConnection(id, friendId);
        }
    }

    @Override
    public boolean removeFriend(long id, long friendId) {
        boolean isMutual = hasMutualConnection(id, friendId);
        boolean isDeleted = deleteConnection(id, friendId);
        if (isMutual) {
            addConnection(friendId, id);
        }
        return isDeleted;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {

        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}