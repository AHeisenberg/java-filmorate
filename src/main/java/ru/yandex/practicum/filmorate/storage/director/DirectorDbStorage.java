package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDbStorage implements DirectorStorage {

    private static final String SQL_ADD_DIRECTOR = "INSERT INTO directors (director_name) VALUES (?)";
    private static final String SQL_UPDATE_DIRECTOR = "UPDATE directors SET director_name = ? WHERE director_id = ?";
    private static final String SQL_GET_ALL_DIRECTORS = "SELECT * FROM directors ORDER BY director_id";
    private static final String SQL_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?";
    private static final String SQL_DELETE_DIRECTOR = "DELETE FROM directors where director_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director addDirector(Director director) {
        checkValidDirectorName(director.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(SQL_ADD_DIRECTOR, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    @Override
    public Optional<Director> updateDirector(Director director) {
        checkValidDirectorName(director.getName());
        boolean isUpdated = jdbcTemplate.update(SQL_UPDATE_DIRECTOR, director.getName(), director.getId()) > 0;
        return isUpdated ? Optional.of(director) : Optional.empty();
    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query(SQL_GET_ALL_DIRECTORS, this::mapRowToDirector);
    }

    @Override
    public Optional<Director> getDirector(long id) {
        List<Director> result = jdbcTemplate.query(SQL_DIRECTOR_BY_ID, this::mapRowToDirector, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean deleteDirector(long id) {
        return jdbcTemplate.update(SQL_DELETE_DIRECTOR, id) > 0;
    }

    private static void checkValidDirectorName(String name) {
        if (name.isBlank()) {
            throw new ValidationException("Name is not correct");
        }
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {

        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }
}
