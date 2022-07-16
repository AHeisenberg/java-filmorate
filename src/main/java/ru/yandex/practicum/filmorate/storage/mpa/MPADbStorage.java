package ru.yandex.practicum.filmorate.storage.mpa;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class MPADbStorage implements MPAStorage {

    private final JdbcTemplate jdbcTemplate;

    public MPADbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RatingMPA addRatingMPA(RatingMPA mpa) {
        String sql = "INSERT INTO ratings_mpa(name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"rating_id"});
            stmt.setString(1, mpa.getName());
            return stmt;
        }, keyHolder);
        mpa.setId(keyHolder.getKey().intValue());
        return mpa;
    }

    @Override
    public Optional<RatingMPA> updateRatingMPA(RatingMPA mpa) {
        String sql = "UPDATE ratings_mpa SET name = ? WHERE rating_id = ?";
        boolean isUpdated = jdbcTemplate.update(sql, mpa.getName(), mpa.getId()) > 0;
        return isUpdated ? Optional.of(mpa) : Optional.empty();
    }

    @Override
    public List<RatingMPA> getAllRatings() {
        String sql = "SELECT * FROM ratings_mpa ORDER BY rating_id";
        return jdbcTemplate.query(sql, this::mapRowToMPA);
    }

    @Override
    public Optional<RatingMPA> getRatingMPA(long id) {
        String sql = "SELECT * FROM ratings_mpa WHERE rating_id = ?";
        List<RatingMPA> result = jdbcTemplate.query(sql, this::mapRowToMPA, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean deleteRatingMPA(long id) {
        String sql = "DELETE FROM ratings_mpa where rating_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    private RatingMPA mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        return RatingMPA.builder()
                .id(resultSet.getInt("rating_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
