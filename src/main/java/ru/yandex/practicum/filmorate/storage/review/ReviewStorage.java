package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Optional<Review> addReview(Review review);

    Optional<Review> editReview(Review review);

    Optional<Review> getReview(long id);

    void deleteReview(long id);

    Optional<Review> putLike(long reviewId, long userId);

    Optional<Review> putDislike(long reviewId, long userId);

    Optional<Review> deleteLike(long reviewId, long userId);

    Optional<Review> deleteDislike(long reviewId, long userId);

    List<Review> getReviewsOfFilm(Optional<Long> filmId, long count);
}
