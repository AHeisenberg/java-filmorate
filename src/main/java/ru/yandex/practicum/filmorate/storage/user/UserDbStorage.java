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
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users(email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
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
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE user_id = ?";
        boolean isUpdated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()) > 0;
        if (isUpdated) {
            jdbcTemplate.update("DELETE FROM friends_status WHERE to_user_id = ? OR from_user_id = ?",
                    user.getId(), user.getId());
        }
        return isUpdated ? Optional.of(user) : Optional.empty();
    }

    @Override
    public boolean deleteUser(long id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        boolean isDeleted = jdbcTemplate.update(sql, id) > 0;
        jdbcTemplate.update("DELETE FROM friends_status WHERE to_user_id = ? OR from_user_id = ?", id, id);
        return isDeleted;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public Optional<User> getUser(long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> result = jdbcTemplate.query(sql, this::mapRowToUser, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<User> getUserFriends(long id) {
        String sql = "SELECT * FROM friends_status f " +
                "LEFT JOIN users u ON f.to_user_id = u.user_id " +
                "WHERE from_user_id = ?";

        String sqlQueryMutual = "SELECT * FROM friends_status f " +
                "LEFT JOIN users u ON f.from_user_id = u.user_id " +
                "WHERE to_user_id = ? AND accepted = ?";

        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, id);
        users.addAll(jdbcTemplate.query(sqlQueryMutual, this::mapRowToUser, id, true));
        return users;

    }

    private boolean hasMutualConnection(long id, long friendId) {
        String sql = "SELECT * FROM friends_status WHERE (from_user_id = ? AND to_user_id = ? AND accepted = ?) " +
                "OR (from_user_id = ? AND to_user_id = ? AND accepted = ?) ";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id, friendId, true, friendId, id, true);
        return sqlRowSet.next();
    }

    private boolean hasConnection(long id, long friendId) {
        String sql = "SELECT * FROM friends_status WHERE (from_user_id = ? AND to_user_id = ?) " +
                "OR (from_user_id = ? AND to_user_id = ?) ";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id, friendId, friendId, id);
        return sqlRowSet.next();
    }

    private boolean addConnection(long id, long friendId) {
        String sql = "INSERT INTO friends_status(to_user_id, from_user_id, accepted) " +
                "values (?, ?, ?)";
        return jdbcTemplate.update(sql, friendId, id, false) > 0;
    }

    private boolean deleteConnection(long id, long friendId) {
        String sql = "DELETE FROM friends_status WHERE (from_user_id = ? AND to_user_id = ?) OR " +
                "(from_user_id = ? AND to_user_id = ?)";
        return jdbcTemplate.update(sql, id, friendId, friendId, id) > 0;
    }

    @Override
    public boolean addFriend(long id, long friendId) {
        if (hasConnection(id, friendId)) {
            String sql = "UPDATE friends_status SET accepted = ? WHERE (to_user_id = ? AND from_user_id = ?) " +
                    "OR (to_user_id = ? AND from_user_id = ?)";
            return jdbcTemplate.update(sql, true, id, friendId, friendId, id) > 0;
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