package ru.yandex.practicum.filmorate.storage.review;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ReviewDbStorage implements ReviewStorage{

    JdbcTemplate jdbcTemplate;

    @Autowired
   public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate =jdbcTemplate;
    }

    private static final String SQL_ADD_REVIEW = "INSERT INTO reviews(content, is_positive, to_user_id, to_film_id, useful) " +
            "values (?, ?, ?, ?, ?)";

    private static final String SQL_EDIT_REVIEW = "UPDATE reviews SET content=?, is_positive=?, to_user_id=?, to_film_id=?, useful=?" +
            "WHERE review_id=?";

    private static final String SQL_GET_REVIEW = "SELECT * FROM reviews WHERE review_id=?";

    private static final String SQL_DELETE_REVIEW = "DELETE FROM reviews WHERE review_id=?";

    @Override
    public Review addReview(Review review) {
        jdbcTemplate.update(SQL_ADD_REVIEW, review.getContent(), review.isPositive(),
                review.getUserId(), review.getFilmId(), review.getUseful());
        return review;
    }

    @Override
    public Review editReview(Review review) {
        jdbcTemplate.update(SQL_EDIT_REVIEW, review.getContent(), review.isPositive(),
                review.getUserId(), review.getFilmId(), review.getUseful(), review.getId());
        return review;
    }

    @Override
    public Review getReview(long id) {
        List<Review> result = jdbcTemplate.query(SQL_GET_REVIEW, this::mapRowToReview, id);
        return result.get(0);
    }

    @Override
    public ResponseEntity deleteReview(long id) {
        jdbcTemplate.update(SQL_DELETE_REVIEW, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {

        return Review.builder()
                .id(resultSet.getLong("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .filmId(resultSet.getLong("to_film_id"))
                .userId(resultSet.getLong("to_user_id"))
                .useful(resultSet.getLong("useful"))
                .build();

    }


}
