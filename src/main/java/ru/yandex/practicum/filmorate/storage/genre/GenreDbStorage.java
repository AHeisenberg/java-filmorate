package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {

    private static final String SQL_ADD_GENRE = "INSERT INTO genres(name) VALUES (?)";
    private static final String SQL_UPDATE_GENRE = "UPDATE genres SET name = ? WHERE genre_id = ?";
    private static final String SQL_GET_GENRES = "SELECT * FROM genres ORDER BY genre_id";
    private static final String SQL_GET_GENRE = "SELECT * FROM genres where genre_id = ?";
    private static final String SQL_DELETE_GENRE = "DELETE FROM genres where genre_id = ?";
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre addGenre(Genre genre) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_ADD_GENRE, new String[]{"genre_id"});
            stmt.setString(1, genre.getName());
            return stmt;
        }, keyHolder);
        genre.setId(keyHolder.getKey().intValue());
        return genre;
    }

    @Override
    public Optional<Genre> updateGenre(Genre genre) {
        boolean isUpdated = jdbcTemplate.update(SQL_UPDATE_GENRE, genre.getName(), genre.getId()) > 0;
        return isUpdated ? Optional.of(genre) : Optional.empty();
    }

    @Override
    public List<Genre> getAllGenre() {
        return jdbcTemplate.query(SQL_GET_GENRES, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getGenre(long id) {
        List<Genre> result = jdbcTemplate.query(SQL_GET_GENRE, this::mapRowToGenre, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean deleteGenre(long id) {
        return jdbcTemplate.update(SQL_DELETE_GENRE, id) > 0;
    }

    public Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
