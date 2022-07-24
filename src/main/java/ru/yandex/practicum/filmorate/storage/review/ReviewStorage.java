package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {

    Review addReview(Review review);

    Review editReview(Review review);

    Review getReview(long id);

    ResponseEntity deleteReview(long id);
}
