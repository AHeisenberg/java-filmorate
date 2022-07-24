package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

@Service
public class ReviewService {
    ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }


    public Review addReview(Review review) {
       return reviewStorage.addReview(review);
    }

    public Review editReview(Review review) {
        return reviewStorage.editReview(review);
    }

    public Review getReview(long id) {
       return reviewStorage.getReview(id);
    }

    public ResponseEntity deleteReview(long id) {
        return new ResponseEntity<>(reviewStorage.delete(id));
    }

}
