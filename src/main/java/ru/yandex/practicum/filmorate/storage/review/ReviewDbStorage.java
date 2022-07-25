package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ReviewDbStorage implements ReviewStorage {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SQL_ADD_REVIEW = "INSERT INTO reviews (content, is_positive, to_user_id, to_film_id) " +
            "VALUES (?, ?, ?, ?)";

    private static final String SQL_EDIT_REVIEW = "UPDATE reviews SET content=?, is_positive=?, to_user_id=?, " +
            "to_film_id=? WHERE review_id=?";

    private static final String SQL_COUNT_LIKES_REVIEW = "SELECT COUNT(user_id) FROM users_likes_reviews WHERE review_id=? AND is_like=true";

    private static final String SQL_COUNT_DISLIKES_REVIEW = "SELECT COUNT(user_id) FROM users_likes_reviews WHERE review_id=? AND is_like=false";

    private static final String SQL_GET_REVIEW = "SELECT * FROM reviews WHERE review_id=?";

    private static final String SQL_GET_ALL_REVIEWS = "SELECT * FROM reviews";

    private static final String SQL_UPDATE_LIKES_IN_REVIEWS = "UPDATE reviews SET useful=? WHERE review_id=?";

    private static final String SQL_DELETE_REVIEW = "DELETE FROM reviews WHERE review_id=?";

    private static final String SQL_DELETE_ALL_LIKES_DISLIKES = "DELETE FROM users_likes_reviews WHERE review_id=?";

    private static final String SQL_LIKE_REVIEW = "INSERT INTO users_likes_reviews (review_id, user_id, is_like)" +
            "VALUES (?, ?, ?)";

    private static final String SQL_DELETE_LIKE = "DELETE FROM users_likes_reviews WHERE review_id=? AND user_id=? AND is_like=true";

    private static final String SQL_DELETE_DISLIKE = "DELETE FROM users_likes_reviews WHERE review_id=? AND user_id=? AND is_like=false";

    private static final String SQL_GET_REVIEWS_OF_FILM = "SELECT * FROM reviews WHERE to_film_id=?";


    @Override
    public Review addReview(Review review) {
        jdbcTemplate.update(SQL_ADD_REVIEW, review.getContent(), review.isPositive(),
                review.getUserId(), review.getFilmId()); //TODO лайки через JSON
        return review;
    }

    @Override
    public Review editReview(Review review) {
        jdbcTemplate.update(SQL_EDIT_REVIEW, review.getContent(), review.isPositive(),
                review.getUserId(), review.getFilmId(), review.getId()); //TODO лайки
        return review;
    }

    @Override
    public Optional<Review> getReview(long id) {
        List<Review> result = jdbcTemplate.query(SQL_GET_REVIEW, this::mapRowToReview, id); //TODO лайки
        return Optional.of(result.get(0));
    }

    @Override
    public void deleteReview(long id) {
        jdbcTemplate.update(SQL_DELETE_ALL_LIKES_DISLIKES, id);
        jdbcTemplate.update(SQL_DELETE_REVIEW, id); //TODO и снести таблицу с лайками
    }

    @Override
    public Optional<Review> putLike(long reviewId, long userId) {
        jdbcTemplate.update(SQL_LIKE_REVIEW, reviewId, userId, true);

        updateAmountOfLikes(reviewId);
        return getReview(reviewId);
    }

    @Override
    public Optional<Review> putDislike(long reviewId, long userId) {
        jdbcTemplate.update(SQL_LIKE_REVIEW, reviewId, userId, false);

        updateAmountOfLikes(reviewId);
        return getReview(reviewId);
    }

    @Override
    public Optional<Review> deleteLike(long reviewId, long userId) {
        jdbcTemplate.update(SQL_DELETE_LIKE, reviewId, userId);
        updateAmountOfLikes(reviewId);
        return getReview(reviewId);
    }
    @Override
    public Optional<Review> deleteDislike(long reviewId, long userId) {
        jdbcTemplate.update(SQL_DELETE_DISLIKE, reviewId, userId);
        updateAmountOfLikes(reviewId);
        return getReview(reviewId);
    }

    @Override
    public List<Review> getReviewsOfFilm(Optional<Long> filmId, long count) {
        List<Review> listOfReviews;

        if (filmId.isPresent()) {
            List<Map<String, Object>> listOfMaps = jdbcTemplate.queryForList(SQL_GET_REVIEWS_OF_FILM, filmId);
            listOfReviews = mapToListOfReviews(listOfMaps);
        } else {
            List<Map<String, Object>> listOfMaps = jdbcTemplate.queryForList(SQL_GET_ALL_REVIEWS);
            listOfReviews = mapToListOfReviews(listOfMaps);
        }
        listOfReviews = listOfReviews
                .stream()
                .sorted(Comparator.comparing(Review::getUseful))
                .limit(count)
                .collect(Collectors.toList());

        return listOfReviews;
    }

    private void updateAmountOfLikes(long reviewId) {
        long likes = jdbcTemplate.queryForObject(SQL_COUNT_LIKES_REVIEW, Long.class, reviewId);
        long dislikes = jdbcTemplate.queryForObject(SQL_COUNT_DISLIKES_REVIEW, Long.class, reviewId);
        jdbcTemplate.update(SQL_UPDATE_LIKES_IN_REVIEWS, likes - dislikes, reviewId);
    }

    private  List<Review> mapToListOfReviews(List<Map<String, Object>> listOfMaps) {
        List<Review> listOfReviews = new ArrayList<>();

        for (Map<String, Object> map : listOfMaps) {

            listOfReviews.add(Review.builder()
                    .id((Long) map.get("review_id"))
                    .content((String) map.get("content"))
                    .isPositive((Boolean) map.get("is_positive"))
                    .filmId((Long) map.get("to_film_id"))
                    .userId((Long) map.get("to_user_id"))
                    .useful((Long) map.get("useful"))
                    .build());
        }
        return listOfReviews;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {

        return Review.builder()
                .id(resultSet.getLong("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .filmId(resultSet.getLong("to_film_id"))
                .userId(resultSet.getLong("to_user_id"))
                .useful(resultSet.getLong("useful"))  //TODO счетчик лайков доделать
                .build();

    }


}